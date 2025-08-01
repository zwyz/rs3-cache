package rs3.js5;

import rs3.util.Packet;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TcpJs5ResourceProvider implements Js5ResourceProvider, AutoCloseable {
    private static final int BLOCK_SIZE = 100 * 1024 - 5;
    private static final int MAX_PENDING_REQUESTS = 500;
    private final String host;
    private final int port;
    private final int version;
    private final int subversion;
    private final String token;
    private final int language;
    private Socket socket;
    private final Queue<GroupRequest> unsentRequests = new LinkedBlockingQueue<>();
    private final Map<ArchiveGroup, GroupRequest> sentRequests = new ConcurrentHashMap<>();
    private boolean shutdownRequested = false;
    private final ReentrantReadWriteLock shutdownRequestedLock = new ReentrantReadWriteLock();
    private long lastResponseTime = Long.MAX_VALUE;

    public TcpJs5ResourceProvider(String host, int port, String token, int version, int subversion, int language) {
        this.host = host;
        this.port = port;
        this.token = token;
        this.version = version;
        this.subversion = subversion;
        this.language = language;

        Thread.ofPlatform().daemon().start(() -> {
            try {
                processRequests();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static TcpJs5ResourceProvider create(String host, int port, String token, int version, int subversion, int language) {
        return new TcpJs5ResourceProvider(host, port, token, version, subversion, language);
    }

    public void processRequests() throws IOException, InterruptedException {
        connect();

        while (true) {
            if (lastResponseTime < System.currentTimeMillis() - 30000 && (!sentRequests.isEmpty() || !unsentRequests.isEmpty())) {
                lastResponseTime = System.currentTimeMillis();

                for (var request : sentRequests.values()) {
                    request.buffer = null;
                    unsentRequests.add(request);
                }

                sentRequests.clear();
                socket.close();
                connect();
            }

            while (sentRequests.size() < MAX_PENDING_REQUESTS && !unsentRequests.isEmpty()) {
                var request = unsentRequests.poll();
                sentRequests.put(new ArchiveGroup(request.archive, request.group), request);

                if (!request.urgent) {
                    sendRequestPrefetch(request.archive, request.group, request.priority);
                } else {
                    sendRequestUrgent(request.archive, request.group, request.priority);
                }
            }

            if (socket.getInputStream().available() > 0) {
                lastResponseTime = System.currentTimeMillis();
                handleResponse();
            }

            Thread.sleep(1);
        }
    }

    private void connect() throws IOException {
        socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        socket.setReceiveBufferSize(10_000_000);

        var packet = Packet.create(1 + 1 + 4 + 4 + token.length() + 1 + 1);
        packet.p1(15);
        packet.p1(9 + token.length() + 1);
        packet.p4(version);
        packet.p4(subversion);
        packet.pjstr(token);
        packet.p1(language);
        send(packet);

        var status = receive(1).g1();

        if (status != 0) {
            // 6 = client out of date
            // 48 = wrong param29
            // 252 = not a content server
            throw new IOException("failed to connect " + status);
        }

        sendConnected(5, version);
        sendLoggedOut();
    }

    private void sendRequestPrefetch(int archive, int group, int priority) throws IOException {
        var request = Packet.create(10);
        request.clear();
        request.p1(0 + (priority << 4));
        request.p1(archive);
        request.p4(group);
        send(request);
    }

    private void sendRequestUrgent(int archive, int group, int priority) throws IOException {
        var request = Packet.create(10);
        request.p1(1 + (priority << 4));
        request.p1(archive);
        request.p4(group);
        send(request);
    }

    private void sendLoggedIn() throws IOException {
        var request = Packet.create(10);
        request.p1(2);
        send(request);
    }

    private void sendLoggedOut() throws IOException {
        var request = Packet.create(10);
        request.p1(3);
        send(request);
    }

    private void sendEnableXor(int key) throws IOException {
        var request = Packet.create(10);
        request.p1(4);
        request.p1(key);
        send(request);
    }

    private void sendConnected(int unknown, int version) throws IOException {
        var request = Packet.create(10);
        request.p1(6);
        request.p3(unknown);
        request.p4(version);
        send(request);
    }

    private void sendUnknown7() throws IOException {
        var request = Packet.create(10);
        request.p1(7);
        send(request);
    }

    public void handleResponse() throws IOException {
        var packet = receive(5);
        var archive = packet.g1();
        var group = packet.g4s() & 0x7fffffff;

        var response = sentRequests.get(new ArchiveGroup(archive, group));

        if (response == null) {
            throw new IOException("received a group that wasn't asked for: archive = " + archive + " group = " + group);
        }

        if (response.buffer == null) {
            var initialData = receive(5);
            var compressionType = initialData.g1();
            var compressedSize = initialData.g4s();

            response.buffer = ByteBuffer.allocate(compressedSize + (compressionType == 0 ? 5 : 9));
            response.buffer.put((byte) compressionType);
            response.buffer.putInt(compressedSize);
        }

        response.buffer.put(receive(Math.min(response.buffer.remaining(), BLOCK_SIZE - response.buffer.position() % BLOCK_SIZE)).arr);

        if (response.buffer.remaining() == 0) {
            sentRequests.remove(new ArchiveGroup(archive, group));
            response.future.complete(response.buffer.array());
        }
    }

    private void send(Packet request) throws IOException {
        socket.getOutputStream().write(request.arr, 0, request.arr.length);
        socket.getOutputStream().flush();
    }

    private Packet receive(int size) throws IOException {
        var response = socket.getInputStream().readNBytes(size);

        if (response.length < size) {
            throw new IOException("end of stream");
        }

        return new Packet(response);
    }

    @Override
    public byte[] get(int archive, int group, boolean urgent, int priority) {
        var future = getAsync(archive, group, urgent, priority);

        try {
            return future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<byte[]> getAsync(int archive, int group, boolean urgent, int priority) {
        shutdownRequestedLock.readLock().lock();

        try {
            if (shutdownRequested) {
                return CompletableFuture.failedFuture(new IOException("resource provider has been shutdown"));
            } else {
                var future = new CompletableFuture<byte[]>();
                unsentRequests.add(new GroupRequest(archive, group, urgent, future, priority));
                return future;
            }
        } finally {
            shutdownRequestedLock.readLock().unlock();
        }
    }

    public void close() {
        shutdownRequestedLock.writeLock().lock();

        try {
            shutdownRequested = true;
        } finally {
            shutdownRequestedLock.writeLock().unlock();
        }
    }

    private static class GroupRequest {
        private final int archive;
        private final int group;
        private final CompletableFuture<byte[]> future;
        private final boolean urgent;
        private final int priority;
        private ByteBuffer buffer;

        public GroupRequest(int archive, int group, boolean urgent, CompletableFuture<byte[]> future, int priority) {
            this.archive = archive;
            this.group = group;
            this.future = future;
            this.urgent = urgent;
            this.priority = priority;
        }
    }

    private record ArchiveGroup(int archive, int group) {

    }
}
