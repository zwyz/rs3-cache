package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class ObjUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.OBJ, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("model=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 2 -> lines.add("name=" + packet.gjstr());
            case 3 -> lines.add("desc=" + packet.gjstr());
            case 4 -> lines.add("2dzoom=" + packet.g2());
            case 5 -> lines.add("2dxan=" + packet.g2());
            case 6 -> lines.add("2dyan=" + packet.g2());
            case 7 -> lines.add("2dxof=" + packet.g2s());
            case 8 -> lines.add("2dyof=" + packet.g2s());
            case 9 -> lines.add("unknown9=" + packet.gjstr()); // todo: unused
            case 11 -> lines.add("stackable=always");
            case 12 -> lines.add("cost=" + packet.g4s());
            case 13 -> lines.add("wearpos=" + Unpacker.formatWearPos(packet.g1()));
            case 14 -> lines.add("wearpos2=" + Unpacker.formatWearPos(packet.g1()));
            case 15 -> lines.add("tradeable=no");
            case 16 -> lines.add("members=yes");
            case 23 -> lines.add("manwear=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 24 -> lines.add("manwear2=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 25 -> lines.add("womanwear=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 26 -> lines.add("womanwear2=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 27 -> lines.add("wearpos3=" + Unpacker.formatWearPos(packet.g1()));
            case 30 -> lines.add("op1=" + packet.gjstr());
            case 31 -> lines.add("op2=" + packet.gjstr());
            case 32 -> lines.add("op3=" + packet.gjstr());
            case 33 -> lines.add("op4=" + packet.gjstr());
            case 34 -> lines.add("op5=" + packet.gjstr());
            case 35 -> lines.add("iop1=" + packet.gjstr());
            case 36 -> lines.add("iop2=" + packet.gjstr());
            case 37 -> lines.add("iop3=" + packet.gjstr());
            case 38 -> lines.add("iop4=" + packet.gjstr());
            case 39 -> lines.add("iop5=" + packet.gjstr());

            case 40 -> {
                var count = packet.g1();

                for (var i = 0; i < count; ++i) {
                    lines.add("recol" + (i + 1) + "s=" + packet.g2());
                    lines.add("recol" + (i + 1) + "d=" + packet.g2());
                }
            }

            case 41 -> {
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

            case 43 -> lines.add("minimenucolour=" + packet.g4s());
            case 44 -> lines.add("unknown44=" + packet.g2());
            case 45 -> lines.add("unknown45=" + packet.g2());
            case 65 -> lines.add("stockmarket=yes");
            case 69 -> lines.add("stockmarketlimit=" + packet.g4s());
            case 78 -> lines.add("manwear3=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 79 -> lines.add("womanwear3=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 90 -> lines.add("manhead=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 91 -> lines.add("womanhead=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 92 -> lines.add("manhead2=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 93 -> lines.add("womanhead2=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));
            case 94 -> lines.add("category=" + Unpacker.format(Type.CATEGORY, packet.g2()));
            case 95 -> lines.add("2dzan=" + packet.g2());
            case 96 -> lines.add("dummyitem=" + packet.g1());
            case 97 -> lines.add("certlink=" + Unpacker.format(Type.OBJ, packet.g2()));
            case 98 -> lines.add("certtemplate=" + Unpacker.format(Type.OBJ, packet.g2()));
            case 100 -> lines.add("count1=" + Unpacker.format(Type.OBJ, packet.g2()) + "," + packet.g2());
            case 101 -> lines.add("count2=" + Unpacker.format(Type.OBJ, packet.g2()) + "," + packet.g2());
            case 102 -> lines.add("count3=" + Unpacker.format(Type.OBJ, packet.g2()) + "," + packet.g2());
            case 103 -> lines.add("count4=" + Unpacker.format(Type.OBJ, packet.g2()) + "," + packet.g2());
            case 104 -> lines.add("count5=" + Unpacker.format(Type.OBJ, packet.g2()) + "," + packet.g2());
            case 105 -> lines.add("count6=" + Unpacker.format(Type.OBJ, packet.g2()) + "," + packet.g2());
            case 106 -> lines.add("count7=" + Unpacker.format(Type.OBJ, packet.g2()) + "," + packet.g2());
            case 107 -> lines.add("count8=" + Unpacker.format(Type.OBJ, packet.g2()) + "," + packet.g2());
            case 108 -> lines.add("count9=" + Unpacker.format(Type.OBJ, packet.g2()) + "," + packet.g2());
            case 109 -> lines.add("count10=" + Unpacker.format(Type.OBJ, packet.g2()) + "," + packet.g2());
            case 110 -> lines.add("resizex=" + packet.g2());
            case 111 -> lines.add("resizey=" + packet.g2());
            case 112 -> lines.add("resizez=" + packet.g2());
            case 113 -> lines.add("ambient=" + packet.g1s());
            case 114 -> lines.add("contrast=" + packet.g1s());
            case 115 -> lines.add("team=" + packet.g1());
            case 121 -> lines.add("lentlink=" + Unpacker.format(Type.OBJ, packet.g2()));
            case 122 -> lines.add("lenttemplate=" + Unpacker.format(Type.OBJ, packet.g2()));
            case 125 -> lines.add("manwearoff=" + packet.g1s() + "," + packet.g1s() + "," + packet.g1s());
            case 126 -> lines.add("womanwearoff=" + packet.g1s() + "," + packet.g1s() + "," + packet.g1s());
            case 127 -> lines.add("unknown127=" + packet.g1() + "," + packet.g2());
            case 128 -> lines.add("unknown128=" + packet.g1() + "," + packet.g2());
            case 129 -> lines.add("unknown129=" + packet.g1() + "," + packet.g2());
            case 130 -> lines.add("unknown130=" + packet.g1() + "," + packet.g2());
            case 131 -> lines.add("memberdesc=" + packet.gjstr()); // todo

            case 132 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("quest=" + packet.g2());
                }
            }

            case 134 -> lines.add("picksizeshift=" + packet.g1());
            case 139 -> lines.add("boughtlink=" + Unpacker.format(Type.OBJ, packet.g2()));
            case 140 -> lines.add("boughttemplate=" + Unpacker.format(Type.OBJ, packet.g2()));
            case 148 -> lines.add("placeholderlink=" + Unpacker.format(Type.OBJ, packet.g2()));
            case 149 -> lines.add("placeholdertemplate=" + Unpacker.format(Type.OBJ, packet.g2()));
            case 142 -> lines.add("cursor1=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 143 -> lines.add("cursor2=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 144 -> lines.add("cursor3=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 145 -> lines.add("cursor4=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 146 -> lines.add("cursor5=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 150 -> lines.add("icursor1=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 151 -> lines.add("icursor2=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 152 -> lines.add("icursor3=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 153 -> lines.add("icursor4=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 154 -> lines.add("icursor5=" + Unpacker.format(Type.CURSOR, packet.g2()));
            case 156 -> lines.add("shadow=no"); // todo
            case 157 -> lines.add("unknown157=yes");

            case 161 -> lines.add("shardlink=" + Unpacker.format(Type.OBJ, packet.g2()));
            case 162 -> lines.add("shardtemplate=" + Unpacker.format(Type.OBJ, packet.g2()));
            case 163 -> lines.add("shardcount=" + packet.g2());
            case 164 -> lines.add("shardname=" + packet.gjstr());
            case 165 -> lines.add("stackable=never");
            case 167 -> lines.add("unknown167=yes");
            case 168 -> lines.add("allowsplaceholder=no");
            case 178 -> lines.add("stackable=sometimes");
            case 181 -> lines.add("cost=" + packet.g8s());

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
