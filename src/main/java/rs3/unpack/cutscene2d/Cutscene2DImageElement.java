package rs3.unpack.cutscene2d;

import rs3.util.Packet;

public class Cutscene2DImageElement {
    public String name;
    public int height;
    public int width;
    public int id;
    public Cutscene2DAnimationFloat animation1;
    public Cutscene2DAnimationFloat animation2;
    public Cutscene2DAnimationVector3 animation3;
    public Cutscene2DAnimationVector3 animation4;

    public Cutscene2DImageElement(Packet packet) {
        name = packet.gjstr2();
        height = packet.g2();
        width = packet.g2();
        id = packet.g4s();

        animation1 = new Cutscene2DAnimationFloat(packet);
        animation2 = new Cutscene2DAnimationFloat(packet);
        animation3 = new Cutscene2DAnimationVector3(packet);
        animation4 = new Cutscene2DAnimationVector3(packet);
    }
}
