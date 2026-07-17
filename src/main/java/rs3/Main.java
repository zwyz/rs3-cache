package rs3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static final String CONFIG_LIVE = "https://runescape.com/jav_config.ws?binaryType=0";
    public static final String CONFIG_BETA = "https://runescape.com/jav_config_beta.ws?binaryType=0";

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 2 && args[0].equals("live")) {
            Unpack.unpackLive(args[1], CONFIG_LIVE, 0);
            return;
        }

        if (args.length == 2 && args[0].equals("beta")) {
            Unpack.unpackLive(args[1], CONFIG_BETA, 0);
            return;
        }

        if (args.length == 1 && args[0].equals("openrs2")) {
            for (var line : Files.readAllLines(Path.of("data/caches.txt"))) {
                var parts = line.split(",");
                System.out.println(parts[1] + " (build " + parts[0] + ", id " + parts[2] + ")");
            }

            return;
        }

        if (args.length == 3 && args[0].equals("openrs2")) {
            for (var line : Files.readAllLines(Path.of("data/caches.txt"))) {
                var parts = line.split(",");

                if (parts[1].equals(args[1])) {
                    Unpack.unpackOpenRS2(args[2], Integer.parseInt(parts[0]), "runescape", Integer.parseInt(parts[2]), false);
                    return;
                }
            }

            System.err.println("cache not found");
            return;
        }

        System.out.println("== Usage ==");
        System.out.println("unpack live [output-dir] - unpack the live cache");
        System.out.println("unpack beta [output-dir] - unpack the beta cache");
        System.out.println("unpack openrs2 - list available openrs2 caches");
        System.out.println("unpack openrs2 [name] [output-dir] - unpack a cache from openrs2");
    }
}
