package rs3.unpack.interfaces;

import rs3.Unpack;
import rs3.util.Packet;

public class SpritePart {
    public int graphic;
    public int graphicactive = -1;
    public String legacygraphic = "";
    public String legacygraphicactive = "";
    public int angle2d;
    public boolean tiling;
    public boolean alpha;
    public int trans;
    public int outline;
    public int graphicshadow;
    public boolean vflip;
    public boolean hflip;
    public int colour = 0xffffff;
    public boolean clickmask;
    public int[] edge = {0, 0, 0, 0};

    public void decode(Packet packet, int version) {
        if (version == -2) {
            if (Unpack.VERSION < 400) {
                graphic = -1;
                legacygraphic = packet.gjstr();
                legacygraphicactive = packet.gjstr();
            } else {
                graphic = packet.g4s();
                graphicactive = packet.g4s();
            }

            return;
        }

        graphic = packet.g4s();
        angle2d = packet.g2();
        var flags = packet.g1();
        tiling = (flags & 1) != 0;
        alpha = (flags & 2) != 0;
        trans = packet.g1();
        outline = packet.g1();
        graphicshadow = packet.g4s();
        vflip = packet.g1() == 1;
        hflip = packet.g1() == 1;

        if (Unpack.VERSION >= 537) {
            colour = packet.g4s();
        }

        if (version >= 3) {
            clickmask = packet.g1() == 1;
        }

        if (version >= 6) {
            edge = new int[]{packet.g1(), packet.g1(), packet.g1(), packet.g1()};
        }
    }
}
