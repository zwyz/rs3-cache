package rs3.unpack.cutscene2d;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class Cutscene2DImage {
    public String name;
    public int height;
    public int width;
    public int id;
    public List<Cutscene2DImageUnknown1> unknown4 = new ArrayList<>();
    public List<Cutscene2DImageUnknown1> unknown5 = new ArrayList<>();
    public List<Cutscene2DImageUnknown2> unknown6 = new ArrayList<>();
    public List<Cutscene2DImageUnknown2> unknown7 = new ArrayList<>();

    public Cutscene2DImage(Packet packet) {
        name = packet.gjstr2();
        height = packet.g2();
        width = packet.g2();
        id = packet.g4s();

        var count1 = packet.g1();

        for (var i = 0; i < count1; i++) {
            unknown4.add(new Cutscene2DImageUnknown1(packet));
        }

        var count2 = packet.g1();

        for (var i = 0; i < count2; i++) {
            unknown5.add(new Cutscene2DImageUnknown1(packet));
        }

        var count3 = packet.g1();

        for (var i = 0; i < count3; i++) {
            unknown6.add(new Cutscene2DImageUnknown2(packet));
        }

        var count4 = packet.g1();

        for (var i = 0; i < count4; i++) {
            unknown7.add(new Cutscene2DImageUnknown2(packet));
        }
    }
}
