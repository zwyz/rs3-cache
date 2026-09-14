package rs3.unpack.defaults;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class MapDefaultsUnpacker {
    public static List<String> unpack(byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("defaultenvironmentmap=" + Unpacker.format(Type.MATERIAL, packet.g2())); // 216 GetDefaultEnvironmentMap
            case 10 -> lines.add("defaultwatertype=" + Unpacker.format(Type.WATER, packet.g2())); // 216 GetDefaultWaterType

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
