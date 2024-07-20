package rs3.unpack.cutscene2d;

import rs3.util.Packet;

public class Cutscene2DSubtitleElement {
    public String name;
    public int id;
    public float x;
    public float y;

    public Cutscene2DSubtitleElement(Packet packet) {
        name = packet.gjstr2();
        id = packet.g4s();
        x = packet.gFloat();
        y = packet.gFloat();
    }
}
