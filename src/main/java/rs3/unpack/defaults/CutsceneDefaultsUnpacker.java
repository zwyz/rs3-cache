package rs3.unpack.defaults;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class CutsceneDefaultsUnpacker {
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

            case 1 -> lines.add("cancel=" + InputEventUtil.unpackInputEvent(packet));
            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
