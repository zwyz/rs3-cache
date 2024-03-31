package rs3.avatarviewer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

public class AvatarViewerDumper {
    private static final HttpClient HTTP = HttpClient.newBuilder().build();
    private static final int MAX_OBJ = 56630;

    public static void main(String[] args) throws IOException, InterruptedException {
        dump(0, MAX_OBJ);
    }

    private static void dump(int start, int end) throws IOException, InterruptedException {
        var next = start;

        while (next <= end) {
            var appearance = new PlayerAppearance();

            for (var slot = 0; slot < appearance.worn.length; slot++) {
                if (PlayerAppearance.HIDDEN[slot] != 1) {
                    appearance.worn[slot] = Math.min(MAX_OBJ, next++);
                }
            }

            var encoded = Base64.getEncoder().encodeToString(appearance.encode()).replace('+', '*').replace('/', '-');
            var request = HttpRequest.newBuilder(URI.create("https://secure.runescape.com/m=adventurers-log/l=0/avatardetails.json?details=" + encoded));
            var response = HTTP.send(request.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IllegalStateException("status " + response.statusCode());
            }

            var data = new Gson().fromJson(response.body(), JsonObject.class);

            for (var slot = 0; slot < appearance.worn.length; slot++) {
                if (PlayerAppearance.HIDDEN[slot] != 1) {
                    var json = data.getAsJsonArray("worn").get(slot);

                    if (json.isJsonPrimitive() && json.getAsString().equals("undefined")) {
                        System.out.println("missing " + appearance.worn[slot]);
                    } else {
                        var info = json.getAsJsonObject();

                        var name = info.getAsJsonPrimitive("name").getAsString();
                        var desc = info.getAsJsonPrimitive("desc").getAsString();
                        var members = info.getAsJsonPrimitive("members").getAsBoolean();
                        var tradeable = info.getAsJsonPrimitive("tradeable").getAsBoolean();
                        var weight = info.getAsJsonPrimitive("weight").getAsInt();
                        output(appearance.worn[slot] + "," + encodeString(name) + "," + encodeString(desc) + "," + members + "," + tradeable + "," + weight);
                    }
                }
            }
        }
    }

    private static String encodeString(String name) {
        return "\"" + name.replace("\\", "\\\\").replace("\"", "\\") + "\"";
    }

    private static void output(String s) throws IOException {
        Files.writeString(Path.of("objdump.txt"), s + "\n", StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        System.out.println(s);
    }
}
