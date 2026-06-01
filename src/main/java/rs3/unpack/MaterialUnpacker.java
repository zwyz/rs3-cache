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

        if (version == 0) {
            lines.add("version=" + version);
            decodeRT5(lines, packet);
        } else {
            decodeRT7(lines, packet);
        }

        if (packet.pos != packet.arr.length) {
            throw new IllegalStateException("didn't reach end of file");
        }

        return lines;
    }

    private static void decodeRT5(ArrayList<String> lines, Packet packet) {
        lines.add("unknown1=" + packet.g1());
        lines.add("size=" + packet.g1());
        var flags1 = packet.g4s();

        if ((flags1 & 1) != 0) lines.add("unknown2=yes");
        if ((flags1 & 2) != 0) lines.add("unknown3=yes");
        if ((flags1 & 4) != 0) lines.add("unknown4=yes");
        if ((flags1 & 8) != 0) lines.add("unknown5=yes");
        if ((flags1 & 16) != 0) lines.add("unknown6=yes");

        if ((flags1 & 1) != 0 || (flags1 & 16) != 0) {
            lines.add("texture=" + Unpacker.format(Type.TEXTURE, packet.g4s()));
        }

        if ((flags1 & 8) != 0 || (flags1 & 2) != 0) {
            lines.add("bloomtexture=" + Unpacker.format(Type.TEXTURE, packet.g4s()));
        }

        var unknown = packet.g1();
        lines.add("repeat=" + (unknown & 7) + "," + (unknown >> 3 & 7));
        var flags2 = packet.g4s();

        if ((flags2 & 1) != 0) lines.add("unknown7=yes");
        if ((flags2 & 2) != 0) lines.add("unknown8=yes");
        if ((flags2 & 4) != 0) lines.add("unknown9=yes");
        if ((flags2 & 8) != 0) lines.add("unknown10=yes");
        if ((flags2 & 0x200000) != 0) lines.add("unknown12=yes");
        if ((flags2 & 0x100000) != 0) lines.add("unknown13=yes");

        if ((flags2 & 0x40000) != 0) {
            lines.add("unknown16=" + packet.g4s());
        }

        if ((flags2 & 0x80000) != 0) {
            lines.add("unknown17=" + packet.g4s() + "," + packet.gFloat() + "," + packet.gFloat() + "," + packet.g4s() + "," + packet.g4s());
        }

        if ((flags2 & 0x10) != 0) {
            lines.add("unknown18=" + packet.gFloat() + "," + packet.gFloat());
        }

        if ((flags1 & 2) != 0) {
            lines.add("unknown19=" + packet.gFloat());
        }

        lines.add("bloom=" + (packet.g1() == 1 ? "yes" : "no"));
        lines.add("facetmode=" + packet.g1());

        lines.add("alphamode=" + switch (packet.g1()) {
            case 0 -> "none";
            case 1 -> "test," + packet.g1();
            case 2 -> "multiply";
            default -> throw new IllegalStateException("unexpected alphamode");
        });

        if ((flags2 & 0x800) != 0) {
            lines.add("unknown20=" + packet.gFloat() + "," + packet.gFloat() + "," + packet.gFloat());
        }

        var flags3 = packet.g1();

        if ((flags3 & 1) != 0) {
            lines.add("speedu=" + packet.g2s());
        }

        if ((flags3 & 2) != 0) {
            lines.add("speedv=" + packet.g2s());
        }

        if ((flags2 & 0x800000) != 0) {
            lines.add("unknown21=" + packet.gFloat());
        }

        if (packet.g1() == 1) {
            lines.add("effect=" + packet.g1());
            lines.add("effectarg1=" + packet.g1());
            lines.add("effectarg2=" + packet.g4s());
            lines.add("effectcombiner=" + packet.g1());
            if (packet.g1() == 1) lines.add("unknown22=yes");
            lines.add("mipmapping=" + packet.g1());
            if (packet.g1() == 1) lines.add("lowdetail=yes");
            if (packet.g1() == 1) lines.add("highdetail=yes");
            lines.add("lightness=" + packet.g1());
            lines.add("saturation=" + packet.g1());
            lines.add("averagecolour=" + packet.g2());
        }
    }


    private static void decodeRT7(ArrayList<String> lines, Packet packet) {
        var flags = packet.g4s();

        if ((flags & 1) != 0) lines.add("unknown7=yes");
        if ((flags & 4) != 0) lines.add("unknown9=yes");
        if ((flags & 8) != 0) lines.add("unknown10=yes");
        if ((flags & 0x400) != 0) lines.add("unknown28=yes");
        if ((flags & 0x200000) != 0) lines.add("unknown12=yes");
        if ((flags & 0x100000) != 0) lines.add("unknown13=yes");

        if ((flags & 0x20) != 0) {
            lines.add("unknown31=" + packet.g1() + "," + Unpacker.format(Type.TEXTURE, packet.g4s()));
        }

        if ((flags & 0x40) != 0) {
            lines.add("unknown32=" + packet.g1() + "," + Unpacker.format(Type.TEXTURE, packet.g4s()));
        }

        if ((flags & 0x80) != 0) {
            lines.add("unknown33=" + packet.g1() + "," + Unpacker.format(Type.TEXTURE, packet.g4s()));
        }

        if ((flags & 0x40000) != 0) {
            lines.add("unknown16=" + packet.g4s());
        }

        if ((flags & 0x80000) != 0) {
            lines.add("unknown17=" + packet.g4s() + "," + packet.gFloat() + "," + packet.gFloat() + "," + packet.g4s() + "," + packet.g4s());
        }

        if ((flags & 0x1000) != 0) {
            lines.add("unknown18=" + packet.gFloat());
        }

        if ((flags & 0x2000) != 0) {
            lines.add("unknown19=" + packet.g4s());
        }

        if ((flags & 0x4000) != 0) {
            lines.add("unknown38=" + packet.gFloat());
        }

        if ((flags & 0x8000) != 0) {
            lines.add("unknown39=" + packet.g4s());
        }

        if ((flags & 0x40) != 0) {
            lines.add("unknown40=" + packet.gFloat());
        }

        if ((flags & 0x800) != 0) {
            lines.add("unknown20=" + packet.gFloat() + "," + packet.gFloat() + "," + packet.gFloat());
        }

        if ((flags & 0x10000) != 0) {
            lines.add("unknown42=" + packet.gFloat());
        }

        if ((flags & 0x20000) != 0) {
            lines.add("unknown43=" + packet.gFloat());
        }

        if ((flags & 0x400000) != 0) {
            lines.add("unknown44=" + packet.gFloat());
        }

        if ((flags & 0x100) != 0) {
            lines.add("speedu=" + packet.g2s());
        }

        if ((flags & 0x200) != 0) {
            lines.add("speedv=" + packet.g2s());
        }

        var repeat = packet.g1();
        lines.add("repeat=" + (repeat & 7) + "," + (repeat >> 3 & 7));
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
