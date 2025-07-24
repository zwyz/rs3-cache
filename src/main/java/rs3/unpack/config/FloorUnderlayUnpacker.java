package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class FloorUnderlayUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.UNDERLAY, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("colour=0x" + Integer.toHexString(packet.g3()));
            case 2 -> lines.add("material=" + Unpacker.format(Type.MATERIAL, packet.g2null()));
            case 3 -> lines.add("texturescale=" + packet.g2());
            case 4 -> lines.add("hardshadow=no");
            case 5 -> lines.add("occlude=no");
            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
