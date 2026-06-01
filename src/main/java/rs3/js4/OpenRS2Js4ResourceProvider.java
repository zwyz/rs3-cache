package rs3.js4;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Semaphore;

public class OpenRS2Js4ResourceProvider implements Js4ResourceProvider, AutoCloseable {
    private static final HttpClient HTTP = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final int MAX_CONCURRENT_REQUESTS = 50;
    private final String scope;
    private final int id;
    private final Semaphore semaphore = new Semaphore(MAX_CONCURRENT_REQUESTS);

    public OpenRS2Js4ResourceProvider(String scope, int id) {
        this.scope = scope;
        this.id = id;
    }

    @Override
    public byte[] get(int archive, int file) {
        return request(archive, file);
    }

    private byte[] request(int archive, int group) {
        return request(archive, group, "https://archive.openrs2.org/caches/" + scope + "/" + id + "/archives/" + archive + "/groups/" + group + ".dat");
    }

    private byte[] request(int archive, int group, String url) {
        var failureCount = 0;

        while (true) {
            try {
                semaphore.acquire();
                System.out.println("requesting " + archive + "." + group);
                var response = HTTP.send(HttpRequest.newBuilder(URI.create(url)).build(), HttpResponse.BodyHandlers.ofByteArray());

                if (response.statusCode() != 200) {
                    throw new IOException("received response " + response.statusCode() + " on archive " + archive + " group " + group);
                }

                System.out.println("received " + archive + "." + group);
                return response.body();
            } catch (IOException e) {
                if (failureCount++ >= 5) {
                    throw new UncheckedIOException(e);
                }
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            } finally {
                semaphore.release();
            }
        }
    }

    @Override
    public void close() {

    }
}
