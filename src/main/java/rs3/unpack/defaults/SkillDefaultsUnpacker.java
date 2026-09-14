package rs3.unpack.defaults;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class SkillDefaultsUnpacker {
    public static List<String> unpack(byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    var stat = packet.g1();
                    var maxLevel = packet.g2();
                    var flags = packet.g1();
                    var cappedFreeLevel = (flags & 0x2) != 0 ? packet.g1() : 0;
                    var xpTable = (flags & 0x4) != 0 ? String.valueOf(packet.g1()) : "null";
                    var levelOffset = (flags & 0x8) != 0 ? packet.g1s() : 1;
                    var unknown = packet.g1();

                    lines.add("skill=" + Unpacker.format(Type.STAT, stat) + "," + maxLevel + "," + Unpacker.formatYesNo(flags & 0x1) + "," + Unpacker.formatYesNo(unknown) + "," + cappedFreeLevel + "," + xpTable + "," + levelOffset);
                }
            }

            case 2 -> { // xp tables, terminated by id 0xff
                packet.g1(); // count

                for (var table = packet.g1(); table != 0xFF; table = packet.g1()) {
                    var count = packet.g2();
                    var thresholds = new StringJoiner(",");

                    for (var i = 0; i < count; i++) {
                        thresholds.add(String.valueOf(packet.g4s()));
                    }

                    lines.add("xptable" + table + "=" + table + "," + thresholds);
                }
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
