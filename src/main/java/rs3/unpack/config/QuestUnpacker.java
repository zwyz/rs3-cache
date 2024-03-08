package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class QuestUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.QUEST, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("name=" + packet.gjstr2());
            case 2 -> lines.add("sortname=" + packet.gjstr2());

            case 3 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("progressvarp=" + Unpacker.format(Type.VAR_PLAYER, packet.g2()) + "," + packet.g4s() + "," + packet.g4s());
                }
            }

            case 4 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("progressvarbit=" + Unpacker.format(Type.VARBIT, packet.g2()) + "," + packet.g4s() + "," + packet.g4s());
                }
            }

            case 5 -> lines.add("unknown5=" + packet.g2());
            case 6 -> lines.add("type=" + packet.g1());
            case 7 -> lines.add("difficulty=" + packet.g1());
            case 8 -> lines.add("members=yes");
            case 9 -> lines.add("points=" + packet.g1());

            case 10 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("unknown10=" + Unpacker.format(Type.COORDGRID, packet.g4s()));
                }
            }

            case 12 -> lines.add("unknown12=" + packet.g4s());

            case 13 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("questreq=" + Unpacker.format(Type.QUEST, packet.g2()));
                }
            }

            case 14 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("statreq=" + Unpacker.format(Type.STAT, packet.g1()) + "," + packet.g1());
                }
            }

            case 15 -> lines.add("pointsreq=" + packet.g2());
            case 17 -> lines.add("icon=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));

            case 18 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("varpreq=" + Unpacker.format(Type.VAR_PLAYER, packet.g4s()) + "," + packet.g4s() + "," + packet.g4s() + "," + packet.gjstr());
                }
            }

            case 19 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("varbitreq=" + Unpacker.format(Type.VARBIT, packet.g4s()) + "," + packet.g4s() + "," + packet.g4s() + "," + packet.gjstr());
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

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
