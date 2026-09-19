package rs3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UnpackEverything {
    private static final int START_INDEX = 0;

    public static void main(String[] args) throws IOException, InterruptedException {
        var caches = Files.readAllLines(Path.of("data/caches.txt"));
        var index = 0;

        for (var cache : caches) {
            var parts = cache.split(",");
            var build = Integer.parseInt(parts[0]);
            var name = parts[1];
            System.out.println("[Cache Unpacker] Unpacking " + name + " build " + build + " (" + (index + 1) + "/" + caches.size() + ")");

            if (index >= START_INDEX) {
                if (build < 226) {
                    Unpack.unpackOldOpenRS2("unpacked/" + name, build, parts[2]);
                } else {
                    Unpack.unpackOpenRS2("unpacked/" + name, build, "runescape", Integer.parseInt(parts[2]), false);
                }
            }

            index++;
        }
    }
}
