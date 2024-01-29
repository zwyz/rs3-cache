package rs3.unpack.config;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class WearPosDefaultsUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[wearposdefaults_" + id + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> {
                var count = packet.g1();
                var line = new ArrayList<String>();

                for (var i = 0; i < count; i++) {
                    line.add(String.valueOf(packet.g1()));
                }

                lines.add("unknown1=" + String.join(",", line));
            }

            case 3 -> lines.add("lefthand=" + packet.g1());
            case 4 -> lines.add("righthand=" + packet.g1());

            case 5 -> {
                var count = packet.g1();
                var line = new ArrayList<String>();

                for (var i = 0; i < count; i++) {
                    line.add(String.valueOf(packet.g1()));
                }

                lines.add("unknown5=" + String.join(",", line));
            }

            case 6 -> {
                var count = packet.g1();
                var line = new ArrayList<String>();

                for (var i = 0; i < count; i++) {
                    line.add(String.valueOf(packet.g1()));
                }

                lines.add("unknown6=" + String.join(",", line));
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
