package rs3.unpack.vfx;

import rs3.util.Packet;

public class Acceleration extends Module {
    public float unknown0;
    public float unknown1;
    public ModulePropertyFloatCurve unknown2;
    public ModulePropertyFloatCurve unknown3;

    public Acceleration(Packet packet, int version) {
        unknown0 = packet.gFloat();
        unknown1 = packet.gFloat();
        unknown2 = new ModulePropertyFloatCurve(packet, version);
        unknown3 = new ModulePropertyFloatCurve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.ACCELERATION;
    }
}
