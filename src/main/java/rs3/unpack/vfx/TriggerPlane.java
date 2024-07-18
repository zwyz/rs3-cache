package rs3.unpack.vfx;

import rs3.util.Packet;

public class TriggerPlane extends Module {
    public int unknown0;
    public ModulePropertyFloatCurve unknown1;
    public ModulePropertyVec3Curve unknown2;
    public ModulePropertyVec3Curve unknown3;

    public TriggerPlane(Packet packet, int version) {
        unknown0 = packet.g1();
        unknown1 = new ModulePropertyFloatCurve(packet, version);
        unknown2 = new ModulePropertyVec3Curve(packet, version);
        unknown3 = new ModulePropertyVec3Curve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.TRIGGER_PLANE;
    }
}
