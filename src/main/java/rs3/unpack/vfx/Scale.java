package rs3.unpack.vfx;

import rs3.util.Packet;

public class Scale extends Module {
    public boolean unknown0;
    public float unknown1;
    public float unknown2;
    public float unknown3;
    public float unknown4;
    public ModulePropertyFloatCurve unknown5;
    public ModulePropertyFloatCurve unknown6;
    public ModulePropertyFloatCurve unknown7;
    public ModulePropertyFloatCurve unknown8;

    public Scale(Packet packet, int version) {
        unknown0 = packet.g1() == 1;
        unknown1 = packet.gFloat();
        unknown2 = packet.gFloat();
        unknown3 = packet.gFloat();
        unknown4 = packet.gFloat();
        unknown5 = new ModulePropertyFloatCurve(packet, version);
        unknown6 = new ModulePropertyFloatCurve(packet, version);
        unknown7 = new ModulePropertyFloatCurve(packet, version);
        unknown8 = new ModulePropertyFloatCurve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.SCALE;
    }
}
