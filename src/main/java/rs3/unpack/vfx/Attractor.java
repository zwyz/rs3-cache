package rs3.unpack.vfx;

import rs3.util.Packet;

public class Attractor extends Module {

    public ModulePropertyVec3Curve position;
    public ModulePropertyFloatCurve strength;

    public Attractor(Packet packet, int version) {
        position = new ModulePropertyVec3Curve(packet, version);
        strength = new ModulePropertyFloatCurve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.ATTRACTOR;
    }
}
