package rs3.unpack.vfx;

import rs3.util.Packet;

public class Unknown17 extends Module {
    int unknown1;
    Unknown unknown2;
    Vec2Curve unknown3;

    public Unknown17(Packet packet, int version) {
        this.unknown1 = packet.g1();
        this.unknown2 = new Unknown(packet, version);
        this.unknown3 = new Vec2Curve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.UNKNOWN17;
    }
}
