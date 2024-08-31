package rs3;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientTokenProvider {
    public static final HttpClient HTTP = HttpClient.newBuilder().build();

    public static String getClientToken() throws IOException, InterruptedException {
        var response = HTTP.send(HttpRequest.newBuilder(URI.create("https://world1.runescape.com/jav_config.ws?binaryType=0")).build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("unexpected http response: " + response.statusCode());
        }

        for (var line : response.body().lines().toList()) {
            if (line.startsWith("param=29=")) {
                return line.split("=")[2];
            }
        }

        throw new IOException("received jav_config does not contain client token");
    }
}
