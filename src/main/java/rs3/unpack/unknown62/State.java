package rs3.unpack.unknown62;

import rs3.util.Packet;

public class State {
    public static State decodeBlendTree(Packet packet) {
        var type = packet.g4s();

        return switch (type) {
            case 0 -> new Animation(packet);
            case 1 -> new BlendTreeDirect(packet);
            case 2 -> new BlendTree1D(packet);
            case 3 -> new BlendTree2D(packet);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
