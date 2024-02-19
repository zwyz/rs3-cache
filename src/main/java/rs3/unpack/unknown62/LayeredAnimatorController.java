package rs3.unpack.unknown62;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class LayeredAnimatorController extends AnimatorController {
    public String type = "LayeredAnimatorController";
    public List<Layer> layers;

    public LayeredAnimatorController(Packet packet) {
        var count = packet.g4s();
        layers = new ArrayList<>(count);

        for (var i = 0; i < count; i++) {
            layers.add(new Layer(packet));
        }
    }

    public static class Layer {
        public AnimationStateMachine layer;
        public int unknown1;
        public String unknown2;
        public long unknown3;
        public long unknown4;

        public Layer(Packet packet) {
            layer = new AnimationStateMachine(packet);
            unknown1 = packet.g4s();
            unknown2 = packet.gjstr();
            unknown3 = packet.g8s();
            unknown4 = packet.g8s();
        }
    }
}
