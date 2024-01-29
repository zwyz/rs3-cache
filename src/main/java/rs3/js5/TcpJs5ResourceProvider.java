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

// 0 = request normal
// 1 = request urgent
// 2 = logged in
// 3 = logged out
// 4 = set xor byte
// 5 =
// 6 = connected (rs3)
// 17 = request ???
// 32 = request ???
// 33 = request ???

public class TcpJs5ResourceProvider implements Js5ResourceProvider, AutoCloseable {
    private static final int BLOCK_SIZE = 100 * 1024 - 5;
    private static final int MAX_PENDING_REQUESTS = 500;
    private final String host;
    private final int port;
    private final int version;
    private final int subversion;
    private final String token;
    private final int language;
    private final int unknown;
    private Socket socket;
    private final Queue<GroupRequest> unsentRequests = new LinkedBlockingQueue<>();
    private final Map<ArchiveGroup, GroupRequest> responses = new ConcurrentHashMap<>();
    private boolean connected;
    private boolean shutdownRequested = false;
    private final ReentrantReadWriteLock shutdownRequestedLock = new ReentrantReadWriteLock();

    public TcpJs5ResourceProvider(String host, int port, String token, int version, int subversion, int language, int unknown) {
        this.host = host;
        this.port = port;
        this.token = token;
        this.version = version;
        this.subversion = subversion;
        this.language = language;
        this.unknown = unknown;

        Thread.ofPlatform().start(() -> {
            try {
                processRequests();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static TcpJs5ResourceProvider create(String host, int port, String token, int version, int subversion, int language, int unknown) {
        return new TcpJs5ResourceProvider(host, port, token, version, subversion, language, unknown);
    }

    public void processRequests() throws IOException, InterruptedException {
        ensureConnected();

        while (true) {
            while (responses.size() < MAX_PENDING_REQUESTS && !unsentRequests.isEmpty()) {
                var request = unsentRequests.poll();
                responses.put(new ArchiveGroup(request.archive, request.group), request);
                sendRequestGroupB(request.archive, request.group);
            }

            if (socket.getInputStream().available() > 0) {
                handleResponse();
            }

            Thread.sleep(1);
        }
    }

    private void ensureConnected() throws IOException {
        if (!connected) {
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

            sendConnected(5, 0);
            sendLoggedOut(5, 0);
            connected = true;
        }
    }

    private void sendRequestGroupA(int archive, int group) throws IOException {
        var request = Packet.create(10);
        request.clear();
        request.p1(0);
        request.p1(archive);
        request.p4(group);
        request.p2(version);
        request.p2(unknown);
        send(request);
    }

    private void sendRequestGroupB(int archive, int group) throws IOException {
        var request = Packet.create(10);
        request.p1(1);
        request.p1(archive);
        request.p4(group);
        request.p2(version);
        request.p2(unknown);
        send(request);
    }

    private void sendRequestGroupC(int archive, int group) throws IOException {
        var request = Packet.create(10);
        request.p1(17);
        request.p1(archive);
        request.p4(group);
        request.p2(version);
        request.p2(unknown);
        send(request);
    }

    private void sendRequestGroupD(int archive, int group) throws IOException {
        var request = Packet.create(10);
        request.p1(32);
        request.p1(archive);
        request.p4(group);
        request.p2(version);
        request.p2(unknown);
        send(request);
    }

    private void sendRequestGroupE(int archive, int group) throws IOException {
        var request = Packet.create(10);
        request.p1(33);
        request.p1(archive);
        request.p4(group);
        request.p2(version);
        request.p2(unknown);
        send(request);
    }

    private void sendLoggedIn(int arg1, int arg2) throws IOException {
        var request = Packet.create(10);
        request.p1(2);
        request.p3(arg1);
        request.p2(arg2);
        request.p2(version);
        request.p2(unknown);
        send(request);
    }

    private void sendLoggedOut(int arg1, int arg2) throws IOException {
        var request = Packet.create(10);
        request.p1(3);
        request.p3(arg1);
        request.p2(arg2);
        request.p2(version);
        request.p2(unknown);
        send(request);
    }

    private void sendConnected(int arg1, int arg2) throws IOException {
        var request = Packet.create(10);
        request.p1(6);
        request.p3(arg1);
        request.p2(arg2);
        request.p2(version);
        request.p2(unknown);
        send(request);
    }

    public void handleResponse() throws IOException {
        var packet = receive(5);
        var archive = packet.g1();
        var group = packet.g4s();

        var response = responses.get(new ArchiveGroup(archive, group));

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
            responses.remove(new ArchiveGroup(archive, group));
            response.future.complete(response.buffer.array());
        }
    }

    private void send(Packet request) throws IOException {
        socket.getOutputStream().write(request.arr, 0, request.pos);
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
    public byte[] get(int archive, int group) {
        var future = getAsync(archive, group);

        try {
            return future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<byte[]> getAsync(int archive, int group) {
        shutdownRequestedLock.readLock().lock();

        try {
            if (shutdownRequested) {
                return CompletableFuture.failedFuture(new IOException("resource provider has been shutdown"));
            } else {
                var future = new CompletableFuture<byte[]>();
                unsentRequests.add(new GroupRequest(archive, group, future));
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
        private ByteBuffer buffer;

        public GroupRequest(int archive, int group, CompletableFuture<byte[]> future) {
            this.archive = archive;
            this.group = group;
            this.future = future;
        }
    }

    private record ArchiveGroup(int archive, int group) {}
}
