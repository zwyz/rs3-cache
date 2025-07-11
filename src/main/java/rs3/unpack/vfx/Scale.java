package rs3.unpack.vfx;

import rs3.util.Packet;

public class Scale extends Module {
    public boolean unknown0;
    public float unknown1;
    public float unknown2;
    public float unknown3;
    public float unknown4;
    public FloatCurve unknown5;
    public FloatCurve unknown6;
    public FloatCurve unknown7;
    public FloatCurve unknown8;

    public Scale(Packet packet, int version) {
        unknown0 = packet.g1() == 1;
        unknown1 = packet.gFloat();
        unknown2 = packet.gFloat();
        unknown3 = packet.gFloat();
        unknown4 = packet.gFloat();
        unknown5 = new FloatCurve(packet, version);
        unknown6 = new FloatCurve(packet, version);
        unknown7 = new FloatCurve(packet, version);
        unknown8 = new FloatCurve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.SCALE;
    }
}
