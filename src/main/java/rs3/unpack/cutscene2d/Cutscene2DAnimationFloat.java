package rs3.unpack.cutscene2d;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class Cutscene2DAnimationFloat {

    public List<Key> keys;

    public Cutscene2DAnimationFloat(Packet packet) {
        int numKeys = packet.g1();
        keys = new ArrayList<>(numKeys);
        for (var i = 0; i < numKeys; i++) {
            keys.add(new Key(packet));
        }
    }

    public static class Key {
        public float unknown0;
        public float unknown1;

        public Key(Packet packet) {
            unknown0 = packet.gFloat();
            unknown1 = packet.gFloat();
        }
    }
}
