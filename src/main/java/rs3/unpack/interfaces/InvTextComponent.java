package rs3.unpack.interfaces;

import rs3.Unpack;
import rs3.util.Packet;

public class InvTextComponent extends Component {
    public int textalignh;
    public int textfont = -1;
    public int legacyfont = -1;
    public boolean textshadow;
    public int colour;
    public int paddingx;
    public int paddingy;
    public boolean interactable;
    public String[] objops = new String[5];

    @Override
    protected void decodeSpecific(Packet packet) {
        if (Unpack.VERSION < 450) {
            textalignh = packet.g1(); // actually `centre = g1() == 1`
            legacyfont = packet.g1();
        } else {
            textalignh = packet.g1();
            textfont = packet.g2null();
        }

        textshadow = packet.g1() == 1;
        colour = packet.g4s();
        paddingx = packet.g2s();
        paddingy = packet.g2s();
        interactable = packet.g1() == 1; // 0x40000000

        for (var i = 0; i < 5; i++) {
            objops[i] = packet.gjstr();
        }
    }
}
