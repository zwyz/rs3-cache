package rs3.unpack.vfx;

import rs3.util.Packet;

public class Unknown {
    public int unknown1;
    public float unknown2;
    public float unknown3;

    public Unknown(Packet packet, int version) {
        unknown1 = packet.g1();
        unknown2 = packet.gFloat();
        unknown3 = packet.gFloat();
    }
}
