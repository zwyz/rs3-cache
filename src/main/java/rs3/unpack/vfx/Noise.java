package rs3.unpack.vfx;

import rs3.util.Packet;

public class Noise extends Module {
    public ModulePropertyFloatCurve unknown0;
    public ModulePropertyFloatCurve unknown1;

    public Noise(Packet packet, int version) {
        unknown0 = new ModulePropertyFloatCurve(packet, version);
        unknown1 = new ModulePropertyFloatCurve(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.NOISE;
    }
}
