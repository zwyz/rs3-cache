package rs3.unpack.map;

import rs3.util.Packet;

public class EnvironmentB {
    public final int unknown1;
    public final int unknown2;
    public final boolean unknown3;
    public final float unknown4;
    public final float unknown5;
    public final float unknown6;
    public final float unknown7;

    public EnvironmentB(Packet packet) {
        this.unknown1 = packet.g4s();
        this.unknown2 = packet.g2();
        this.unknown3 = packet.g1() == 1;
        this.unknown4 = packet.gFloat();
        this.unknown5 = packet.gFloat();
        this.unknown6 = packet.gFloat();
        this.unknown7 = packet.gFloat();
    }
}
