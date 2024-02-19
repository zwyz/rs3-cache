package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class InvUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.INV, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 2 -> lines.add("size=" + packet.g2()); // https://twitter.com/JagexAsh/status/1087312806435794945

            case 4 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("stock" + (i + 1) + "=" + Unpacker.format(Type.OBJ, packet.g2()) + "," + packet.g2()); // https://twitter.com/JagexAsh/status/1087312806435794945
                }
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
