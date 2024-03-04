package rs3.unpack.config;

import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class AudioDefaultsUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[audiodefaults_" + id + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> {
                if (Unpack.VERSION >= 900) {
                    lines.add("titlescreensong=" + Unpacker.format(Type.MIDI, packet.g4s()));
                } else {
                    lines.add("titlescreensong=" + Unpacker.format(Type.MIDI, packet.g2()));
                }
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
