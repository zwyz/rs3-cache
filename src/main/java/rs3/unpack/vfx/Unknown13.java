package rs3.unpack.vfx;

import rs3.util.Packet;

public class Unknown13 extends Module {
    public float unknown1;
    public float unknown2;
    public float unknown3;
    public float unknown4;
    public int unknown5;
    public Vec4Curve unknown6;
    public FloatCurve unknown7;

    public Unknown13(Packet packet, int version) {
        this.unknown1 = packet.gFloat();
        this.unknown2 = packet.gFloat();
        this.unknown3 = packet.gFloat();
        this.unknown4 = packet.gFloat();
        this.unknown5 = packet.g1();
        this.unknown6 = new Vec4Curve(packet, version);
        this.unknown7 = new FloatCurve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.UNKNOWN13;
    }
}
