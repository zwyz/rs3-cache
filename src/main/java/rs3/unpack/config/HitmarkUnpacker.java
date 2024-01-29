package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.unpack.VarDomain;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class HitmarkUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.HITMARK, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("damagefont=" + packet.gSmart2or4null());
            case 2 -> lines.add("damagecolour=" + packet.g3());
            case 3 -> lines.add("classgraphic=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 4 -> lines.add("leftgraphic=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 5 -> lines.add("middlegraphic=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 6 -> lines.add("rightgraphic=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 7 -> lines.add("scrolltooffsetx=" + packet.g2s());
            case 8 -> lines.add("damageformat=" + packet.gjstr2());
            case 9 -> lines.add("sticktime=" + packet.g2());
            case 10 -> lines.add("scrolltooffsety=" + packet.g2s());
            case 11 -> lines.add("fadeout=no");
            case 12 -> lines.add("replacemode=" + packet.g1());
            case 13 -> lines.add("damageyof=" + packet.g2s());
            case 14 -> lines.add("fadeout=" + packet.g2());
            case 16 -> lines.add("graphicof=" + packet.g2() + "," + packet.g2());

            case 17 -> {
                var multivarbit = packet.g2null();

                if (multivarbit != -1) {
                    lines.add("multivar=" + Unpacker.formatVarBit(multivarbit));
                }

                var multivarp = packet.g2null();

                if (multivarp != -1) {
                    lines.add("multivar=" + Unpacker.formatVar(VarDomain.PLAYER, multivarp));
                }

                var count = packet.g1();

                for (var i = 0; i <= count; ++i) {
                    var multi = packet.g2null();

                    if (multi != -1) {
                        lines.add("multimark=" + i + "," + Unpacker.format(Type.HITMARK, multi));
                    }
                }
            }

            case 18 -> {
                var multivarbit = packet.g2null();

                if (multivarbit != -1) {
                    lines.add("multivar=" + Unpacker.formatVarBit(multivarbit));
                }

                var multivarp = packet.g2null();

                if (multivarp != -1) {
                    lines.add("multivar=" + Unpacker.formatVar(VarDomain.PLAYER, multivarp));
                }

                var multidefault = packet.g2null();

                if (multidefault != -1) {
                    lines.add("multimark=default," + Unpacker.format(Type.HITMARK, multidefault));
                }

                var count = packet.g1();

                for (var i = 0; i <= count; ++i) {
                    var multi = packet.g2null();

                    if (multi != -1) {
                        lines.add("multimark=" + i + "," + Unpacker.format(Type.HITMARK, multi));
                    }
                }
            }

            case 19 -> lines.add("damagescaleto=" + packet.g2());
            case 20 -> lines.add("damagescalefrom=" + packet.g2());
            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
