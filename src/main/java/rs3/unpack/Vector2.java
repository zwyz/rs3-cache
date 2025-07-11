package rs3.unpack;

import rs3.util.Packet;

public class Vector2 {
    public final float x;
    public final float y;

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Packet packet) {
        this.x = packet.gFloat();
        this.y = packet.gFloat();
    }
}
