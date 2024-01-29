package rs3.unpack.map;

import rs3.unpack.Vector3;
import rs3.util.Packet;

public class WaterPatch {
    public final int x;
    public final int z;
    public final int width;
    public final int length;
    public final int unknown1;
    public final Vector3 unknown2;
    public final float unknown3;
    public final int unknown4;
    public final int unknown5;
    public final int unknown6;
    public final int type;

    public WaterPatch(Packet packet) {
        x = packet.g1();
        z = packet.g1();
        width = packet.g1();
        length = packet.g1();
        unknown1 = packet.g2();
        unknown2 = new Vector3(packet);
        unknown3 = packet.gFloat();
        unknown4 = packet.g2();
        unknown5 = packet.g1();
        unknown6 = packet.g1();
        type = packet.g2();
    }
}
