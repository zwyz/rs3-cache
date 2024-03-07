package rs3.unpack.config;

import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class SkyBoxUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.SKYBOX, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("material=" + Unpacker.format(Type.MATERIAL, packet.g2()));

            case 2 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("unknown2=" + packet.g2());
                }
            }

            case 3 -> lines.add("unknown3=" + packet.g1());
            case 4 -> lines.add("fillmode=" + packet.g1());
            case 5 -> lines.add("unknown5=" + Unpacker.format(Type.MODEL, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()));
            case 6 -> lines.add("unknown6=" + packet.gSmart2or4null());

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
