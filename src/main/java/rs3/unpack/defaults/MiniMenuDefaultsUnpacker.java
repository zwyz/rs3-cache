package rs3.unpack.defaults;

import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class MiniMenuDefaultsUnpacker {
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

            case 1 -> lines.add("primaryclick=" + InputEventUtil.unpackInputEvent(packet));
            case 2 -> lines.add("secondaryclick=" + InputEventUtil.unpackInputEvent(packet));
            case 3 -> lines.add("tertiaryclick=" + InputEventUtil.unpackInputEvent(packet));
            case 4 -> lines.add("selectclick=" + InputEventUtil.unpackInputEvent(packet));
            case 5 -> lines.add("primarymodifier=" + InputEventUtil.unpackKeyHeldEvent(packet));
            case 6 -> lines.add("secondarymodifier=" + InputEventUtil.unpackKeyHeldEvent(packet));
            case 7 -> lines.add("tertiarymodifier=" + InputEventUtil.unpackKeyHeldEvent(packet));
            case 8 -> lines.add("unknown8=" + InputEventUtil.unpackInputEvent(packet));
            case 9 -> lines.add("unknown9=" + InputEventUtil.unpackInputEvent(packet));
            case 10 -> lines.add("unknown10=" + InputEventUtil.unpackInputEvent(packet));
            case 11 -> lines.add("unknown11=yes");
            case 12 -> lines.add("membersobjectcolour=" + Unpacker.formatColour(packet.g4s()));
            case 13 -> lines.add("freeobjectcolour=" + Unpacker.formatColour(packet.g4s()));

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
