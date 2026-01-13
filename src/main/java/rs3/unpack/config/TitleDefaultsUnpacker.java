package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class TitleDefaultsUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[titledefaults_" + id + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("title=" + Unpacker.format(Type.ENUM, packet.gSmart2or4null()) + "," + Unpacker.format(Type.ENUM, packet.gSmart2or4null()));

            case 2 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("unknown2=" + packet.g1() + "," + packet.g1());
                }
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
