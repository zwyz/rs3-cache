package rs3.unpack.cutscene2d;

import rs3.util.Packet;

public class Cutscene2DImageUnknown1 {
    public float unknown0;
    public float unknown1;

    public Cutscene2DImageUnknown1(Packet packet) {
        unknown0 = packet.gFloat();
        unknown1 = packet.gFloat();
    }
}
