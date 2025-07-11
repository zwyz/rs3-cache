package rs3.unpack.vfx;

import rs3.util.Packet;

public class Unknown14 extends Module {
    public Unknown unknown1;

    public Unknown14(Packet packet, int version) {
        this.unknown1 = new Unknown(packet, version);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.UNKNOWN14;
    }
}
