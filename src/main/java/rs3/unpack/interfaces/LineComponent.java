package rs3.unpack.interfaces;

import rs3.Unpack;
import rs3.util.Packet;

public class LineComponent extends Component {
    public int linewid;
    public int colour;
    public boolean linedirection;

    @Override
    protected void decodeSpecific(Packet packet) {
        linewid = packet.g1();
        colour = packet.g4s();

        if (Unpack.VERSION >= 493) {
            linedirection = packet.g1() == 1;
        }
    }
}
