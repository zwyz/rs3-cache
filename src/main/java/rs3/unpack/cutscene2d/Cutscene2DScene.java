package rs3.unpack.cutscene2d;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class Cutscene2DScene {
    public String name;
    public float start;
    public float end;
    public List<Cutscene2DImageElement> images = new ArrayList<>();
    public List<Cutscene2DAudioElement> audio = new ArrayList<>();
    public List<Cutscene2DSubtitleElement> subtitles = new ArrayList<>();

    public Cutscene2DScene(Packet packet) {
        name = packet.gjstr2();
        start = packet.gFloat();
        end = packet.gFloat();

        var count1 = packet.g1();

        for (var i = 0; i < count1; i++) {
            images.add(new Cutscene2DImageElement(packet));
        }

        var count2 = packet.g1();

        for (var i = 0; i < count2; i++) {
            audio.add(new Cutscene2DAudioElement(packet));
        }

        var count3 = packet.g1();

        for (var i = 0; i < count3; i++) {
            subtitles.add(new Cutscene2DSubtitleElement(packet));
        }
    }
}
