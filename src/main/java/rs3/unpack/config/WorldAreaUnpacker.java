package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class WorldAreaUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.WORLD_AREA, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 2 -> lines.add("colour=" + Integer.toHexString(packet.g3()));
            case 3 -> lines.add("impostorsquare=" + Unpacker.format(Type.COORDGRID, packet.g4s()) + "," + Unpacker.format(Type.COORDGRID, packet.g4s()));
            case 4 -> lines.add("impostorzone=" + Unpacker.format(Type.COORDGRID, packet.g4s()) + "," + formatTemplateZone(packet.g4s()));
            default -> throw new IllegalStateException("unknown opcode");
        }
    }

    public static String formatTemplateZone(int value) {
        if (value >>> 26 != 0) {
            throw new IllegalStateException("invalid template zone " + value);
        }

        var level = (value >> 24) & 0x3;
        var x = ((value >> 14) & 0x3ff) * 8;
        var z = ((value >> 3) & 0x7ff) * 8;
        var angle = (value >> 1) & 0x3;
        var unknown = value & 1;

        return level + "_" + (x / 64) + "_" + (z / 64) + "_" + (x % 64) + "_" + (z % 64) + "," + angle + "," + unknown;
    }
}
