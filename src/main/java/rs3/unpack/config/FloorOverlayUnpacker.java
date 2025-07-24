package rs3.unpack.config;

import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class FloorOverlayUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.OVERLAY, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("colour=0x" + Integer.toHexString(packet.g3()));
            case 2 -> lines.add("material=" + Unpacker.format(Type.MATERIAL, packet.g1()));

            case 3 -> {
                if (Unpack.VERSION < 400) {
                    lines.add("unknown3=yes"); // todo
                } else {
                    lines.add("material=" + Unpacker.format(Type.MATERIAL, packet.g2null()));
                }
            }

            case 5 -> lines.add("occlude=no");
            case 6 -> lines.add("debugname=" + packet.gjstr());
            case 7 -> lines.add("mapcolour=0x" + Integer.toHexString(packet.g3()));
            case 8 -> lines.add("unknown8=yes");
            case 9 -> lines.add("texturescale=" + packet.g2());
            case 10 -> lines.add("hardshadow=no");
            case 11 -> lines.add("priority=" + packet.g1());
            case 12 -> lines.add("smoothedges=yes");
            case 13 -> lines.add("waterfogcolour=0x" + Integer.toHexString(packet.g3()));
            case 14 -> lines.add("waterfogscale=" + packet.g1());
            case 15 -> lines.add("unknown15=" + packet.g2()); // todo
            case 16 -> lines.add("waterfogoffset=" + packet.g1());
            case 20 -> lines.add("waterfogunknowna=" + packet.g2());
            case 21 -> lines.add("waterfogunknownb=" + packet.g1());
            case 22 -> lines.add("waterfogunknownc=" + packet.g2());
            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
