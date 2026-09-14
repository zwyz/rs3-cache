package rs3.unpack.defaults;

import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class WearposDefaultsUnpacker {
    public static List<String> unpack(byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        var wearposcount = 0;

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> {
                wearposcount = packet.g1();
                var line = new ArrayList<String>();

                for (var i = 0; i < wearposcount; i++) {
                    line.add(String.valueOf(packet.g1()));
                }

                lines.add("wearpostype=" + String.join(",", line));
            }

            case 3 -> lines.add("replaceheldleft=" + Unpacker.formatWearPos(packet.g1())); // 216 GetReplaceHeldLeftWearPos
            case 4 -> lines.add("replaceheldright=" + Unpacker.formatWearPos(packet.g1())); // 216 GetReplaceHeldRightWearPos

            case 5 -> { // 216 GetReplaceHeldLeftResetWearPos
                var count = packet.g1();
                var line = new ArrayList<String>();

                for (var i = 0; i < count; i++) {
                    line.add(Unpacker.formatWearPos(packet.g1()));
                }

                lines.add("replaceheldleftreset=" + String.join(",", line));
            }

            case 6 -> { // 216 GetReplaceHeldRightResetWearPos
                var count = packet.g1();
                var line = new ArrayList<String>();

                for (var i = 0; i < count; i++) {
                    line.add(Unpacker.formatWearPos(packet.g1()));
                }

                lines.add("replaceheldrightreset=" + String.join(",", line));
            }

            case 7 -> {
                var line = new ArrayList<String>();

                for (var i = 0; i < wearposcount; i++) {
                    line.add(Unpacker.formatWearPos(packet.g1()));
                }

                lines.add("unknown7=" + String.join(",", line));
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
