package rs3.unpack.config;

import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class BASUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.BAS, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("readyanim=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()) + "," + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 2 -> lines.add("crawlanim=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 3 -> lines.add("crawlanim_b=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 4 -> lines.add("crawlanim_l=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 5 -> lines.add("crawlanim_r=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 6 -> lines.add("runanim=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 7 -> lines.add("runanim_b=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 8 -> lines.add("runanim_l=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 9 -> lines.add("runanim_r=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 26 -> lines.add("offset=" + packet.g1() + "," + packet.g1());
            case 27 -> lines.add("unknown27=" + packet.g1() + "," + packet.g2s() + "," + packet.g2s() + "," + packet.g2s() + "," + packet.g2s() + "," + packet.g2s() + "," + packet.g2s());

            case 28 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("unknown28=" + packet.g1());
                }
            }

            case 29 -> lines.add("turnspeed=" + packet.g1());
            case 30 -> lines.add("unknown30=" + packet.g2());
            case 31 -> lines.add("unknown31=" + packet.g1());
            case 32 -> lines.add("unknown32=" + packet.g2());
            case 33 -> lines.add("unknown33=" + packet.g2s());
            case 34 -> lines.add("unknown34=" + packet.g1());
            case 35 -> lines.add("unknown35=" + packet.g2());
            case 36 -> lines.add("unknown36=" + packet.g2s());
            case 37 -> lines.add("walkspeed=" + packet.g1());
            case 38 -> lines.add("readyanim_l=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 39 -> lines.add("readyanim_r=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 40 -> lines.add("walkanim_b=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 41 -> lines.add("walkanim_l=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 42 -> lines.add("walkanim_r=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 43 -> lines.add("unknown43=" + packet.g2());
            case 44 -> lines.add("unknown44=" + packet.g2());
            case 45 -> lines.add("unknown45=" + packet.g2());
            case 46 -> lines.add("unknown46=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 47 -> lines.add("unknown47=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 48 -> lines.add("unknown48=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 49 -> lines.add("unknown49=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 50 -> lines.add("unknown50=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));
            case 51 -> lines.add("unknown51=" + Unpacker.format(Type.SEQ, Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null()));

            case 52 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    var anim = Unpack.VERSION < 700 ? packet.g2null() : packet.gSmart2or4null();
                    var weight = packet.g1();
                    var line = "randomreadyanim=" + Unpacker.format(Type.SEQ, anim) + "," + weight;

                    if (Unpack.VERSION >= 920) {
                        var unknownCount = packet.g1();

                        for (var j = 0; j < unknownCount; j++) {
                            line += "," + packet.g1();
                        }
                    }

                    lines.add(line);
                }
            }

            case 53 -> lines.add("unknown53=no");
            case 54 -> lines.add("unknown54=" + packet.g1() + "," + packet.g1());
            case 55 -> lines.add("unknown55=" + packet.g1() + "," + packet.g2());
            case 56 -> lines.add("unknown56=" + packet.g1() + "," + packet.g2s() + "," + packet.g2s() + "," + packet.g2s());

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
