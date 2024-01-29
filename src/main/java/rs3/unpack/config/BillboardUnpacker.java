package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class BillboardUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.BILLBOARD, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("material=" + Unpacker.format(Type.MATERIAL, packet.g2null()));
            case 2 -> lines.add("unknown2=" + packet.g2() + "," + packet.g2());
            case 3 -> lines.add("unknown3=" + packet.g1s());
            case 4 -> lines.add("unknown4=" + packet.g1());
            case 5 -> lines.add("unknown5=" + packet.g1());
            case 6 -> lines.add("unknown6=yes");
            case 7 -> lines.add("unknown7=yes");

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
