package rs3.unpack.config;

import rs3.unpack.ColourConversion;
import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
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

                lines = Unpacker.transformRecolRetexIndices(lines);
                return lines;
            }

            case 1 -> { // https://discord.com/channels/@me/698790755363323904/1203639168836833340
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("model" + (i + 1) + "=" + Unpacker.format(Type.MODEL, Unpack.VERSION < 681 ? packet.g2null() : packet.gSmart2or4null()));
                }
            }

            case 2 -> lines.add("name=" + packet.gjstr()); // https://discord.com/channels/@me/698790755363323904/1203639168836833340
            case 3 -> lines.add("desc=" + packet.gjstr());
            case 12 -> lines.add("size=" + packet.g1()); // https://discord.com/channels/@me/698790755363323904/1203639168836833340
            case 13 -> lines.add("readyanim=" + Unpacker.format(Type.SEQ, packet.g2()));
            case 14 -> lines.add("walkanim=" + Unpacker.format(Type.SEQ, packet.g2())); // https://twitter.com/JagexAsh/status/1782360089321447453
            case 15 -> lines.add("turnleftanim=" + Unpacker.format(Type.SEQ, packet.g2()));

            case 16 -> {
                if (Unpack.VERSION < 300) {
                    lines.add("hasanim=yes"); // todo
                } else {
                    lines.add("turnrightanim=" + Unpacker.format(Type.SEQ, packet.g2()));
                }
            }

            case 17 -> lines.add("walkanim=" + Unpacker.format(Type.SEQ, packet.g2()) + "," + Unpacker.format(Type.SEQ, packet.g2()) + "," + Unpacker.format(Type.SEQ, packet.g2()) + "," + Unpacker.format(Type.SEQ, packet.g2())); // https://twitter.com/JagexAsh/status/1782360089321447453
            case 18 -> lines.add("category=" + Unpacker.format(Type.CATEGORY, packet.g2())); // https://discord.com/channels/@me/698790755363323904/1203639168836833340
            case 30 -> lines.add("op1=" + packet.gjstr()); // https://discord.com/channels/@me/698790755363323904/1203639168836833340
            case 31 -> lines.add("op2=" + packet.gjstr()); // https://discord.com/channels/@me/698790755363323904/1203639168836833340
            case 32 -> lines.add("op3=" + packet.gjstr()); // https://discord.com/channels/@me/698790755363323904/1203639168836833340
            case 33 -> lines.add("op4=" + packet.gjstr()); // https://discord.com/channels/@me/698790755363323904/1203639168836833340
            case 34 -> lines.add("op5=" + packet.gjstr()); // https://discord.com/channels/@me/698790755363323904/1203639168836833340

            case 39 -> lines.add("unknown39=" + packet.g1());

            case 40 -> {
                var length = packet.g1();

                for (var i = 0; i < length; ++i) {
                    if (Unpack.VERSION < 469) {
                        lines.add("recol" + (i + 1) + "s=" + ColourConversion.reverseRGBFromHSL(packet.g2()));
                        lines.add("recol" + (i + 1) + "d=" + ColourConversion.reverseRGBFromHSL(packet.g2()));
                    } else {
                        lines.add("recol" + (i + 1) + "s=" + packet.g2());
                        lines.add("recol" + (i + 1) + "d=" + packet.g2());
                    }
                }
            }

            case 41 -> {
                var length = packet.g1();

                for (var i = 0; i < length; ++i) {
                    lines.add("retex" + (i + 1) + "s=" + Unpacker.format(Type.MATERIAL, packet.g2()));
                    lines.add("retex" + (i + 1) + "d=" + Unpacker.format(Type.MATERIAL, packet.g2()));
                }
            }

            case 42 -> {
                var count = packet.g1();

                for (var i = 0; i < count; ++i) {
                    lines.add("unknown42=" + packet.g1s());
                }
            }

            case 44 -> lines.add("recolindices=" + Unpacker.formatRecolRetexIndexList(packet.g2()));
            case 45 -> lines.add("retexindices=" + Unpacker.formatRecolRetexIndexList(packet.g2()));

            case 60 -> {
                var length = packet.g1();

                for (var i = 0; i < length; i++) {
                    lines.add("head" + (i + 1) + "=" + Unpacker.format(Type.MODEL, Unpack.VERSION < 681 ? packet.g2null() : packet.gSmart2or4null()));
                }
            }

            case 93 -> lines.add("minimap=no"); // https://twitter.com/JagexAsh/status/1763550956443111935
            case 95 -> lines.add("vislevel=" + packet.g2());
            case 97 -> lines.add("resizeh=" + packet.g2()); // html5 (only resize)
            case 98 -> lines.add("resizev=" + packet.g2()); // html5 (only resize)
            case 99 -> lines.add("alwaysontop=yes"); // https://twitter.com/JagexAsh/status/1690998554347610112
            case 100 -> lines.add("ambient=" + packet.g1s());
            case 101 -> lines.add("contrast=" + packet.g1s());

            case 102 -> {
                if (Unpack.VERSION < 809) {
                    lines.add("headicon=" + packet.g2());
                } else {
                    var filter = packet.g1();

                    for (var i = 0; i < 8; ++i) {
                        if ((filter & 1 << i) != 0) {
                            lines.add("headicon" + (i + 1) + "=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()) + "," + packet.gSmart1or2null());
                        }
                    }
                }
            }

            case 103 -> lines.add("turnspeed=" + packet.g2());

            case 106 -> {
                var multivarbit = packet.g2null();

                if (multivarbit != -1) {
                    lines.add("multivar=" + Unpacker.format(Type.VARBIT, multivarbit));
                }

                var multivarp = packet.g2null();

                if (multivarp != -1) {
                    lines.add("multivar=" + Unpacker.format(Type.VAR_PLAYER, multivarp));
                }

                var count = Unpack.VERSION >= 910 ? packet.gSmart1or2() : packet.g1();

                for (var i = 0; i <= count; ++i) {
                    var multi = packet.g2null();

                    if (multi != -1) {
                        lines.add("multinpc=" + i + "," + Unpacker.format(Type.NPC, multi));
                    }
                }
            }

            case 107 -> lines.add("active=no");
            case 109 -> lines.add("walksmoothing=no"); // nxt
            case 111 -> lines.add("spotshadow=no");
            case 113 -> lines.add("spotshadowcolour=" + packet.g2() + "," + packet.g2());
            case 114 -> lines.add("spotshadowtrans=" + packet.g1s() + "," + packet.g1s());
            case 115 -> lines.add("unknown115=" + packet.g1() + "," + packet.g1());

            case 118 -> {
                var multivarbit = packet.g2null();

                if (multivarbit != -1) {
                    lines.add("multivar=" + Unpacker.format(Type.VARBIT, multivarbit));
                }

                var multivarp = packet.g2null();

                if (multivarp != -1) {
                    lines.add("multivar=" + Unpacker.format(Type.VAR_PLAYER, multivarp));
                }

                var multidefault = packet.g2null();

                if (multidefault != -1) {
                    lines.add("multinpc=default," + Unpacker.format(Type.NPC, multidefault));
                }

                var count = Unpack.VERSION >= 910 ? packet.gSmart1or2() : packet.g1();

                for (var i = 0; i <= count; ++i) {
                    var multi = packet.g2null();

                    if (multi != -1) {
                        lines.add("multinpc=" + i + "," + Unpacker.format(Type.NPC, multi));
                    }
                }
            }

            case 119 -> lines.add("unknown119=" + packet.g1s());
            case 120 -> lines.add("unknown120=" + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g1());

            case 121 -> {
                var count = packet.g1();

                for (var i = 0; i < count; ++i) {
                    lines.add("modeloffset" + packet.g1() + "=" + packet.g1s() + "," + packet.g1s() + "," + packet.g1s());
                }
            }

            case 122 -> lines.add("unknown122=" + packet.g2()); // todo: removed
            case 123 -> lines.add("overlayheight=" + packet.g2());
            case 125 -> lines.add("respawndir=" + packet.g1s());
            case 127 -> lines.add("bas=" + Unpacker.format(Type.BAS, packet.g2())); // https://discord.com/channels/@me/698790755363323904/1203639168836833340
            case 128 -> lines.add("defaultmovemode=" + Unpacker.format(Type.MOVESPEED, packet.g1())); // https://discord.com/channels/@me/698790755363323904/1203639168836833340
            case 134 -> lines.add("bgsound=" + Unpacker.format(Unpacker.CONFIG_SOUND_TYPE, packet.g2null()) + "," + Unpacker.format(Unpacker.CONFIG_SOUND_TYPE, packet.g2null()) + "," + Unpacker.format(Unpacker.CONFIG_SOUND_TYPE, packet.g2null()) + "," + Unpacker.format(Unpacker.CONFIG_SOUND_TYPE, packet.g2null()) + "," + packet.g1());
            case 135 -> lines.add("cursor1=" + (packet.g1() + 1) + "," + Unpacker.format(Type.CURSOR, packet.g2()));
            case 136 -> lines.add("cursor2=" + (packet.g1() + 1) + "," + Unpacker.format(Type.CURSOR, packet.g2()));
            case 137 -> lines.add("cursorattack=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 138 -> lines.add("covermarker=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 139 -> lines.add("unknown139=" + packet.gSmart2or4null());
            case 140 -> lines.add("bgsoundvolume=" + packet.g1());
            case 141 -> lines.add("follower=yes");
            case 142 -> lines.add("mapelement=" + Unpacker.format(Type.MAPELEMENT, packet.g2()));
            case 143 -> lines.add("drawbelow=yes");
            case 150 -> lines.add("membersop1=" + packet.gjstr());
            case 151 -> lines.add("membersop2=" + packet.gjstr());
            case 152 -> lines.add("membersop3=" + packet.gjstr());
            case 153 -> lines.add("membersop4=" + packet.gjstr());
            case 154 -> lines.add("membersop5=" + packet.gjstr());
            case 155 -> lines.add("tint=" + packet.g1s() + "," + packet.g1s() + "," + packet.g1s() + "," + packet.g1s());
            case 158 -> lines.add("reprioritiseattackop=yes"); // https://discord.com/channels/@me/698790755363323904/1203639168836833340
            case 159 -> lines.add("reprioritiseattackop=no"); // https://discord.com/channels/@me/698790755363323904/1203639168836833340

            case 160 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("quest=" + Unpacker.format(Type.QUEST, packet.g2()));
                }
            }

            case 162 -> lines.add("unknown162=yes");
            case 163 -> lines.add("picksize=" + packet.g1());
            case 164 -> lines.add("bgsoundrate=" + packet.g2() + "," + packet.g2());
            case 165 -> lines.add("picksizeshift=" + packet.g1());
            case 168 -> lines.add("bgsoundsize=" + packet.g1());
            case 169 -> lines.add("antimacro=no");
            case 170 -> lines.add("cursor1=" + Unpacker.format(Type.CURSOR, packet.g2null()));
            case 171 -> lines.add("cursor2=" + Unpacker.format(Type.CURSOR, packet.g2null()));
            case 172 -> lines.add("cursor3=" + Unpacker.format(Type.CURSOR, packet.g2null()));
            case 173 -> lines.add("cursor4=" + Unpacker.format(Type.CURSOR, packet.g2null()));
            case 174 -> lines.add("cursor5=" + Unpacker.format(Type.CURSOR, packet.g2null()));
            case 175 -> lines.add("cursor6=" + Unpacker.format(Type.CURSOR, packet.g2null()));
            case 178 -> lines.add("unknown178=no");
            case 179 -> lines.add("clickbox=" + packet.gSmart1or2() + "," + packet.gSmart1or2() + "," + packet.gSmart1or2() + "," + packet.gSmart1or2() + "," + packet.gSmart1or2() + "," + packet.gSmart1or2());
            case 180 -> lines.add("unknown180=" + packet.g1());
            case 181 -> lines.add("spotshadowtexture=" + Unpacker.format(Type.MATERIAL, packet.g2()) + "," + packet.g1());
            case 182 -> lines.add("transmogfakenpc=yes");
            case 184 -> lines.add("unknown184=" + packet.g1());
            case 185 -> lines.add("unknown185=no");

            case 186 -> {
                packet.g2();
                lines.add("multivar=" + Unpacker.format(Type.VARBIT, packet.g2null()));
                lines.add("multivar=" + Unpacker.format(Type.VAR_PLAYER, packet.g2null()));

                var flags = packet.g1();

                if ((flags & 1) != 0) {
                    var length = packet.g1();

                    for (var i = 0; i < length; i++) {
                        var value = packet.g1();
                        var length2 = packet.g1();

                        for (var j = 0; j < length2; j++) {
                            var line = "multimodel=" + value + "," + packet.g2() + "," + packet.g2() + "," + packet.gSmart2or4s();
                            var n = packet.g1();
                            if (n >= 1) line += "," + packet.g1();
                            if (n >= 2) line += "," + packet.g1();
                            if (n >= 3) line += "," + packet.g1();
                            lines.add(line);
                        }
                    }
                }

                if ((flags & 2) != 0) {
                    var length = packet.g1();

                    for (var i = 0; i < length; i++) {
                        var value = packet.g1();
                        var length2 = packet.g1();

                        for (var j = 0; j < length2; j++) {
                            lines.add("multiheadmodel=" + value + "," + packet.g2() + "," + packet.g2() + "," + packet.gSmart2or4s());
                        }
                    }
                }

                if ((flags & 4) != 0) {
                    var length = packet.g1();

                    for (var i = 0; i < length; i++) {
                        var value = packet.g1();
                        var length2 = packet.g1();

                        for (var j = 0; j < length2; j++) {
                            lines.add("multiretex=" + value + "," + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2());
                        }
                    }
                }

                if ((flags & 8) != 0) {
                    var length = packet.g1();

                    for (var i = 0; i < length; i++) {
                        var value = packet.g1();
                        var length2 = packet.g1();

                        for (var j = 0; j < length2; j++) {
                            lines.add("multirecol=" + value + "," + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2());
                        }
                    }
                }

                if ((flags & 16) != 0) {
                    var length = packet.g1();

                    for (var i = 0; i < length; i++) {
                        var value = packet.g1();
                        lines.add("multitint=" + value + "," + packet.g2() + "," + packet.g2() + "," + packet.g1() + "," + packet.g1() + "," + packet.g1() + "," + packet.g1());
                    }
                }

                lines.add("multidefault=" + packet.g2());
            }

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
