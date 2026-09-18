package rs3.unpack.interfaces;

import rs3.util.Packet;

public class PanelComponent extends Component {
    public int scrollwidth;
    public int scrollheight;
    public boolean isvertical;
    public int childspacing;

    @Override
    protected void decodeSpecific(Packet packet) {
        scrollwidth = packet.g2();
        scrollheight = packet.g2();
        isvertical = packet.g1() == 1;
        childspacing = packet.g1();
    }
}
