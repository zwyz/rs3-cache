package rs3.unpack;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class MaterialUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.MATERIAL, id) + "]");

        var version = packet.g1();
        lines.add("version=" + version);

        if (version == 0) {
            decodeV0(lines, packet);
        } else if (version == 1 || version == 2) {
            decodeV1(lines, packet);
        } else {
            throw new UnsupportedOperationException();
        }

        if (packet.pos != packet.arr.length) {
            throw new IllegalStateException("didn't reach end of file");
        }

        return lines;
    }

    private static void decodeV0(ArrayList<String> lines, Packet packet) {
        lines.add("unknown1=" + packet.g1());
        lines.add("size=" + packet.g1());
        var flagsA = packet.g4s();

        var flaga0 = (flagsA & 1) != 0;
        var flaga1 = (flagsA & 2) != 0;
        var flaga2 = (flagsA & 4) != 0;
        var flaga3 = (flagsA & 8) != 0;
        var flaga4 = (flagsA & 16) != 0;

        if (flaga0) lines.add("flaga0=yes");
        if (flaga1) lines.add("flaga1=yes");
        if (flaga2) lines.add("flaga2=yes");
        if (flaga3) lines.add("flaga3=yes");
        if (flaga4) lines.add("flaga4=yes");

        if (flaga0 || flaga4) {
            lines.add("texture=" + Unpacker.format(Type.TEXTURE, packet.g4s()));
        }

        if (flaga3 || flaga1) {
            lines.add("bloomtexture=" + Unpacker.format(Type.TEXTURE, packet.g4s()));
        }

        var unknown = packet.g1();
        lines.add("repeat=" + (unknown & 7) + "," + (unknown >> 3 & 7));
        var flagsB = packet.g4s();
        var flagb0 = (flagsB & 1) != 0;
        var flagb1 = (flagsB & 2) != 0;
        var flagb2 = (flagsB & 4) != 0;
        var flagb3 = (flagsB & 8) != 0;
        var flagb4 = (flagsB & 0x10) != 0;
        var flagb5 = (flagsB & 0x20) != 0;
        var flagb6 = (flagsB & 0x40) != 0;
        var flagb7 = (flagsB & 0x80) != 0;
        var flagb8 = (flagsB & 0x100) != 0;
        var flagb9 = (flagsB & 0x200) != 0;
        var flagb10 = (flagsB & 0x400) != 0;
        var flagb11 = (flagsB & 0x800) != 0;
        var flagb18 = (flagsB & 0x40000) != 0;
        var flagb19 = (flagsB & 0x80000) != 0;
        var flagb20 = (flagsB & 0x100000) != 0;
        var flagb21 = (flagsB & 0x200000) != 0;

        lines.add("flagb0=" + (flagb0 ? "yes" : "no"));
        lines.add("flagb1=" + (flagb1 ? "yes" : "no"));
        lines.add("flagb2=" + (flagb2 ? "yes" : "no"));
        lines.add("flagb4=" + (flagb4 ? "yes" : "no"));
        lines.add("flagb21=" + (flagb21 ? "yes" : "no"));
        lines.add("flagb20=" + (flagb20 ? "yes" : "no"));

        if (flagb5) {
            lines.add("unknown19=" + packet.g1() + "," + Unpacker.format(Type.TEXTURE, packet.g4s()));
        }

        if (flagb6) {
            lines.add("unknown20=" + packet.g1() + "," + Unpacker.format(Type.TEXTURE, packet.g4s()));
        }

        if (flagb18) {
            lines.add("unknown4=" + packet.g4s());
        }

        if (flagb19) {
            lines.add("unknown5=" + packet.g4s() + "," + packet.gFloat() + "," + packet.gFloat() + "," + packet.g4s() + "," + packet.g4s());
        }

        if (flagb4) {
            lines.add("unknown6=" + packet.gFloat() + "," + packet.gFloat());
        }

        if (flaga1) {
            lines.add("unknown7=" + packet.gFloat());
        }

        lines.add("bloom=" + (packet.g1() == 1 ? "yes" : "no"));
        lines.add("facetmode=" + packet.g1());
        var alphamode = packet.g1();

        lines.add("alphamode=" + switch (alphamode) {
            case 0 -> "none";
            case 1 -> "test," + packet.g1();
            case 2 -> "multiply";
            default -> throw new IllegalStateException("Unexpected value: " + alphamode);
        });

        if (flagb11) {
            lines.add("unknown9=" + packet.gFloat() + "," + packet.gFloat() + "," + packet.gFloat());
        }

        var flagsC = packet.g1();
        var flagc1 = (flagsC & 1) != 0;
        var flagc2 = (flagsC & 2) != 0;

        if (flagc1) {
            lines.add("speedu=" + packet.g2s());
        }

        if (flagc2) {
            lines.add("speedv=" + packet.g2s());
        }

        if (packet.g1() == 1) {
            lines.add("effect=" + packet.g1());
            lines.add("effectarg1=" + packet.g1());
            lines.add("effectarg2=" + packet.g4s());
            lines.add("effectcombiner=" + packet.g1());
            lines.add("unknown15=" + (packet.g1() == 1 ? "yes" : "no"));
            lines.add("mipmapping=" + packet.g1());
            lines.add("lowdetail=" + (packet.g1() == 1 ? "yes" : "no"));
            lines.add("highdetail=" + (packet.g1() == 1 ? "yes" : "no"));
            lines.add("lightness=" + packet.g1());
            lines.add("saturation=" + packet.g1());
            lines.add("averagecolour=" + packet.g2());
        }
    }


    private static void decodeV1(ArrayList<String> lines, Packet packet) {
        var flagsb = packet.g4s();
        var flagb0 = (flagsb & 1) != 0;
        var flagb1 = (flagsb & 2) != 0;
        var flagb2 = (flagsb & 4) != 0;
        var flagb3 = (flagsb & 8) != 0;
        var flagb4 = (flagsb & 0x10) != 0;
        var flagb5 = (flagsb & 0x20) != 0;
        var flagb6 = (flagsb & 0x40) != 0;
        var flagb7 = (flagsb & 0x80) != 0;
        var flagb8 = (flagsb & 0x100) != 0;
        var flagb9 = (flagsb & 0x200) != 0;
        var flagb10 = (flagsb & 0x400) != 0;
        var flagb11 = (flagsb & 0x800) != 0;
        var flagb12 = (flagsb & 0x1000) != 0;
        var flagb13 = (flagsb & 0x2000) != 0;
        var flagb14 = (flagsb & 0x4000) != 0;
        var flagb15 = (flagsb & 0x8000) != 0;
        var flagb16 = (flagsb & 0x10000) != 0;
        var flagb17 = (flagsb & 0x20000) != 0;
        var flagb18 = (flagsb & 0x40000) != 0;
        var flagb19 = (flagsb & 0x80000) != 0;
        var flagb20 = (flagsb & 0x100000) != 0;
        var flagb21 = (flagsb & 0x200000) != 0;

        if ((flagsb >> 22) != 0) {
            throw new IllegalStateException("invalid flag");
        }

        if (flagb0) lines.add("flagsb0=yes");
        if (flagb1) lines.add("flagsb1=yes");
        if (flagb2) lines.add("flagsb2=yes");
        if (flagb3) lines.add("flagsb3=yes");
        if (flagb4) lines.add("flagsb4=yes");
        if (flagb10) lines.add("flagsb10=yes");
        if (flagb21) lines.add("flagsb21=yes");
        if (flagb20) lines.add("flagsb20=yes");

        if (flagb5) {
            lines.add("unknown19=" + packet.g1() + "," + Unpacker.format(Type.TEXTURE, packet.g4s()));
        }

        if (flagb6) {
            lines.add("unknown20=" + packet.g1() + "," + Unpacker.format(Type.TEXTURE, packet.g4s()));
        }

        if (flagb7) {
            lines.add("unknown21=" + packet.g1() + "," + Unpacker.format(Type.TEXTURE, packet.g4s()));
        }

        if (flagb18) {
            lines.add("unknown4=" + packet.g4s());
        }

        if (flagb19) {
            lines.add("unknown5=" + packet.g4s() + "," + packet.gFloat() + "," + packet.gFloat() + "," + packet.g4s() + "," + packet.g4s());
        }

        if (flagb12) {
            lines.add("unknown6=" + packet.gFloat());
        }

        if (flagb13) {
            lines.add("unknown7=" + packet.g4s());
        }

        if (flagb14) {
            lines.add("unknown22=" + packet.gFloat());
        }

        if (flagb15) {
            lines.add("unknown23=" + packet.g4s());
        }

        if (flagb6) {
            lines.add("unknown24=" + packet.gFloat());
        }

        if (flagb11) {
            lines.add("unknown9=" + packet.gFloat() + "," + packet.gFloat() + "," + packet.gFloat());
        }

        if (flagb16) {
            lines.add("unknown25=" + packet.gFloat());
        }

        if (flagb17) {
            lines.add("unknown26=" + packet.gFloat());
        }

        if (flagb8) {
            lines.add("speedu=" + packet.g2s());
        }

        if (flagb9) {
            lines.add("speedv=" + packet.g2s());
        }

        var unknown = packet.g1();
        lines.add("repeat=" + (unknown & 7) + "," + (unknown >> 3 & 7));
        lines.add("facetmode=" + packet.g1());
        lines.add("qualitymode=" + packet.g1());

        var alphamode = packet.g1();

        lines.add("alphamode=" + switch (alphamode) {
            case 0 -> "none";
            case 1 -> "test," + packet.g1();
            case 2 -> "multiply";
            default -> throw new IllegalStateException("Unexpected value: " + alphamode);
        });

        lines.add("averagecolour=" + packet.g2());
        lines.add("size=" + packet.g1());
    }
}
