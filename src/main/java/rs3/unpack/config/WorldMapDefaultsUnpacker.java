package rs3.unpack.config;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class WorldMapDefaultsUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[worldmapdefaults_" + id + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("unknown1=" + packet.g4s());
            case 2 -> lines.add("membersfillcolour=0x" + Integer.toHexString(packet.g4s()));
            case 3 -> lines.add("membersbordercolour=0x" + Integer.toHexString(packet.g4s()));
            case 4 -> lines.add("membersborderthickness=" + packet.g1());
            case 5 -> lines.add("memberschamferwidth=" + packet.g1());
            case 6 -> lines.add("mainarea=" + packet.g4s());
            case 7 -> lines.add("textshadowcolour=0x" + Integer.toHexString(packet.g4s()));

            case 100 -> lines.add("font0zoom0=" + packet.g2());
            case 101 -> lines.add("font1zoom0=" + packet.g2());
            case 102 -> lines.add("font2zoom0=" + packet.g2());

            case 108 -> lines.add("font0zoom1=" + packet.g2());
            case 109 -> lines.add("font1zoom1=" + packet.g2());
            case 110 -> lines.add("font2zoom1=" + packet.g2());

            case 116 -> lines.add("font0zoom2=" + packet.g2());
            case 117 -> lines.add("font1zoom2=" + packet.g2());
            case 118 -> lines.add("font2zoom2=" + packet.g2());

            case 124 -> lines.add("font0zoom3=" + packet.g2());
            case 125 -> lines.add("font1zoom3=" + packet.g2());
            case 126 -> lines.add("font2zoom3=" + packet.g2());

            case 132 -> lines.add("font0zoom4=" + packet.g2());
            case 133 -> lines.add("font1zoom4=" + packet.g2());
            case 134 -> lines.add("font2zoom4=" + packet.g2());

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
