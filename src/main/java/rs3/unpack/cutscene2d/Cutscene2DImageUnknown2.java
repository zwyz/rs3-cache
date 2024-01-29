package rs3.unpack.cutscene2d;

import rs3.util.Packet;

public class Cutscene2DImageUnknown2 {
    public float unknown0;
    public float unknown1;
    public float unknown2;

    public Cutscene2DImageUnknown2(Packet packet) {
        unknown0 = packet.gFloat();
        unknown1 = packet.gFloat();
        unknown2 = packet.gFloat();
    }
}
