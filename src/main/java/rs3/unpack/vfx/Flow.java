package rs3.unpack.vfx;

import rs3.util.Packet;

public class Flow extends Module {
    public int flags;
    public FlowShape shape1;
    public FlowShape shape2;
    public float unknown3;
    public float spawnRateNum;
    public float spawnRateDenom; // 0-lifetime
    public float unknown10;
    public float lifetimeStart; // 0-lifetime
    public float lifetimeEnd; // 0-lifetime
    public float unknown8;
    public float unknown9;
    public float unknown11;

    public Flow(Packet packet, int version) {
        flags = packet.g1();
        // 0x2 = single burst spawn
        shape1 = new FlowShape(packet);
        shape2 = new FlowShape(packet);
        unknown3 = packet.gFloat();
        spawnRateNum = packet.gFloat();
        spawnRateDenom = packet.gFloat();

        if (version >= 3) {
            unknown10 = packet.gFloat();
        }

        lifetimeStart = packet.gFloat();
        lifetimeEnd = packet.gFloat();
        unknown8 = packet.gFloat();
        unknown9 = packet.gFloat();

        if (version >= 3) {
            unknown11 = packet.gFloat();
        }
    }

    @Override
    public ModuleType getType() {
        return ModuleType.FLOW;
    }
}
