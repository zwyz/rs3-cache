package rs3.unpack.vfx;

import rs3.util.Packet;

public class Colour extends Module {
    public Vec3Curve unknown0; // color?
    public FloatCurve unknown1; // alpha?
    public Vec3Curve unknown2; // color?
    public FloatCurve unknown3; // alpha?

    public Colour(Packet packet, int version) {
        unknown0 = new Vec3Curve(packet, version);
        unknown1 = new FloatCurve(packet, version);
        unknown2 = new Vec3Curve(packet, version);
        unknown3 = new FloatCurve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.COLOUR;
    }
}
