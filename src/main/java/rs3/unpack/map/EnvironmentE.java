package rs3.unpack.map;

import rs3.util.Packet;

public class EnvironmentE {
    public final boolean unknown1;
    public final int unknown2;
    public final float unknown3;
    public final float unknown4;
    public final float unknown5;
    public final float unknown6;
    public final float unknown7;

    public EnvironmentE(Packet packet) {
        this.unknown1 = packet.g1() == 1;
        this.unknown2 = packet.g1();
        this.unknown3 = packet.gFloat();
        this.unknown4 = packet.gFloat();
        this.unknown5 = packet.gFloat();
        this.unknown6 = packet.gFloat();
        this.unknown7 = packet.gFloat();
    }
}
