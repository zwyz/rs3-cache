package rs3.unpack.cutscene2d;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class Cutscene2D {
    public int version;
    public int width;
    public int height;
    public int text;
    public List<Cutscene2DScene> scenes = new ArrayList<>();

    public Cutscene2D(Packet packet) {
        version = packet.g1();

        if (version != 1) {
            throw new IllegalStateException();
        }

        width = packet.g2();
        height = packet.g2();
        text = packet.g2();

        var count = packet.g1();

        for (var i = 0; i < count; i++) {
            scenes.add(new Cutscene2DScene(packet));
        }
    }
}
