package rs3.unpack.vfx;

import rs3.util.Packet;

public class Attractor extends Module {

    public Vec3Curve position;
    public FloatCurve strength;

    public Attractor(Packet packet, int version) {
        position = new Vec3Curve(packet, version);
        strength = new FloatCurve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.ATTRACTOR;
    }
}
