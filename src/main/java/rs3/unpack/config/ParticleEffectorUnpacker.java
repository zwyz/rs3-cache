package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class ParticleEffectorUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.PARTICLE_EFFECTOR, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("unknown1=" + packet.g2());
            case 2 -> lines.add("unknown2=" + packet.g1());
            case 3 -> lines.add("unknown3=" + packet.g4s() + "," + packet.g4s() + "," + packet.g4s());
            case 4 -> lines.add("unknown4=" + packet.g1() + "," + packet.g4s());
            case 6 -> lines.add("unknown6=" + packet.g1());
            case 8 -> lines.add("unknown8=yes");
            case 9 -> lines.add("unknown9=yes");
            case 10 -> lines.add("unknown10=yes");

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
