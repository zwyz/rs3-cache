package rs3.unpack.config;

import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class IDKUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.IDKIT, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                lines = Unpacker.transformRecolRetexIndices(lines);
                return lines;
            }

            case 1 -> lines.add("bodypart=" + packet.g1());

            case 2 -> {
                var count = packet.g1();

                for (var i = 0; i < count; ++i) {
                    lines.add("model=" + Unpacker.format(Type.MODEL, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()));
                }
            }

            case 3 -> lines.add("disable=yes");

            case 40 -> {
                if (Unpack.VERSION < 400) {
                    lines.add("recol1s=" + packet.g2());
                } else {
                    var count = packet.g1();

                    for (var i = 0; i < count; ++i) {
                        lines.add("recol" + (i + 1) + "s=" + packet.g2());
                        lines.add("recol" + (i + 1) + "d=" + packet.g2());
                    }
                }
            }

            case 41 -> {
                if (Unpack.VERSION < 400) {
                    lines.add("recol2d=" + packet.g2());
                } else {
                    var count = packet.g1();

                    for (var i = 0; i < count; ++i) {
                        lines.add("retex" + (i + 1) + "s=" + Unpacker.format(Type.MATERIAL, packet.g2()));
                        lines.add("retex" + (i + 1) + "d=" + Unpacker.format(Type.MATERIAL, packet.g2()));
                    }
                }
            }

            case 42 -> lines.add("recol3s=" + packet.g2());
            case 43 -> lines.add("recol4s=" + packet.g2());

            case 44 -> {
                if (Unpack.VERSION < 400) {
                    lines.add("recol5s=" + packet.g2());
                } else {
                    lines.add("recolindices=" + Unpacker.formatRecolRetexIndexList(packet.g2()));
                }
            }

            case 45 -> {
                if (Unpack.VERSION < 400) {
                    lines.add("recol6s=" + packet.g2());
                } else {
                    lines.add("retexindices=" + Unpacker.formatRecolRetexIndexList(packet.g2()));
                }
            }

            case 46 -> lines.add("recol7s=" + packet.g2());
            case 47 -> lines.add("recol8s=" + packet.g2());
            case 48 -> lines.add("recol9s=" + packet.g2());
            case 49 -> lines.add("recol10s=" + packet.g2());

            case 50 -> lines.add("recol1d=" + packet.g2());
            case 51 -> lines.add("recol2d=" + packet.g2());
            case 52 -> lines.add("recol3d=" + packet.g2());
            case 53 -> lines.add("recol4d=" + packet.g2());
            case 54 -> lines.add("recol5d=" + packet.g2());
            case 55 -> lines.add("recol6d=" + packet.g2());
            case 56 -> lines.add("recol7d=" + packet.g2());
            case 57 -> lines.add("recol8d=" + packet.g2());
            case 58 -> lines.add("recol9d=" + packet.g2());
            case 59 -> lines.add("recol10d=" + packet.g2());

            case 60 -> lines.add("head1=" + Unpacker.format(Type.MODEL, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()));
            case 61 -> lines.add("head2=" + Unpacker.format(Type.MODEL, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()));
            case 62 -> lines.add("head3=" + Unpacker.format(Type.MODEL, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()));
            case 63 -> lines.add("head4=" + Unpacker.format(Type.MODEL, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()));
            case 64 -> lines.add("head5=" + Unpacker.format(Type.MODEL, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()));
            case 65 -> lines.add("head6=" + Unpacker.format(Type.MODEL, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()));
            case 66 -> lines.add("head7=" + Unpacker.format(Type.MODEL, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()));
            case 67 -> lines.add("head8=" + Unpacker.format(Type.MODEL, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()));
            case 68 -> lines.add("head9=" + Unpacker.format(Type.MODEL, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()));
            case 69 -> lines.add("head10=" + Unpacker.format(Type.MODEL, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()));
            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
