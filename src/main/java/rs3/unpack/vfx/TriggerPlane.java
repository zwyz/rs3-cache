package rs3.unpack.vfx;

import rs3.util.Packet;

public class TriggerPlane extends Module {
    public int unknown0;
    public FloatCurve unknown1;
    public Vec3Curve unknown2;
    public Vec3Curve unknown3;

    public TriggerPlane(Packet packet, int version) {
        unknown0 = packet.g1();
        unknown1 = new FloatCurve(packet, version);
        unknown2 = new Vec3Curve(packet, version);
        unknown3 = new Vec3Curve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.TRIGGER_PLANE;
    }
}
