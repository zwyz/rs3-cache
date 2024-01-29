package rs3.unpack;

import rs3.util.Packet;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TextureUnpacker {
    public static void unpack(int id, byte[] data) {
        var packet = new Packet(data);
        var count = packet.g1();

        for (var i = 0; i < count; i++) {
            var levels = packet.g1();

            for (var level = 0; level < levels; level++) {
                var length = packet.g4s();
                var png = packet.gdata(length);

                if (level == 0) {
                    try {
                        Files.write(Path.of("texturedump/" + id + "_" + i + ".png"), png);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }
        }
    }
}
