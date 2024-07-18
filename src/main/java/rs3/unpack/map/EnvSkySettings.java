package rs3.unpack.map;

import rs3.util.Packet;

public class EnvSkySettings {
    public final int unknown1;
    public final int unknown2;
    public final boolean unknown3;

    public EnvSkySettings(Packet packet) {
        this.unknown1 = packet.g2();
        this.unknown2 = packet.g2();
        this.unknown3 = packet.g1() == 1;
    }
}
