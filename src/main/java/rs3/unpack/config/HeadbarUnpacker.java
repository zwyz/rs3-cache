package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class HeadbarUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.HEADBAR, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("unknown1=" + packet.g2());
            case 2 -> lines.add("showpriority=" + packet.g1());
            case 3 -> lines.add("hidepriority=" + packet.g1());
            case 4 -> lines.add("fadeout=no"); // https://twitter.com/JagexAsh/status/1654124199194288137
            case 5 -> lines.add("sticktime=" + packet.g2()); // https://twitter.com/JagexAsh/status/1654124199194288137
            case 6 -> lines.add("unknown6=" + packet.g2());
            case 7 -> lines.add("full=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null())); // https://twitter.com/JagexAsh/status/1654124199194288137
            case 8 -> lines.add("empty=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null())); // https://twitter.com/JagexAsh/status/1654124199194288137
            case 9 -> lines.add("fullplayergroup=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 10 -> lines.add("emptyplayergroup=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 11 -> lines.add("fadeout=" + packet.g2()); // https://twitter.com/JagexAsh/status/1654124199194288137
            case 12 -> lines.add("fullplayergroupteam=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 13 -> lines.add("emptyplayergroupteam=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 14 -> lines.add("unknown14=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 15 -> lines.add("unknown15=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 16 -> lines.add("unknown16=yes");
            case 17 -> lines.add("unknown17=" + packet.g1());
            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
