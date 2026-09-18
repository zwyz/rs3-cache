package rs3.unpack.interfaces;

import rs3.Unpack;
import rs3.util.Packet;

public class TextPart {
    public int textfont = -1;
    public int legacyfont = -1;
    public boolean fontmono;
    public String text;
    public String textactive;
    public int textlineheight;
    public int textalignh;
    public int textalignv;
    public boolean textshadow;
    public int colour;
    public int colouractive;
    public int trans;
    public int maxlines;
    public int mouseovercolour;
    public int mouseovercolouractive;

    public void decode(Packet packet, int version) {
        if (version == -2) {
            if (Unpack.VERSION < 450) {
                textalignh = packet.g1(); // actually `centre = g1() == 1`
                legacyfont = packet.g1();
            } else {
                textalignh = packet.g1();
                textalignv = packet.g1();
                textlineheight = packet.g1();
                textfont = packet.g2null();
            }

            textshadow = packet.g1() == 1;
            text = packet.gjstr();
            textactive = packet.gjstr();
            colour = packet.g4s();
            colouractive = packet.g4s();
            mouseovercolour = packet.g4s();
            if (Unpack.VERSION >= 245) mouseovercolouractive = packet.g4s();
            return;
        }

        textfont = Unpack.VERSION < 681 ? packet.g2null() : packet.gSmart2or4null();

        if (version >= 2) {
            fontmono = packet.g1() == 1;
        }

        text = packet.gjstr();
        textlineheight = packet.g1();
        textalignh = packet.g1();
        textalignv = packet.g1();
        textshadow = packet.g1() == 1;
        colour = packet.g4s();

        if (Unpack.VERSION >= 582) {
            trans = packet.g1();
        }

        if (version >= 0) {
            maxlines = packet.g1();
        }
    }
}
