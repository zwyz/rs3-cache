package rs3.unpack.vfx;

import rs3.util.Packet;

public class Lighting extends Module {
    public int unknown0;
    public int unknown1;
    public int unknown2;

    public Lighting(Packet packet, int version) {
        unknown0 = packet.g1();
        unknown1 = packet.g1();
        unknown2 = packet.g1();
    }

    @Override
    public ModuleType getType() {
        return ModuleType.LIGHTING;
    }
}
