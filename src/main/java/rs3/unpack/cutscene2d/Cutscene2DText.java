package rs3.unpack.cutscene2d;

import rs3.util.Packet;

public class Cutscene2DText {
    public String name;
    public int id;
    public float x;
    public float y;

    public Cutscene2DText(Packet packet) {
        name = packet.gjstr2();
        id = packet.g4s();
        x = packet.gFloat();
        y = packet.gFloat();
    }
}
