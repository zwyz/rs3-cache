package rs3.unpack.ui_anim;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class Anim {
    public int mode;
    public int curve;
    public int easingType;
    public boolean easingUnknown;
    public int target;
    public int targetMode;
    public List<List<Integer>> values = new ArrayList<>();

    public Anim(Packet packet) {
        mode = packet.g1();

        if (mode == 1) {
            curve = packet.g4s();
        } else if (mode == 2) {
            easingType = packet.g4s();
            easingUnknown = packet.g1() == 1;
        } else {
            throw new IllegalStateException();
        }

        target = packet.g1();
        targetMode = packet.g1();

        var count = packet.g2();

        for (var i = 0; i < count; i++) {
            // unconfirmed:
            // 0 = position
            // 1 = position_x
            // 2 = position_y
            // 3 = size
            // 4 = size_x
            // 5 = size_y
            // 6 = rgb
            // 7 = r
            // 8 = g
            // 9 = b
            // 10 = alpha

            if (target == 0 || target == 3) { // position, size
                var x = packet.g4s();
                var y = packet.g4s();
                values.add(List.of(x, y));
            } else if (target == 6) { // rgb
                var x = packet.g4s();
                var y = packet.g4s();
                var z = packet.g4s();
                values.add(List.of(x, y, z));
            } else { // alpha, r, g, b, position_x, position_y, size_x, size_y
                var x = packet.g4s();
                values.add(List.of(x));
            }
        }
    }
}
