package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class QuickChatCatUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.CHATCAT, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("desc=" + packet.gjstr());

            case 2 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("subcat=" + Unpacker.format(Type.CHATCAT, packet.g2()) + "," + packet.g1s());
                }
            }

            case 3 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("phrase=" + Unpacker.format(Type.CHATPHRASE, packet.g2()) + "," + packet.g1s());
                }
            }

            case 4 -> lines.add("unknown4=yes");

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
