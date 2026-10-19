package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class GameLogEventUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        var types = new ArrayList<Type>();
        lines.add("[" + Unpacker.format(Type.GAMELOGEVENT, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                Unpacker.setGameLogEventType(id, types);
                return lines;
            }

            case 103 -> {
                var type = Type.byID(packet.gSmart1or2());
                types.add(type);
                lines.add("type=" + type);
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
