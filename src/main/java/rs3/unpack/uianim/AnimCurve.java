package rs3.unpack.uianim;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class AnimCurve {
    public List<List<Float>> keyframes = new ArrayList<>();

    public AnimCurve(Packet packet) {
        var count = packet.g1();

        for (var i = 0; i < count; i++) {
            var a = packet.gFloat();
            var b = packet.gFloat();
            var c = packet.gFloat();
            var d = packet.gFloat();
            keyframes.add(List.of(a, b, c, d));
        }
    }
}
