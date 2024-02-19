package rs3.unpack.unknown62;

import rs3.util.Packet;

public class AnimatorController {
    public static AnimatorController decode(Packet packet) {
        var version = packet.g1();

        if (version != 1) {
            throw new IllegalStateException("unsupported version " + version);
        }

        var type = packet.g1();

        return switch (type) {
            case 0 -> new AnimationStateMachine(packet);
            case 1 -> new LayeredAnimatorController(packet);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
