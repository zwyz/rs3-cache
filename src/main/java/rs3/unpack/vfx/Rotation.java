package rs3.unpack.vfx;

import rs3.util.Packet;

public class Rotation extends Module {
    public boolean unknown0;
    public float unknown1;
    public float unknown2;
    public ModulePropertyFloatCurve unknown3;
    public ModulePropertyFloatCurve unknown4;

    public Rotation(Packet packet, int version) {
        unknown0 = packet.g1() == 1;
        unknown1 = packet.gFloat();
        unknown2 = packet.gFloat();
        unknown3 = new ModulePropertyFloatCurve(packet, version);
        unknown4 = new ModulePropertyFloatCurve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.ROTATION;
    }
}
