package rs3.unpack.unknown62;

import rs3.util.Packet;

public class NamedState {
    public String name;
    public Object state;

    public NamedState(Packet packet) {
        name = packet.gjstr();
        var type = packet.g4s();

        state = switch (type) {
            case 0 -> null;
            case 1 -> new Animation(packet);
            case 2 -> State.decodeBlendTree(packet);
            default -> throw new IllegalStateException("invalid type " + type);
        };
    }
}
