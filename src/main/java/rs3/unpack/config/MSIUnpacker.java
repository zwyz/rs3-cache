package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class MSIUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.MAPSCENEICON, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("graphic=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 2 -> lines.add("tint=" + packet.g3()); // 216 GetTint
            case 3 -> lines.add("resize=yes"); // 216 GetResize
            case 4 -> lines.add("graphic=" + Unpacker.format(Type.GRAPHIC, -1)); // 216 GetSpriteID
            case 5 -> lines.add("hideonminimap=yes"); // 216 GetHideOnMinimap

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
