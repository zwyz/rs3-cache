package rs3.unpack;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class StylesheetUnpacker {
    public static Map<Integer, String> KEYS = new HashMap<>();

    static {
        try {
            for (var key : Files.readAllLines(Path.of("data/stylesheetkeys.txt"))) {
                KEYS.put(hash(key), key);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.STYLESHEET, id) + "]");

        lines.add("parent=" + Unpacker.format(Type.STYLESHEET, packet.g2null()));
        var count = packet.g2();

        for (var i = 0; i < count; i++) {
            var unknown = packet.g1();
            var a = packet.g4s();
            var b = packet.g4s();
            lines.add("entry=" + unknown + "," + KEYS.getOrDefault(a, "0x" + Integer.toHexString(a)) + "," + b);
        }

        return lines;
    }

    public static int hash(String s) {
        var hash = 0;

        for (var i = 0; i < s.length(); i++) {
            hash = s.charAt(i) + 31 * hash;
        }

        return hash;
    }
}
