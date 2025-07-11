package rs3.unpack.vfx;

import rs3.util.Packet;

public class Unknown12 extends Module {
    public int unknown1;
    public Unknown unknown2;
    public Vec3Curve unknown3;

    public Unknown12(Packet packet, int version) {
        this.unknown1 = packet.g1();
        this.unknown2 = new Unknown(packet, version);
        this.unknown3 = new Vec3Curve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.UNKNOWN12;
    }
}
