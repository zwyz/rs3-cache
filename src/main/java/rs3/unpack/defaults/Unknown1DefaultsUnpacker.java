package rs3.unpack.defaults;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Unknown1DefaultsUnpacker {
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

            case 1 -> lines.add("unknown1=" + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2());

            case 4 -> {
                var count = packet.g1();
                var parts = new StringJoiner(",");

                for (var i = 0; i < count; i++) {
                    parts.add(Unpacker.format(Type.ENUM, packet.g2null()));
                }

                lines.add("unknown4=" + parts);
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
