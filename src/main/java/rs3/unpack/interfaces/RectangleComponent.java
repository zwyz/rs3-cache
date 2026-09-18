package rs3.unpack.interfaces;

import rs3.Unpack;
import rs3.util.Packet;

public class RectangleComponent extends Component {
    public int colour;
    public int colouractive;
    public boolean fill;
    public int trans;
    public int mouseovercolour;
    public int mouseovercolouractive;

    @Override
    protected void decodeSpecific(Packet packet) {
        if (version == -2) {
            fill = packet.g1() == 1;
            colour = packet.g4s();
            colouractive = packet.g4s();
            mouseovercolour = packet.g4s();
            if (Unpack.VERSION >= 245) mouseovercolouractive = packet.g4s();
            return;
        }

        colour = packet.g4s();
        fill = packet.g1() == 1;
        trans = packet.g1();
    }
}
