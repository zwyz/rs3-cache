package rs3.unpack.defaults;

import rs3.util.Packet;

import java.util.StringJoiner;

public class InputEventUtil {
    public static String unpackInputEvent(Packet packet) {
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

    public static String unpackKeyHeldEvent(Packet packet) {
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
