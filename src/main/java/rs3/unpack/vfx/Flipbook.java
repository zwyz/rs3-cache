package rs3.unpack.vfx;

import rs3.util.Packet;

public class Flipbook extends Module {
    public int frameRate;

    public Flipbook(Packet packet, int version) {
        this.frameRate = packet.g1();
    }

    @Override
    public ModuleType getType() {
        return ModuleType.FLIPBOOK;
    }
}
