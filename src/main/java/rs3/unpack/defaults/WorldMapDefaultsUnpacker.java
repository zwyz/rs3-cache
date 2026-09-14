package rs3.unpack.defaults;

import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class WorldMapDefaultsUnpacker {
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

            case 1 -> lines.add("f2playermap=" + Unpacker.format(Type.MAPAREA, packet.g4s())); // 216 GetF2PLayerMapID
            case 2 -> lines.add("f2playercolour=" + Unpacker.formatColour(packet.g4s())); // 216 GetF2PLayerColourRGBA
            case 3 -> lines.add("f2poutlinecolour=" + Unpacker.formatColour(packet.g4s())); // 216 GetF2POutlineColourRGBA
            case 4 -> lines.add("f2poutlinethickness=" + packet.g1());
            case 5 -> lines.add("f2poutlinechamfer=" + packet.g1());
            case 6 -> lines.add("mainmap=" + Unpacker.format(Type.MAPAREA, packet.g4s()));
            case 7 -> lines.add("dropshadowcolour=" + Unpacker.formatColour(packet.g4s())); // 216 GetDropShadowColourRGBA

            case 100 -> lines.add("font0zoom0=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID
            case 101 -> lines.add("font1zoom0=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID
            case 102 -> lines.add("font2zoom0=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID

            case 108 -> lines.add("font0zoom1=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID
            case 109 -> lines.add("font1zoom1=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID
            case 110 -> lines.add("font2zoom1=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID

            case 116 -> lines.add("font0zoom2=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID
            case 117 -> lines.add("font1zoom2=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID
            case 118 -> lines.add("font2zoom2=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID

            case 124 -> lines.add("font0zoom3=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID
            case 125 -> lines.add("font1zoom3=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID
            case 126 -> lines.add("font2zoom3=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID

            case 132 -> lines.add("font0zoom4=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID
            case 133 -> lines.add("font1zoom4=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID
            case 134 -> lines.add("font2zoom4=" + Unpacker.format(Type.FONTMETRICS, packet.g2())); // 216 GetFontID

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
