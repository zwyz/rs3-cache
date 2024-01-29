package rs3.unpack.vfx;

import rs3.unpack.Vector3;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class VFXKeyedVector3 {
    public List<KeyFrame> keyframes = new ArrayList<>();

    public VFXKeyedVector3(Packet packet, int version) {
        var count = packet.g1();

        if (count == 1) {
            var x = packet.gFloat();
            var y = new Vector3(packet);
            keyframes.add(new KeyFrame(x, y, 0, new Vector3(0, 0, 0)));
        } else {
            for (var i = 0; i < count; i++) {
                var x = packet.gFloat();
                var y = new Vector3(packet);
                var xa = packet.gFloat();
                var ya = new Vector3(packet);
                keyframes.add(new KeyFrame(x, y, xa, ya));
            }
        }
    }

    public static class KeyFrame {
        public final float time1;
        public final Vector3 value1;
        public final float time2;
        public final Vector3 value2;

        public KeyFrame(float time1, Vector3 value1, float time2, Vector3 value2) {
            this.time1 = time1;
            this.value1 = value1;
            this.time2 = time2;
            this.value2 = value2;
        }
    }
}
