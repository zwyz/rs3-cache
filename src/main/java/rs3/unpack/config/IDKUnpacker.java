package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class IDKUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.IDKIT, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("bodypart=" + packet.g1());

            case 2 -> {
                var count = packet.g1();

                for (var i = 0; i < count; ++i) {
                    lines.add("model=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
                }
            }

            case 3 -> lines.add("disable=yes");

            case 40 -> {
                var count = packet.g1();

                for (var i = 0; i < count; ++i) {
                    lines.add("recol=" + packet.g2() + "," + packet.g2());
                }
            }

            case 41 -> {
                var count = packet.g1();

                for (var i = 0; i < count; ++i) {
                    lines.add("retex=" + packet.g2() + "," + packet.g2());
                }
            }

            case 44 -> lines.add("unknown44=" + packet.g2());
            case 45 -> lines.add("unknown45=" + packet.g2());
            case 60 -> lines.add("head1=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 61 -> lines.add("head2=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 62 -> lines.add("head3=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 63 -> lines.add("head4=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 64 -> lines.add("head5=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 65 -> lines.add("head6=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 66 -> lines.add("head7=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 67 -> lines.add("head8=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 68 -> lines.add("head9=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 69 -> lines.add("head10=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
