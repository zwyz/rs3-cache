package rs3.unpack;

import rs3.util.Packet;

public class Vector4 {
    public final float x;
    public final float y;
    public final float z;
    public final float w;

    public Vector4(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4(Packet packet) {
        this.x = packet.gFloat();
        this.y = packet.gFloat();
        this.z = packet.gFloat();
        this.w = packet.gFloat();
    }
}
