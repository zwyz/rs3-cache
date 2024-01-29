package rs3.unpack.cutscene2d;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class Cutscene2DFrame {
    public String name;
    public float start;
    public float end;
    public List<Cutscene2DImage> images = new ArrayList<>();
    public List<Cutscene2DSound> sounds = new ArrayList<>();
    public List<Cutscene2DText> texts = new ArrayList<>();

    public Cutscene2DFrame(Packet packet) {
        name = packet.gjstr2();
        start = packet.gFloat();
        end = packet.gFloat();

        var count1 = packet.g1();

        for (var i = 0; i < count1; i++) {
            images.add(new Cutscene2DImage(packet));
        }

        var count2 = packet.g1();

        for (var i = 0; i < count2; i++) {
            sounds.add(new Cutscene2DSound(packet));
        }

        var count3 = packet.g1();

        for (var i = 0; i < count3; i++) {
            texts.add(new Cutscene2DText(packet));
        }
    }
}
