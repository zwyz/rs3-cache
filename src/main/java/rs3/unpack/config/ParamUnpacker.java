package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class ParamUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.PARAM, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> {
                var type = packet.g1();
                Unpacker.setParamType(id, Type.byChar(type));
                lines.add("type=" + Unpacker.formatTypeChar(type));
            }

            case 2 -> lines.add("default=" + Unpacker.format(Unpacker.getParamType(id), packet.g4s()));
            case 4 -> lines.add("autodisable=no");
            case 5 -> lines.add("default=" + packet.gjstr());

            case 101 -> {
                var type = packet.gSmart1or2();
                Unpacker.setParamType(id, Type.byID(type));
                lines.add("type=" + Unpacker.format(Type.TYPE, type));
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
