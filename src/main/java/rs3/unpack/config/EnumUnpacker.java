package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class EnumUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.ENUM, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> {
                var type = packet.g1();
                Unpacker.setEnumInputType(id, Type.byChar(type));
                lines.add("inputtype=" + Type.byChar(type).name);
            }

            case 2 -> {
                var type = packet.g1();
                Unpacker.setEnumOutputType(id, Type.byChar(type));
                lines.add("outputtype=" + Type.byChar(type).name);
            }

            case 3 -> lines.add("default=" + packet.gjstr());
            case 4 -> lines.add("default=" + Unpacker.format(Unpacker.getEnumOutputType(id), packet.g4s()));

            case 5 -> { // sparse
                var count = packet.g2();

                for (var i = 0; i < count; ++i) {
                    lines.add("val=" + Unpacker.format(Unpacker.getEnumInputType(id), packet.g4s()) + "," + packet.gjstr());
                }
            }

            case 6 -> { // sparse
                var count = packet.g2();

                for (var i = 0; i < count; ++i) {
                    lines.add("val=" + Unpacker.format(Unpacker.getEnumInputType(id), packet.g4s()) + "," + Unpacker.format(Unpacker.getEnumOutputType(id), packet.g4s()));
                }
            }

            case 7 -> { // dense
                var capacity = packet.g2(); // todo: why is this different from count? defined in config?
                var count = packet.g2();

                for (var i = 0; i < count; ++i) {
                    lines.add("val=" + Unpacker.format(Unpacker.getEnumInputType(id), packet.g2()) + "," + packet.gjstr());
                }
            }

            case 8 -> { // dense
                var capacity = packet.g2(); // todo: why is this different from count? defined in config?
                var count = packet.g2();

                for (var i = 0; i < count; ++i) {
                    lines.add("val=" + Unpacker.format(Unpacker.getEnumInputType(id), packet.g2()) + "," + Unpacker.format(Unpacker.getEnumOutputType(id), packet.g4s()));
                }
            }

            case 101 -> {
                var type = packet.gSmart1or2();
                Unpacker.setEnumInputType(id, Type.byID(type));
                lines.add("inputtype=" + Unpacker.format(Type.TYPE, type));
            }

            case 102 -> {
                var type = packet.gSmart1or2();
                Unpacker.setEnumOutputType(id, Type.byID(type));
                lines.add("outputtype=" + Unpacker.format(Type.TYPE, type));
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
