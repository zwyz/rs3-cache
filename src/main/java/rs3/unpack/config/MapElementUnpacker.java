package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.unpack.VarDomain;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class MapElementUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.MAPELEMENT, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("graphic=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 2 -> lines.add("unknown2=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 3 -> lines.add("text=" + packet.gjstr());
            case 4 -> lines.add("colour=" + packet.g3());
            case 5 -> lines.add("unknown5=" + packet.g3());
            case 6 -> lines.add("size=" + packet.g1());
            case 7 -> lines.add("vis=" + packet.g1());
            case 8 -> lines.add("unknown8=" + packet.g1());
            case 9 -> lines.add("unknown9=" + packet.g2null() + "," + packet.g2null() + "," + packet.g4s() + "," + packet.g4s());
            case 10 -> lines.add("op1=" + packet.gjstr());
            case 11 -> lines.add("op2=" + packet.gjstr());
            case 12 -> lines.add("op3=" + packet.gjstr());
            case 13 -> lines.add("op4=" + packet.gjstr());
            case 14 -> lines.add("op5=" + packet.gjstr());

            case 15 -> {
                var count1 = packet.g1();

                for (var i = 0; i < count1 * 2; ++i) {
                    lines.add("unknown15a=" + packet.g2s());
                }

                lines.add("unknown15b=" + packet.g4s());
                var count2 = packet.g1();

                for (var i = 0; i < count2; ++i) {
                    lines.add("unknown15c=" + packet.g4s());
                }

                for (var i = 0; i < count1; ++i) {
                    lines.add("unknown15d=" + packet.g1s());
                }
            }

            case 16 -> lines.add("unknown16=yes");
            case 17 -> lines.add("opbase=" + packet.gjstr());
            case 18 -> lines.add("unknown18=" + packet.gSmart2or4null());
            case 19 -> lines.add("category=" + Unpacker.format(Type.CATEGORY, packet.g2()));
            case 20 -> lines.add("unknown20=" + packet.g2null() + "," + packet.g2null() + "," + packet.g4s() + "," + packet.g4s());
            case 21 -> lines.add("unknown21=" + packet.g4s());
            case 22 -> lines.add("unknown22=" + packet.g4s());
            case 23 -> lines.add("unknown23=" + packet.g1() + "," + packet.g1() + "," + packet.g1());
            case 24 -> lines.add("unknown24=" + packet.g2s() + "," + packet.g2s());
            case 25 -> lines.add("unknown25=" + packet.gSmart2or4null());

            case 26 -> {
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
                        lines.add("multimapelement=" + i + "," + Unpacker.format(Type.MAPELEMENT, multi));
                    }
                }
            }

            case 27 -> {
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
                    lines.add("multimel=default," + Unpacker.format(Type.HITMARK, multidefault));
                }

                var count = packet.g1();

                for (var i = 0; i <= count; ++i) {
                    var multi = packet.g2null();

                    if (multi != -1) {
                        lines.add("multimel=" + i + "," + Unpacker.format(Type.HITMARK, multi));
                    }
                }
            }

            case 28 -> lines.add("unknown28=" + packet.g1());
            case 29 -> lines.add("alignx=" + packet.g1());
            case 30 -> lines.add("aligny=" + packet.g1());

            case 249 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    if (packet.g1() == 1) {
                        lines.add("param=" + Unpacker.format(Type.PARAM, packet.g3()) + "," + packet.gjstr());
                    } else {
                        var param = packet.g3();
                        lines.add("param=" + Unpacker.format(Type.PARAM, param) + "," + Unpacker.format(Unpacker.getParamType(param), packet.g4s()));
                    }
                }
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
