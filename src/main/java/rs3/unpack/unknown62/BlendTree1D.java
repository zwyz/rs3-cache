package rs3.unpack.unknown62;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class BlendTree1D extends State {
    public String type = "BlendTree1D";
    public float unknown0;
    public float unknown1;
    public String variable;
    public List<Entry> animations;

    public BlendTree1D(Packet packet) {
        unknown0 = packet.gFloat();
        unknown1 = packet.gFloat();
        variable = packet.gjstr();

        var count = packet.g4s();
        animations = new ArrayList<>(count);

        for (var i = 0; i < count; i++) {
            animations.add(new Entry(packet));
        }
    }

    public static class Entry {
        public float value;
        public State animation;

        public Entry(Packet packet) {
            value = packet.gFloat();
            animation = decodeBlendTree(packet);
        }
    }
}
