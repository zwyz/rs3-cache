package rs3.unpack.defaults;

import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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

            case 1 -> lines.add("primaryclick=" + unpackInputEvent(packet));
            case 2 -> lines.add("secondaryclick=" + unpackInputEvent(packet));
            case 3 -> lines.add("tertiaryclick=" + unpackInputEvent(packet));
            case 4 -> lines.add("selectclick=" + unpackInputEvent(packet));
            case 5 -> lines.add("primarymodifier=" + unpackKeyHeldEvent(packet));
            case 6 -> lines.add("secondarymodifier=" + unpackKeyHeldEvent(packet));
            case 7 -> lines.add("tertiarymodifier=" + unpackKeyHeldEvent(packet));
            case 8 -> lines.add("unknown8=" + unpackInputEvent(packet));
            case 9 -> lines.add("unknown9=" + unpackInputEvent(packet));
            case 10 -> lines.add("unknown10=" + unpackInputEvent(packet));
            case 11 -> lines.add("unknown11=yes");
            case 12 -> lines.add("membersobjectcolour=" + Unpacker.formatColour(packet.g4s()));
            case 13 -> lines.add("freeobjectcolour=" + Unpacker.formatColour(packet.g4s()));

            default -> throw new IllegalStateException("unknown opcode");
        }
    }

    private static String unpackInputEvent(Packet packet) {
        var parts = new StringJoiner(",");

        switch (packet.g1()) {
            case 0 -> {
                parts.add("mouse");
                parts.add(formatMouseEventType(packet.g1s()));
                parts.add(String.valueOf(packet.g1()));
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    parts.add(formatKeyCode(packet.g1()));
                }
            }

            case 1 -> {
                parts.add("keypress");
                parts.add(formatKeyCode(packet.g1()));
                parts.add(String.valueOf(packet.g1()));
            }

            case 2 -> {
                parts.add("keyheld");
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    parts.add(formatKeyCode(packet.g1()));
                }
            }

            default -> throw new IllegalStateException("unknown input event type");
        }

        return parts.toString();
    }

    private static String unpackKeyHeldEvent(Packet packet) {
        var parts = new StringJoiner(",");
        var count = packet.g1();

        for (var i = 0; i < count; i++) {
            parts.add(formatKeyCode(packet.g1()));
        }

        return parts.toString();
    }

    private static String formatMouseEventType(int type) {
        return "mouse_" + type;
    }

    private static String formatKeyCode(int type) {
        return "key_" + type;
    }
}
