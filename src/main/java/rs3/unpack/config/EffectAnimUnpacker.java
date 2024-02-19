package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class EffectAnimUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.SPOTANIM, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("model=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 2 -> lines.add("anim=" + Unpacker.format(Type.SEQ, packet.gSmart2or4null()));
            case 4 -> lines.add("resizeh=" + packet.g2());
            case 5 -> lines.add("resizev=" + packet.g2());
            case 6 -> lines.add("rotation=" + packet.g2());
            case 7 -> lines.add("ambient=" + packet.g1());
            case 8 -> lines.add("contrast=" + packet.g1());
            case 10 -> lines.add("unknown10=yes");

            case 9 -> lines.add("unknown9=8224");
            case 15 -> lines.add("unknown9=" + packet.g2());
            case 16 -> lines.add("unknown9=" + packet.g4s());

            case 40 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("recol" + (i + 1) + "s=" + packet.g2());
                    lines.add("recol" + (i + 1) + "d=" + packet.g2());
                }
            }

            case 41 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("retex" + (i + 1) + "s=" + Unpacker.format(Type.MATERIAL, packet.g2()));
                    lines.add("retex" + (i + 1) + "d=" + Unpacker.format(Type.MATERIAL, packet.g2()));
                }
            }

            case 44 -> lines.add("unknown44=" + packet.g2());
            case 45 -> lines.add("unknown45=" + packet.g2());

            case 46 -> lines.add("unknown46=yes");

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
