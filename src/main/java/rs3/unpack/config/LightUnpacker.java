package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class LightUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.LIGHT, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("function=" + packet.g1());
            case 2 -> lines.add("frequency=" + packet.g2());
            case 3 -> lines.add("amplitude=" + packet.g2());
            case 4 -> lines.add("offset=" + packet.g2s()); // html5 linked to loctype offset

            case 5 -> lines.add("unknown5=" + packet.g4s());
            case 6 -> lines.add("unknown6=" + packet.g4s());
            case 7 -> lines.add("unknown7=" + packet.g4s());

            case 8 -> lines.add("swayamount=" + packet.gFloat());
            case 9 -> lines.add("swayamountrandom=yes");
            case 10 -> lines.add("swayduration=" + packet.g4s());
            case 11 -> lines.add("swaydurationrandom=" + packet.g4s());
            case 12 -> lines.add("swayeasing=" + packet.gFloat());
            case 13 -> lines.add("swayfade=" + packet.gFloat());

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
