package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.unpack.VarDomain;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class NPCUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.NPC, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("model=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
                }
            }

            case 2 -> lines.add("name=" + packet.gjstr());
            case 12 -> lines.add("size=" + packet.g1());
            case 13 -> lines.add("readyanim=" + Unpacker.format(Type.SEQ, packet.g2()));
            case 14 -> lines.add("walkanim=" + Unpacker.format(Type.SEQ, packet.g2()));
            case 15 -> lines.add("turnleftanim=" + Unpacker.format(Type.SEQ, packet.g2()));
            case 16 -> lines.add("turnrightanim=" + Unpacker.format(Type.SEQ, packet.g2()));
            case 17 -> lines.add("walkanim=" + Unpacker.format(Type.SEQ, packet.g2()) + "," + Unpacker.format(Type.SEQ, packet.g2()) + "," + Unpacker.format(Type.SEQ, packet.g2()) + "," + Unpacker.format(Type.SEQ, packet.g2()));
            case 18 -> lines.add("category=" + Unpacker.format(Type.CATEGORY, packet.g2()));
            case 30 -> lines.add("op1=" + packet.gjstr());
            case 31 -> lines.add("op2=" + packet.gjstr());
            case 32 -> lines.add("op3=" + packet.gjstr());
            case 33 -> lines.add("op4=" + packet.gjstr());
            case 34 -> lines.add("op5=" + packet.gjstr());

            case 39 -> lines.add("unknown39=" + packet.g1());

            case 40 -> {
                var length = packet.g1();

                for (var i = 0; i < length; ++i) {
                    lines.add("recol=" + packet.g2() + "," + packet.g2());
                }
            }

            case 41 -> {
                var length = packet.g1();

                for (var i = 0; i < length; ++i) {
                    lines.add("retex=" + packet.g2() + "," + packet.g2());
                }
            }

            case 42 -> {
                var count = packet.g1();

                for (var i = 0; i < count; ++i) {
                    lines.add("unknown42=" + packet.g1s());
                }
            }

            case 44 -> lines.add("unknown44=" + packet.g2());
            case 45 -> lines.add("unknown45=" + packet.g2());

            case 60 -> {
                var length = packet.g1();

                for (var i = 0; i < length; i++) {
                    lines.add("headmodel=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
                }
            }

            case 93 -> lines.add("visonmap=no");
            case 95 -> lines.add("vislevel=" + packet.g2());
            case 97 -> lines.add("resizeh=" + packet.g2());
            case 98 -> lines.add("resizev=" + packet.g2());
            case 99 -> lines.add("drawpriority=yes");
            case 100 -> lines.add("ambient=" + packet.g1s());
            case 101 -> lines.add("contrast=" + packet.g1s());

            case 102 -> {
                var filter = packet.g1();

                for (var i = 0; i < 8; ++i) {
                    if ((filter & 1 << i) != 0) {
                        lines.add("headicon=" + packet.gSmart2or4null() + "," + packet.gSmart1or2null());
                    }
                }
            }

            case 103 -> lines.add("turnspeed=" + packet.g2());

            case 106 -> {
                var multivarbit = packet.g2null();

                if (multivarbit != -1) {
                    lines.add("multivar=" + Unpacker.formatVarBit(multivarbit));
                }

                var multivarp = packet.g2null();

                if (multivarp != -1) {
                    lines.add("multivar=" + Unpacker.formatVar(VarDomain.PLAYER, multivarp));
                }

                var count = packet.gSmart1or2();

                for (var i = 0; i <= count; ++i) {
                    var multi = packet.g2null();

                    if (multi != -1) {
                        lines.add("multinpc=" + i + "," + Unpacker.format(Type.NPC, multi));
                    }
                }
            }

            case 107 -> lines.add("active=no");
            case 109 -> lines.add("walksmoothing=no");
            case 111 -> lines.add("unknown111=no");
            case 113 -> lines.add("unknown113=" + packet.g2() + "," + packet.g2());
            case 114 -> lines.add("unknown114=" + packet.g1s() + "," + packet.g1s());

            case 118 -> {
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
                    lines.add("multinpc=default," + Unpacker.format(Type.LOC, multidefault));
                }

                var count = packet.gSmart1or2();

                for (var i = 0; i <= count; ++i) {
                    var multi = packet.g2null();

                    if (multi != -1) {
                        lines.add("multinpc=" + i + "," + Unpacker.format(Type.LOC, multi));
                    }
                }
            }

            case 119 -> lines.add("walkflags=" + packet.g1s());

            case 121 -> {
                var count = packet.g1();

                for (var i = 0; i < count; ++i) {
                    lines.add("unknown121=" + packet.g1() + "," + packet.g1s() + "," + packet.g1s() + "," + packet.g1s());
                }
            }

            case 123 -> lines.add("unknown123=" + packet.g2());
            case 125 -> lines.add("unknown125=" + packet.g1s());
            case 127 -> lines.add("bas=" + packet.g2());
            case 128 -> lines.add("unknown128=" + packet.g1());
            case 134 -> lines.add("unknown134=" + packet.g2null() + "," + packet.g2null() + "," + packet.g2null() + "," + packet.g2null() + "," + packet.g1());
            case 135 -> lines.add("unknown135=" + packet.g1() + "," + packet.g2());
            case 136 -> lines.add("unknown135=" + packet.g1() + "," + packet.g2());
            case 137 -> lines.add("unknown137=" + packet.g2());
            case 138 -> lines.add("covermarker=" + packet.gSmart2or4null());
            case 139 -> lines.add("unknown139=" + packet.gSmart2or4null());
            case 140 -> lines.add("unknown140=" + packet.g1());
            case 141 -> lines.add("unknown141=yes");
            case 142 -> lines.add("unknown142=" + packet.g2());
            case 143 -> lines.add("unknown143=yes");
            case 150 -> lines.add("membersop1=" + packet.gjstr());
            case 151 -> lines.add("membersop2=" + packet.gjstr());
            case 152 -> lines.add("membersop3=" + packet.gjstr());
            case 153 -> lines.add("membersop4=" + packet.gjstr());
            case 154 -> lines.add("membersop5=" + packet.gjstr());
            case 155 -> lines.add("tint=" + packet.g1s() + "," + packet.g1s() + "," + packet.g1s() + "," + packet.g1s());
            case 158 -> lines.add("unknown158=yes");
            case 159 -> lines.add("unknown158=no");

            case 160 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("quest=" + Unpacker.format(Type.QUEST, packet.g2()));
                }
            }

            case 162 -> lines.add("unknown162=yes");
            case 163 -> lines.add("unknown163=" + packet.g1());
            case 164 -> lines.add("unknown164=" + packet.g2() + "," + packet.g2());
            case 165 -> lines.add("picksizeshift=" + packet.g1());
            case 168 -> lines.add("unknown168=" + packet.g1());
            case 169 -> lines.add("antimacro=no");
            case 170 -> lines.add("unknown170=" + packet.g2null());
            case 171 -> lines.add("unknown171=" + packet.g2null());
            case 172 -> lines.add("unknown172=" + packet.g2null());
            case 173 -> lines.add("unknown173=" + packet.g2null());
            case 174 -> lines.add("unknown174=" + packet.g2null());
            case 175 -> lines.add("unknown175=" + packet.g2null());
            case 178 -> lines.add("unknown178=yes");
            case 179 -> lines.add("clickbox=" + packet.gSmart1or2() + "," + packet.gSmart1or2() + "," + packet.gSmart1or2() + "," + packet.gSmart1or2() + "," + packet.gSmart1or2() + "," + packet.gSmart1or2());
            case 180 -> lines.add("unknown180=" + packet.g1());
            case 181 -> lines.add("unknown181=" + packet.g2() + "," + packet.g1());
            case 182 -> lines.add("unknown182=yes");
            case 184 -> lines.add("unknown182=" + packet.g1());
            case 185 -> lines.add("unknown185=yes");

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

            case 252 -> lines.add("unknown252=" + packet.g2());
            case 253 -> lines.add("unknown253=" + packet.g1());

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
