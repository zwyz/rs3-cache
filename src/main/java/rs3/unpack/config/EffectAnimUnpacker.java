package rs3.unpack.config;

import rs3.unpack.ColourConversion;
import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class EffectAnimUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.SPOTANIM, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                lines = Unpacker.transformRecolRetexIndices(lines);
                return lines;
            }

            case 1 -> lines.add("model=" + Unpacker.format(Type.MODEL, Unpack.VERSION < 681 ? packet.g2null() : packet.gSmart2or4null()));
            case 2 -> lines.add("anim=" + Unpacker.format(Type.SEQ, packet.gSmart2or4null()));
            case 3 -> lines.add("hasalpha=yes");
            case 4 -> lines.add("resizeh=" + packet.g2());
            case 5 -> lines.add("resizev=" + packet.g2());
            case 6 -> lines.add("rotation=" + packet.g2());
            case 7 -> lines.add("ambient=" + packet.g1());
            case 8 -> lines.add("contrast=" + packet.g1());
            case 10 -> lines.add("allowloop=yes");

            case 9 -> lines.add("hillchange=rotate");
            case 15 -> lines.add("hillchange=rotate," + packet.g2());
            case 16 -> lines.add("hillchange=rotate," + packet.g4s());

            case 40 -> {
                if (Unpack.VERSION < 465) {
                    lines.add("recol1s=" + packet.g2());
                } else {
                    var count = packet.g1();

                    for (var i = 0; i < count; i++) {
                        if (Unpack.VERSION < 500) {
                            lines.add("recol" + (i + 1) + "s=" + ColourConversion.reverseRGBFromHSL(packet.g2()));
                            lines.add("recol" + (i + 1) + "d=" + ColourConversion.reverseRGBFromHSL(packet.g2()));
                        } else {
                            lines.add("recol" + (i + 1) + "s=" + packet.g2());
                            lines.add("recol" + (i + 1) + "d=" + packet.g2());
                        }
                    }
                }
            }

            case 41 -> {
                if (Unpack.VERSION < 465) {
                    lines.add("recol2s=" + packet.g2());
                } else {
                    var count = packet.g1();

                    for (var i = 0; i < count; i++) {
                        lines.add("retex" + (i + 1) + "s=" + Unpacker.format(Type.MATERIAL, packet.g2()));
                        lines.add("retex" + (i + 1) + "d=" + Unpacker.format(Type.MATERIAL, packet.g2()));
                    }
                }
            }

            case 42 -> lines.add("recol3s=" + packet.g2());
            case 43 -> lines.add("recol4s=" + packet.g2());

            case 44 -> {
                if (Unpack.VERSION < 465) {
                    lines.add("recol5s=" + packet.g2());
                } else {
                    lines.add("recolindices=" + Unpacker.formatRecolRetexIndexList(packet.g2()));
                }
            }

            case 45 -> {
                if (Unpack.VERSION < 465) {
                    lines.add("recol6s=" + packet.g2());
                } else {
                    lines.add("retexindices=" + Unpacker.formatRecolRetexIndexList(packet.g2()));
                }
            }

            case 46 -> {
                if (Unpack.VERSION < 465) {
                    lines.add("recol7s=" + packet.g2());
                } else {
                    lines.add("unknown46=yes");
                }
            }

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

            default -> throw new IllegalStateException("unknown opcode");
        }
    }

}
