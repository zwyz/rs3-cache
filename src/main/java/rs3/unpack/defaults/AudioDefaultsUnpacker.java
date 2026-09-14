package rs3.unpack.defaults;

import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class AudioDefaultsUnpacker {
    public static List<String> unpack(byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("titlescreensong=" + Unpacker.format(Type.MIDI, Unpack.VERSION >= 912 ? packet.g4s() : packet.g2()));

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
