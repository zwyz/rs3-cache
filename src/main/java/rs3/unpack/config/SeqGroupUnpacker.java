package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class SeqGroupUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.SEQGROUP, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 2 -> {
                var count = packet.gSmart1or2();
                var result = new ArrayList<String>();

                for (var i = 0; i < count; i++) {
                    result.add("label_" + packet.gSmart1or2());
                }

                lines.add("walkmerge=" + String.join(",", result));
            }

            case 3 -> {
                lines.add("unknown3default=" + packet.g1());
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    lines.add("unknown3=" + packet.gSmart1or2() + "," + packet.g1());
                }
            }

            case 4 -> {
                lines.add("unknown4default=" + packet.g1());
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    lines.add("unknown4=" + packet.gSmart1or2() + "," + packet.g1());
                }
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
