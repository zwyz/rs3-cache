package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class WaterUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.WATER, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            // jag::WaterType::DecodeA
            case 1 -> lines.add("unknown1=" + packet.g2());
            case 3 -> lines.add("unknown3=" + packet.g2());
            case 6 -> lines.add("unknown6=0x" + Integer.toHexString(packet.g3()));
            case 7 -> lines.add("unknown7=" + packet.g2() + "," + packet.g2());
            case 8 -> lines.add("unknown8=" + packet.g2());
            case 11 -> lines.add("unknown11=" + packet.g2());
            case 13 -> lines.add("unknown13=" + packet.g2());
            case 15 -> lines.add("unknown15=" + packet.g4s());
            case 16 -> lines.add("unknown16=" + packet.g2());
            case 17 -> lines.add("unknown17=" + packet.g2());
            case 18 -> lines.add("unknown18=" + packet.g1());
            case 19 -> lines.add("unknown19=" + packet.g1());
            case 20 -> lines.add("unknown20=" + packet.g2());
            case 21 -> lines.add("unknown21=" + packet.g2());
            case 22 -> lines.add("unknown22=" + packet.g2());
            case 23 -> lines.add("unknown23=" + packet.g2());
            case 24 -> lines.add("unknown24=" + packet.g1());
            case 26 -> lines.add("unknown26=" + packet.g2() + "," + packet.g2() + "," + packet.g2());
            case 28 -> lines.add("unknown28=" + packet.g2());

            // jag::WaterType::DecodeB (inlined)
            case 12 -> lines.add("unknown12=0x" + Integer.toHexString(packet.g4s()));
            case 9 -> lines.add("unknown9=" + packet.g2());
            case 14 -> lines.add("unknown14=" + packet.g2());
            case 5 -> lines.add("unknown5=" + packet.g2());
            case 25 -> lines.add("unknown25=" + packet.g2());
            case 27 -> lines.add("unknown27=" + packet.g2());
            case 83 -> lines.add("unknown83=" + packet.gFloat());
            case 84 -> lines.add("unknown84=" + packet.gFloat());

            // jag::WaterType::DecodeC
            case 2 -> lines.add("unknown2=" + packet.g2());
            case 4 -> lines.add("unknown4=" + packet.g2());
            case 10 -> lines.add("unknown10=" + packet.g2());
            case 32 -> lines.add("unknown32=" + packet.g2());
            case 87 -> lines.add("unknown87=" + packet.g2());

            // jag::WaterType::DecodeD
            case 29 -> lines.add("unknown29=" + packet.g2());
            case 30 -> lines.add("unknown30=" + packet.g2());
            case 31 -> lines.add("unknown31=" + packet.g2());

            case 33 -> lines.add("unknown33a0=" + Unpacker.formatBoolean(packet.g1()));
            case 34 -> lines.add("unknown34a0=" + packet.gFloat());
            case 35 -> lines.add("unknown35a0=" + packet.gFloat());
            case 36 -> lines.add("unknown36a0=" + packet.gFloat());
            case 37 -> lines.add("unknown37a0=" + packet.gFloat() + "," + packet.gFloat());
            case 38 -> lines.add("unknown38a0=" + packet.gFloat() + "," + packet.gFloat());
            case 39 -> lines.add("unknown39a0=" + packet.gFloat());
            case 40 -> lines.add("unknown40a0=" + packet.gFloat());

            case 41 -> lines.add("unknown33a1=" + Unpacker.formatBoolean(packet.g1()));
            case 42 -> lines.add("unknown34a1=" + packet.gFloat());
            case 43 -> lines.add("unknown35a1=" + packet.gFloat());
            case 44 -> lines.add("unknown36a1=" + packet.gFloat());
            case 45 -> lines.add("unknown37a1=" + packet.gFloat() + "," + packet.gFloat());
            case 46 -> lines.add("unknown38a1=" + packet.gFloat() + "," + packet.gFloat());
            case 47 -> lines.add("unknown39a1=" + packet.gFloat());
            case 48 -> lines.add("unknown40a1=" + packet.gFloat());

            case 49 -> lines.add("unknown33a2=" + Unpacker.formatBoolean(packet.g1()));
            case 50 -> lines.add("unknown34a2=" + packet.gFloat());
            case 51 -> lines.add("unknown35a2=" + packet.gFloat());
            case 52 -> lines.add("unknown36a2=" + packet.gFloat());
            case 53 -> lines.add("unknown37a2=" + packet.gFloat() + "," + packet.gFloat());
            case 54 -> lines.add("unknown38a2=" + packet.gFloat() + "," + packet.gFloat());
            case 55 -> lines.add("unknown39a2=" + packet.gFloat());
            case 56 -> lines.add("unknown40a2=" + packet.gFloat());

            case 57 -> lines.add("unknown33b0=" + Unpacker.formatBoolean(packet.g1()));
            case 58 -> lines.add("unknown34b0=" + packet.gFloat());
            case 59 -> lines.add("unknown35b0=" + packet.gFloat());
            case 60 -> lines.add("unknown36b0=" + packet.gFloat());
            case 61 -> lines.add("unknown37b0=" + packet.gFloat() + "," + packet.gFloat());
            case 62 -> lines.add("unknown38b0=" + packet.gFloat() + "," + packet.gFloat());
            case 63 -> lines.add("unknown39b0=" + packet.gFloat());
            case 64 -> lines.add("unknown40b0=" + packet.gFloat());

            case 65 -> lines.add("unknown33b1=" + Unpacker.formatBoolean(packet.g1()));
            case 66 -> lines.add("unknown34b1=" + packet.gFloat());
            case 67 -> lines.add("unknown35b1=" + packet.gFloat());
            case 68 -> lines.add("unknown36b1=" + packet.gFloat());
            case 69 -> lines.add("unknown37b1=" + packet.gFloat() + "," + packet.gFloat());
            case 70 -> lines.add("unknown38b1=" + packet.gFloat() + "," + packet.gFloat());
            case 71 -> lines.add("unknown39b1=" + packet.gFloat());
            case 72 -> lines.add("unknown40b1=" + packet.gFloat());

            case 73 -> lines.add("unknown33b2=" + Unpacker.formatBoolean(packet.g1()));
            case 74 -> lines.add("unknown34b2=" + packet.gFloat());
            case 75 -> lines.add("unknown35b2=" + packet.gFloat());
            case 76 -> lines.add("unknown36b2=" + packet.gFloat());
            case 77 -> lines.add("unknown37b2=" + packet.gFloat() + "," + packet.gFloat());
            case 78 -> lines.add("unknown38b2=" + packet.gFloat() + "," + packet.gFloat());
            case 79 -> lines.add("unknown39b2=" + packet.gFloat());
            case 80 -> lines.add("unknown40b2=" + packet.gFloat());

            // jag::WaterType::DecodeE
            case 86 -> lines.add("unknown86=" + packet.g2());
            case 88 -> lines.add("unknown88=" + packet.gFloat() + "," + packet.gFloat());
            case 89 -> lines.add("unknown89=" + packet.g4s());
            case 90 -> lines.add("unknown90=" + packet.gFloat());
            case 91 -> lines.add("unknown91=" + packet.g4s());
            case 92 -> lines.add("unknown92=" + packet.g1());
            case 93 -> lines.add("unknown93=" + packet.gFloat());
            case 94 -> lines.add("unknown94=" + packet.g4s());
            case 95 -> lines.add("unknown95=" + packet.g4s());
            case 96 -> lines.add("unknown96=" + packet.g1());
            case 108 -> lines.add("unknown108=" + packet.g4s());

            // jag::WaterType::DecodeF
            case 97 -> lines.add("unknown97=" + packet.gFloat() + "," + packet.gFloat() + "," + packet.gFloat());
            case 98 -> lines.add("unknown98=" + packet.g4s());
            case 99 -> lines.add("unknown99=" + packet.gFloat());

            // jag::WaterType::DecodeG
            case 100 -> lines.add("unknown100=" + packet.gFloat());
            case 101 -> lines.add("unknown101=" + packet.gFloat());
            case 102 -> lines.add("unknown102=" + packet.gFloat());
            case 103 -> lines.add("unknown103=" + packet.gFloat());
            case 104 -> lines.add("unknown104=" + packet.gFloat());
            case 105 -> lines.add("unknown105=" + packet.gFloat());
            case 106 -> lines.add("unknown106=" + packet.gFloat());
            case 107 -> lines.add("unknown107=" + packet.gFloat());

            // jag::WaterType::DecodeH (inlined)
            case 81 -> lines.add("unknown81=" + packet.gFloat());
            case 82 -> lines.add("unknown82=" + packet.gFloat());
            case 85 -> lines.add("unknown85=" + Unpacker.formatBoolean(packet.g1()));

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
