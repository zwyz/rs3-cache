package rs3.unpack.unknown62;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class BlendTree2D extends State {
    public String type = "BlendTree2D";
    public String variable1;
    public String variable2;
    public List<Entry> animations;
    public List<Integer> unknown3;

    public BlendTree2D(Packet packet) {
        variable1 = packet.gjstr();
        variable2 = packet.gjstr();

        var count1 = packet.g4s();
        animations = new ArrayList<>(count1);

        for (var i = 0; i < count1; i++) {
            animations.add(new Entry(packet));
        }

        var count2 = packet.g4s();
        unknown3 = new ArrayList<>(count2);

        for (var i = 0; i < count2; i++) {
            unknown3.add(packet.g4s());
        }
    }

    public static class Entry {
        public float value1;
        public float value2;
        public State animation;

        public Entry(Packet packet) {
            value1 = packet.gFloat();
            value2 = packet.gFloat();
            animation = decodeBlendTree(packet);
        }
    }
}
