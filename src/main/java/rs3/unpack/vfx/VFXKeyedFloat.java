package rs3.unpack.vfx;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class VFXKeyedFloat {
    private List<KeyFrame> keyframes = new ArrayList<>();

    public VFXKeyedFloat(Packet packet, int version) {
        var frames = packet.g1();

        if (frames == 1) {
            var x = packet.gFloat();
            var y = packet.gFloat();
            keyframes.add(new KeyFrame(x, y, 0, 0));
        } else {
            for (var i = 0; i < frames; i++) {
                var x = packet.gFloat();
                var y = packet.gFloat();
                var xa = packet.gFloat();
                var ya = packet.gFloat();
                keyframes.add(new KeyFrame(x, y, xa, ya));
            }
        }
    }

    public static class KeyFrame {
        public final float time1;
        public final float value1;
        public final float time2;
        public final float value2;

        public KeyFrame(float time1, float value1, float time2, float value2) {
            this.time1 = time1;
            this.value1 = value1;
            this.time2 = time2;
            this.value2 = value2;
        }
    }
}
