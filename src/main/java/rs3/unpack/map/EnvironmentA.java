package rs3.unpack.map;

import rs3.util.Packet;

public class EnvironmentA {
    public final int unknown1;
    public final int unknown2;
    public final int unknown3;
    public final int unknown4;
    public final int unknown5;
    public final int unknown6;
    public final int unknown7;
    public final float unknown8;
    public final float unknown9;
    public final float unknown10;

    public EnvironmentA(Packet packet) {
        this.unknown1 = packet.g4s();
        this.unknown2 = packet.g2();
        this.unknown3 = packet.g2();
        this.unknown4 = packet.g2();
        this.unknown5 = packet.g2();
        this.unknown6 = packet.g2();
        this.unknown7 = packet.g2();
        this.unknown8 = packet.gFloat();
        this.unknown9 = packet.gFloat();
        this.unknown10 = packet.gFloat();
    }
}
