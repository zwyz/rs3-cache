package rs3.unpack.vfx;

import rs3.unpack.Vector2;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class Vec2Curve {
    public List<KeyFrame> keyframes = new ArrayList<>();

    public Vec2Curve(Packet packet, int version) {
        var count = packet.g1();

        if (count == 1) {
            var x = packet.gFloat();
            var y = new Vector2(packet);
            keyframes.add(new KeyFrame(x, y, 0, new Vector2(0, 0)));
        } else {
            for (var i = 0; i < count; i++) {
                var x = packet.gFloat();
                var y = new Vector2(packet);
                var xa = packet.gFloat();
                var ya = new Vector2(packet);
                keyframes.add(new KeyFrame(x, y, xa, ya));
            }
        }
    }

    public static class KeyFrame {
        public final float time1;
        public final Vector2 value1;
        public final float time2;
        public final Vector2 value2;

        public KeyFrame(float time1, Vector2 value1, float time2, Vector2 value2) {
            this.time1 = time1;
            this.value1 = value1;
            this.time2 = time2;
            this.value2 = value2;
        }
    }
}
