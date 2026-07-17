package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class AchievementUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.ACHIEVEMENT, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("name=" + packet.gjstr2()); // cs2 achievement_getname, lua name

            case 2 -> { // cs2 achievement_getdesc, lua descriptions
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("desc=" + packet.g1() + "," + packet.gjstr2());
                }
            }

            case 3 -> lines.add("category=" + Unpacker.format(Type.CATEGORY, packet.g2())); // cs2 achievement_category, lua category
            case 4 -> lines.add("sprite=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null())); // cs2 achievement_sprite, lua spriteID
            case 5 -> lines.add("runescore=" + packet.g1()); // cs2 achievement_runescore, lua runeScore
            case 6 -> lines.add("graceday=" + packet.g2()); // cs2 achievement_getgraceday, lua graceExpiryRunedate
            case 7 -> lines.add("reward=" + packet.gjstr2()); // cs2 achievement_getreward, lua reward

            case 8 -> {
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    var a = packet.g1();
                    var b = packet.g1();
                    var c = packet.gjstr2();
                    var line = "statprereq=" + a + "," + b + "," + c;
                    var count2 = packet.gSmart1or2();

                    for (var j = 0; j < count2; j++) {
                        line += "," + Unpacker.format(Type.STAT, packet.g2());
                    }

                    lines.add(line);
                }
            }

            case 9 -> { // cs2 achievement_varp_prereq_*
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    var a = packet.g1();
                    var b = packet.gSmart2or4s();
                    var c = packet.gjstr2();
                    var line = "varpprereq=" + a + "," + b + "," + c;
                    var count2 = packet.gSmart1or2();

                    for (var j = 0; j < count2; j++) {
                        line += "," + Unpacker.format(Type.VAR_PLAYER, packet.g2());
                    }

                    lines.add(line);
                }
            }

            case 10 -> { // cs2 achievement_varbit_prereq_*
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    var a = packet.g1();
                    var b = packet.gSmart2or4s();
                    var c = packet.gjstr2();
                    var line = "varbitprereq=" + a + "," + b + "," + c;
                    var count2 = packet.gSmart1or2();

                    for (var j = 0; j < count2; j++) {
                        line += "," + Unpacker.format(Type.VAR_PLAYER_BIT, packet.g2());
                    }

                    lines.add(line);
                }
            }

            case 11 -> { // cs2 achievement_achievement_prereq_*
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    lines.add("achievementprereq=" + packet.g1() + "," + Unpacker.format(Type.ACHIEVEMENT, packet.g2()));
                }
            }

            case 12 -> { // cs2 achievement_stat_req_*
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    var a = packet.g1();
                    var b = packet.g1();
                    var c = packet.gjstr2();
                    var line = "statreq=" + a + "," + b + "," + c;
                    var count2 = packet.gSmart1or2();

                    for (var j = 0; j < count2; j++) {
                        line += "," + Unpacker.format(Type.STAT, packet.g2());
                    }

                    lines.add(line);
                }
            }

            case 13 -> { // cs2 achievement_varp_req_*
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    var a = packet.g1();
                    var b = packet.gSmart2or4s();
                    var c = packet.gjstr2();
                    var line = "varpreq=" + a + "," + b + "," + c;
                    var count2 = packet.gSmart1or2();

                    for (var j = 0; j < count2; j++) {
                        line += "," + Unpacker.format(Type.VAR_PLAYER, packet.g2());
                    }

                    lines.add(line);
                }
            }

            case 14 -> { // cs2 achievement_varbit_req_*
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    var a = packet.g1();
                    var b = packet.gSmart2or4s();
                    var c = packet.gjstr2();
                    var line = "varbitreq=" + a + "," + b + "," + c;
                    var count2 = packet.gSmart1or2();

                    for (var j = 0; j < count2; j++) {
                        line += "," + Unpacker.format(Type.VAR_PLAYER_BIT, packet.g2());
                    }

                    lines.add(line);
                }
            }

            case 15 -> { // cs2 achievement_achievement_req_*
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    lines.add("achievementreq=" + packet.g1() + "," + Unpacker.format(Type.ACHIEVEMENT, packet.g2()));
                }
            }

            case 16 -> lines.add("subcat=" + Unpacker.format(Type.CATEGORY, packet.g2())); // cs2 achievement_findsubcat, lua subCategory
            case 17 -> lines.add("locked=yes");
            case 18 -> lines.add("hide=" + packet.g1()); // cs2 achievement_gethide, lua hiddenType
            case 19 -> lines.add("members=no"); // cs2 achievement_getmembers, lua isMembers

            case 20 -> { // cs2 achievement_quest_prereq_*
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    lines.add("questprereq=" + packet.g1() + "," + Unpacker.format(Type.QUEST, packet.g2()));
                }
            }

            case 21 -> { // cs2 achievement_quest_req_*
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    lines.add("questreq=" + packet.g1() + "," + Unpacker.format(Type.QUEST, packet.g2()));
                }
            }

            case 22 -> { // cs2 achievement_varp_testbit_prereq_*
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    lines.add("varptestbitprereq=" + packet.g1() + "," + Unpacker.format(Type.VAR_PLAYER, packet.g2()) + "," + packet.g1() + "," + packet.gjstr2() + "," + packet.g1());
                }
            }

            case 23 -> { // cs2 achievement_varp_testbit_req_*
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    lines.add("varptestbitreq=" + packet.g1() + "," + Unpacker.format(Type.VAR_PLAYER, packet.g2()) + "," + packet.g1() + "," + packet.gjstr2() + "," + packet.g1());
                }
            }

            case 24 -> { // cs2 achievement_varbit_testbit_prereq_*
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    lines.add("varbittestbitprereq=" + packet.g1() + "," + Unpacker.format(Type.VAR_PLAYER_BIT, packet.g2()) + "," + packet.g1() + "," + packet.gjstr2() + "," + packet.g1());
                }
            }

            case 25 -> { // cs2 achievement_varp_testbit_req_*
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    lines.add("varbittestbitreq=" + packet.g1() + "," + Unpacker.format(Type.VAR_PLAYER_BIT, packet.g2()) + "," + packet.g1() + "," + packet.gjstr2() + "," + packet.g1());
                }
            }

            case 26 -> lines.add("dbrow=" + Unpacker.format(Type.DBROW, packet.g2())); // lua name dbRowConfig
            case 27 -> lines.add("checklist=yes"); // cs2 achievement_is_checklist

            case 28 -> {
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    lines.add("unknown28=" + packet.gSmart1or2());
                }
            }

            case 29 -> lines.add("unknown29=" + packet.g1());

            case 30 -> {
                var count = packet.gSmart1or2();

                for (var i = 0; i < count; i++) {
                    lines.add("unknown22=" + packet.gSmart1or2());
                }
            }

            case 31 -> lines.add("unknown31=" + packet.g1());
            case 32 -> lines.add("unknown32=" + packet.g1() + "," + packet.g1() + "," + packet.g1());

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
