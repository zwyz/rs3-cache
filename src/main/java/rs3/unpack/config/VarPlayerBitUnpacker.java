package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class VarPlayerBitUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.VAR_PLAYER_BIT, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> { // https://twitter.com/JagexAsh/status/978615662796066816
                lines.add("basevar=" + Unpacker.format(Type.VAR_PLAYER, packet.g2()));
                lines.add("startbit=" + packet.g1());
                lines.add("endbit=" + packet.g1());
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
