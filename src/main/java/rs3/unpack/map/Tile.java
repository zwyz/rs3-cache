package rs3.unpack.map;

import rs3.util.Packet;

public class Tile {
    public int flags;
    public int height;
    public int underlay;
    public int overlay;
    public int overlayShape;
    public int overlayAngle;

    public int underwaterTexture;
    public int underwaterHeight;
    public int underwaterUnderlay;
    public int underwaterOverlay;

    public Tile(Packet packet) {
        this.flags = packet.g1();
        this.height = packet.g1();

        if (flags == 0) {
            return;
        }

        var underwater = (flags & 0x10) != 0;

        if (underwater) {
            this.underwaterHeight = packet.g1();
        }

        this.underlay = packet.gSmart1or2() - 1;

        if (this.underlay != -1) {
            this.underwaterTexture = packet.g2();
        }

        this.overlay = packet.gSmart1or2() - 1;

        if (underwater) {
            this.underwaterOverlay = packet.gSmart1or2() - 1;
        }

        if (this.overlay != -1) {
            var data = packet.g1();
            this.overlayShape = data >> 2;
            this.overlayAngle = data & 0x3;

            if (underwater) {
                this.underwaterUnderlay = packet.gSmart1or2() - 1;
            }
        }
    }
}
