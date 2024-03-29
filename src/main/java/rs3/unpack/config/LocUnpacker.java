package rs3.unpack.config;

import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class LocUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.LOC, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                lines = Unpacker.transformRecolRetexIndices(lines);
                return lines;
            }

            case 1 -> {
                if (Unpack.VERSION < 600) {
                    var count = packet.g1();

                    for (var i = 0; i < count; ++i) {
                        lines.add("model=" + Unpacker.format(Type.MODEL, packet.g2()) + "," + Unpacker.format(Type.LOC_SHAPE, packet.g1()));
                    }
                } else {
                    var shapeCount = packet.g1();

                    for (var i = 0; i < shapeCount; ++i) {
                        var shape = Unpacker.format(Type.LOC_SHAPE, packet.g1s());
                        var modelCount = packet.g1();

                        for (var j = 0; j < modelCount; j++) {
                            lines.add("model=" + shape + "," + Unpacker.format(Type.MODEL, Unpack.VERSION <= 700 ? packet.g2null() : packet.gSmart2or4null()));
                        }
                    }
                }
            }

            case 2 -> lines.add("name=" + packet.gjstr()); // https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00
            case 3 -> lines.add("desc=" + packet.gjstr());

            case 5 -> {
                if (Unpack.VERSION < 600) {
                    var count = packet.g1();

                    for (var i = 0; i < count; ++i) {
                        lines.add("model=" + Unpacker.format(Type.MODEL, packet.g2())); // https://www.youtube.com/watch?v=vZ7oG1IDz1w 2:09:30
                    }
                } else {
                    var shapeCount1 = packet.g1();

                    for (var i = 0; i < shapeCount1; ++i) {
                        var shape = Unpacker.format(Type.LOC_SHAPE, packet.g1s());
                        var modelCount = packet.g1();

                        for (var j = 0; j < modelCount; j++) {
                            lines.add("modela=" + shape + "," + Unpacker.format(Type.MODEL, Unpack.VERSION <= 700 ? packet.g2null() : packet.gSmart2or4null()));
                        }
                    }

                    var shapeCount2 = packet.g1();

                    for (var i = 0; i < shapeCount2; ++i) {
                        var shape = Unpacker.format(Type.LOC_SHAPE, packet.g1s());
                        var modelCount = packet.g1();

                        for (var j = 0; j < modelCount; j++) {
                            lines.add("modelb=" + shape + "," + Unpacker.format(Type.MODEL, Unpack.VERSION <= 700 ? packet.g2null() : packet.gSmart2or4null()));
                        }
                    }
                }
            }

            case 14 -> lines.add("width=" + packet.g1()); // https://www.youtube.com/watch?v=vZ7oG1IDz1w
            case 15 -> lines.add("length=" + packet.g1()); // https://www.youtube.com/watch?v=vZ7oG1IDz1w
            case 17 -> lines.add("blockwalk=no"); // https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00
            case 18 -> lines.add("blockrange=no"); // https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00
            case 19 -> lines.add("active=" + Unpacker.formatBoolean(packet.g1())); //https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00
            case 21 -> lines.add("hillskew=yes"); // https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00
            case 22 -> lines.add("sharelight=yes"); // https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00
            case 23 -> lines.add("occlude=yes"); // https://www.youtube.com/watch?v=vZ7oG1IDz1w 2:09:30
            case 24 -> lines.add("anim=" + Unpacker.format(Type.SEQ, packet.gSmart2or4null()));
            case 25 -> lines.add("hasalpha=yes"); // todo
            case 27 -> lines.add("blockwalk=yes"); // https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00
            case 28 -> lines.add("wallwidth=" + packet.g1()); // * https://discord.com/channels/@me/698790755363323904/1131401170045374545
            case 29 -> lines.add("ambient=" + packet.g1s()); // https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00
            case 39 -> lines.add("contrast=" + packet.g1s()); // https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00
            case 30 -> lines.add("op1=" + packet.gjstr()); // https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00
            case 31 -> lines.add("op2=" + packet.gjstr()); // https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00
            case 32 -> lines.add("op3=" + packet.gjstr()); // https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00
            case 33 -> lines.add("op4=" + packet.gjstr()); // https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00
            case 34 -> lines.add("op5=" + packet.gjstr()); // https://www.youtube.com/watch?v=ovGBifJR4Fs 4:38:00

            case 40 -> { // https://www.youtube.com/watch?v=vZ7oG1IDz1w 2:09:30
                var count = packet.g1();

                for (var i = 0; i < count; ++i) {
                    lines.add("recol" + (i + 1) + "s=" + packet.g2());
                    lines.add("recol" + (i + 1) + "d=" + packet.g2());
                }
            }

            case 41 -> { // https://www.youtube.com/watch?v=vZ7oG1IDz1w 2:09:30
                var count = packet.g1();

                for (var i = 0; i < count; ++i) {
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
            case 60 -> lines.add("mapfunction=" + packet.g2());
            case 61 -> lines.add("category=" + Unpacker.format(Type.CATEGORY, packet.g2()));
            case 62 -> lines.add("mirror=yes");
            case 64 -> lines.add("shadow=no"); // https://www.youtube.com/watch?v=vZ7oG1IDz1w 2:09:30
            case 65 -> lines.add("resizex=" + packet.g2()); // html5 (only resize)
            case 66 -> lines.add("resizey=" + packet.g2()); // html5 (only resize)
            case 67 -> lines.add("resizez=" + packet.g2()); // html5 (only resize)
            case 68 -> lines.add("mapscene=" + packet.g2());

            case 69 -> { // https://twitter.com/JagexAsh/status/1641051532010434560
                int blocked = packet.g1s();
                var result = new ArrayList<String>();

                if ((blocked & 1) == 0) result.add("north");
                if ((blocked & 2) == 0) result.add("east");
                if ((blocked & 4) == 0) result.add("south");
                if ((blocked & 8) == 0) result.add("west");

                if (blocked >>> 4 != 0) {
                    throw new IllegalStateException("invalid blocked: " + blocked);
                }

                lines.add("forceapproach=" + String.join(",", result));
            }

            case 70 -> lines.add("offsetx=" + packet.g2s()); // html5 (only offset)
            case 71 -> lines.add("offsety=" + packet.g2s()); // html5 (only offset)
            case 72 -> lines.add("offsetz=" + packet.g2s()); // html5 (only offset)
            case 73 -> lines.add("forcedecor=yes");
            case 74 -> lines.add("breakroutefinding=yes"); // https://twitter.com/JagexAsh/status/1443150721734660096
            case 75 -> lines.add("raiseobject=" + Unpacker.formatBoolean(packet.g1())); // https://twitter.com/JagexAsh/status/1641051532010434560

            case 77 -> { // * https://twitter.com/JagexAsh/status/737426310545481728
                var multivarbit = packet.g2null();

                if (multivarbit != -1) {
                    lines.add("multivar=" + Unpacker.format(Type.VARBIT, multivarbit));
                }

                var multivarp = packet.g2null();

                if (multivarp != -1) {
                    lines.add("multivar=" + Unpacker.format(Type.VAR_PLAYER, multivarp));
                }

                var count = Unpack.VERSION < 900 ? packet.g1() : packet.gSmart1or2();

                for (var i = 0; i <= count; ++i) {
                    var multi = Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null();

                    if (multi != -1) {
                        lines.add("multiloc=" + i + "," + Unpacker.format(Type.LOC, multi));
                    }
                }
            }

            case 78 -> lines.add("bgsound=" + Unpacker.format(Type.SYNTH, packet.g2()) + "," + packet.g1()); // https://twitter.com/JagexAsh/status/1651904693671546881

            case 79 -> { // https://twitter.com/JagexAsh/status/1651904693671546881
                var line = "randomsound=" + packet.g2() + "," + packet.g2() + "," + packet.g1();
                var count = packet.g1();

                for (var i = 0; i < count; ++i) {
                    line += "," + Unpacker.format(Type.SYNTH, packet.g2());
                }

                lines.add(line);
            }

            case 81 -> lines.add("hillchange=tree_skew," + packet.g1());
            case 82 -> lines.add("istexture=yes");
            case 88 -> lines.add("hardshadow=no");
            case 89 -> lines.add("randomanimframe=no"); // https://twitter.com/JagexAsh/status/1773322757041766706
            case 90 -> lines.add("unknown90=yes"); // removed
            case 91 -> lines.add("members=yes");

            case 92 -> {
                var multivarbit = packet.g2null();

                if (multivarbit != -1) {
                    lines.add("multivar=" + Unpacker.format(Type.VARBIT, multivarbit));
                }

                var multivarp = packet.g2null();

                if (multivarp != -1) {
                    lines.add("multivar=" + Unpacker.format(Type.VAR_PLAYER, multivarp));
                }

                var multidefault = Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null();

                if (multidefault != -1) {
                    lines.add("multiloc=default," + Unpacker.format(Type.LOC, multidefault));
                }

                var count = Unpack.VERSION >= 900 ? packet.gSmart1or2() : packet.g1();

                for (var i = 0; i <= count; ++i) {
                    var multi = Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null();

                    if (multi != -1) {
                        lines.add("multiloc=" + i + "," + Unpacker.format(Type.LOC, multi));
                    }
                }
            }

            case 93 -> lines.add("hillchange=rotate," + packet.g2());
            case 94 -> lines.add("hillchange=ceiling_skew");

            case 95 -> {
                if (Unpack.VERSION < 600) {
                    lines.add("hillchange=skew_to_fit");
                } else {
                    lines.add("hillchange=skew_to_fit," + packet.g2());
                }
            }

            case 96 -> lines.add("unknown96=yes");
            case 97 -> lines.add("msirotate=yes");
            case 98 -> lines.add("unknown98=yes");
            case 99 -> lines.add("unknown99=" + packet.g1() + "," + packet.g2()); // gone in nxt
            case 100 -> lines.add("unknown100=" + packet.g1() + "," + packet.g2()); // gone in nxt
            case 101 -> lines.add("msiangle=" + packet.g1());
            case 102 -> lines.add("msi=" + Unpacker.format(Type.MAPSCENEICON, packet.g2()));
            case 103 -> lines.add("occlude=no");
            case 104 -> lines.add("bgsoundvolume=" + packet.g1());
            case 105 -> lines.add("msimirror=yes");

            case 106 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("anim=" + Unpacker.format(Type.SEQ, packet.gSmart2or4null()) + "," + packet.g1());
                }
            }

            case 107 -> lines.add("mapelement=" + Unpacker.format(Type.MAPELEMENT, packet.g2()));
            case 150 -> lines.add("membersop1=" + packet.gjstr());
            case 151 -> lines.add("membersop2=" + packet.gjstr());
            case 152 -> lines.add("membersop3=" + packet.gjstr());
            case 153 -> lines.add("membersop4=" + packet.gjstr());
            case 154 -> lines.add("membersop5=" + packet.gjstr());

            case 160 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("quest=" + Unpacker.format(Type.QUEST, packet.g2()));
                }
            }

            case 162 -> lines.add("hillchange=rotate," + packet.g4s());
            case 163 -> lines.add("tint=" + packet.g1s() + "," + packet.g1s() + "," + packet.g1s() + "," + packet.g1s());
            case 164 -> lines.add("unknown164=" + packet.g2s());
            case 165 -> lines.add("unknown165=" + packet.g2s());
            case 166 -> lines.add("unknown166=" + packet.g2s());
            case 167 -> lines.add("unknown167=" + packet.g2());
            case 168 -> lines.add("unknown168=yes");
            case 169 -> lines.add("unknown169=yes");
            case 170 -> lines.add("unknown170=" + packet.gSmart1or2());
            case 171 -> lines.add("unknown171=" + packet.gSmart1or2());
            case 173 -> lines.add("bgsoundrate=" + packet.g2() + "," + packet.g2());
            case 177 -> lines.add("unknown177=yes");
            case 178 -> lines.add("bgsoundsize=" + packet.g1());
            case 179 -> lines.add("unknown179=yes"); // todo: bgsound
            case 186 -> lines.add("unknown186=" + packet.g1());
            case 188 -> lines.add("unknown188=yes");
            case 189 -> lines.add("antimacro=yes");
            case 190 -> lines.add("cursor1=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 191 -> lines.add("cursor2=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 192 -> lines.add("cursor3=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 193 -> lines.add("cursor4=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 194 -> lines.add("cursor5=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 195 -> lines.add("cursor6=" + Unpacker.format(Type.CURSOR, packet.g2()));

            case 196 -> lines.add("unknown196=" + packet.g1());
            case 197 -> lines.add("unknown197=" + packet.g1());
            case 198 -> lines.add("unknown198=yes");
            case 199 -> lines.add("unknown199=no");
            case 200 -> lines.add("unknown200=yes");
            case 201 -> lines.add("clickbox=" + packet.gSmart1or2s() + "," + packet.gSmart1or2s() + "," + packet.gSmart1or2s() + "," + packet.gSmart1or2s() + "," + packet.gSmart1or2s() + "," + packet.gSmart1or2s());
            case 202 -> lines.add("unknown202=" + packet.gSmart1or2());
            case 203 -> lines.add("unknown203=yes");

            case 204 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("unknown204=" + packet.g2() + "," + packet.g1() + "," + packet.gFloat() + "," + packet.gFloat() + "," + packet.gFloat() + "," + packet.gFloat() + "," + packet.gFloat() + "," + packet.gFloat());
                }
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

            // _thy/_thz/_tia/_tie
            // 2 7 pjm
            // bgsound _vmc/_vyj _tbh/_tcv
            case 250 -> lines.add("bgsoundshape=" + packet.g1()); // todo: sound _vmd/_vyl
            case 251 -> lines.add("unknown251=" + Unpacker.formatBoolean(packet.g1())); // todo: sound _vme/_vym
            case 252 -> lines.add("unknown252=" + packet.g2() + "," + packet.g2() + "," + packet.g2()); // todo sound _vmg/_vyo _vmi/_vyp _vmk/_vyr

            case 253 -> lines.add("randomsoundshape=" + packet.g1()); // todo sound _vml/_vyy
            case 254 -> lines.add("unknown254=" + Unpacker.formatBoolean(packet.g1())); // todo: sound _vmm/_vza
            case 255 -> lines.add("unknown255=" + packet.g2() + "," + packet.g2() + "," + packet.g2()); // todo: sound _vmn/_vzc _vmp/_vzd _vmq/_vzf

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
