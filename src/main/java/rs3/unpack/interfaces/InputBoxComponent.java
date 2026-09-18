package rs3.unpack.interfaces;

import rs3.Unpack;
import rs3.util.Packet;

public class InputBoxComponent extends Component {
    public int unknown200;
    public int unknown201;
    public int textalignh;
    public int textalignv;
    public int textlineheight;
    public int textfont = -1;
    public int legacyfont = -1;
    public boolean textshadow;
    public int colour;

    @Override
    protected void decodeSpecific(Packet packet) {
        unknown200 = packet.g2();
        unknown201 = packet.g1();

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
        colour = packet.g4s();
    }
}
