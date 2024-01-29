package rs3.unpack.map;

import rs3.unpack.Vector3;
import rs3.util.Packet;

public class EnvironmentC {
    public final boolean unknown1;
    public final float unknown2;
    public final float unknown3;
    public final float unknown4;
    public final float unknown5;
    public final Vector3 unknown6;
    public final Vector3 unknown7;
    public final Vector3 unknown8;

    public EnvironmentC(Packet packet) {
        this.unknown1 = packet.g1() == 1;
        this.unknown2 = packet.gFloat();
        this.unknown3 = packet.gFloat();
        this.unknown4 = packet.gFloat();
        this.unknown5 = packet.gFloat();
        this.unknown6 = new Vector3(packet);
        this.unknown7 = new Vector3(packet);
        this.unknown8 = new Vector3(packet);
    }
}
