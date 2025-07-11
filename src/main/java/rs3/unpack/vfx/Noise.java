package rs3.unpack.vfx;

import rs3.util.Packet;

public class Noise extends Module {
    public FloatCurve unknown0;
    public FloatCurve unknown1;

    public Noise(Packet packet, int version) {
        unknown0 = new FloatCurve(packet, version);
        unknown1 = new FloatCurve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.NOISE;
    }
}
