package rs3.unpack.vfx;

import rs3.util.Packet;

public class Acceleration extends Module {
    public float unknown0;
    public float unknown1;
    public FloatCurve unknown2;
    public FloatCurve unknown3;

    public Acceleration(Packet packet, int version) {
        unknown0 = packet.gFloat();
        unknown1 = packet.gFloat();
        unknown2 = new FloatCurve(packet, version);
        unknown3 = new FloatCurve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.ACCELERATION;
    }
}
