package rs3.unpack.vfx;

import rs3.util.Packet;

public class Colour extends Module {
    public ModulePropertyVec3Curve unknown0; // color?
    public ModulePropertyFloatCurve unknown1; // alpha?
    public ModulePropertyVec3Curve unknown2; // color?
    public ModulePropertyFloatCurve unknown3; // alpha?

    public Colour(Packet packet, int version) {
        unknown0 = new ModulePropertyVec3Curve(packet, version);
        unknown1 = new ModulePropertyFloatCurve(packet, version);
        unknown2 = new ModulePropertyVec3Curve(packet, version);
        unknown3 = new ModulePropertyFloatCurve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.COLOUR;
    }
}
