package rs3.unpack.vfx;

import rs3.util.Packet;

public class Attractor extends Module {
    public ModulePropertyVec3Curve unknown0;
    public ModulePropertyFloatCurve unknown1;

    public Attractor(Packet packet, int version) {
        unknown0 = new ModulePropertyVec3Curve(packet, version);
        unknown1 = new ModulePropertyFloatCurve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.ATTRACTOR;
    }
}
