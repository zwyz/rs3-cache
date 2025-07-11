package rs3.unpack.vfx;

import rs3.util.Packet;

public class Unknown15 extends Module {
    public Unknown unknown1;
    public FloatCurve unknown2;
    public FlowShape unknown3;
    public float unknown4;
    public float unknown5;
    public int unknown6;

    public Unknown15(Packet packet, int version) {
        this.unknown1 = new Unknown(packet, version);
        this.unknown2 = new FloatCurve(packet, version);
        this.unknown3 = new FlowShape(packet);
        this.unknown4 = packet.gFloat();
        this.unknown5 = packet.gFloat();
        this.unknown6 = packet.g1();
    }

    @Override
    public ModuleType getType() {
        return ModuleType.UNKNOWN15;
    }
}
