package rs3.unpack.interfaces;

import rs3.util.Packet;

public class GridComponent extends Component {
    public int scrollwidth;
    public int scrollheight;
    public int childspacing;
    public int layoutparams_x;
    public int layoutparams_y;
    public boolean layoutparams_mode;

    @Override
    protected void decodeSpecific(Packet packet) {
        scrollwidth = packet.g2();
        scrollheight = packet.g2();
        childspacing = packet.g1();
        layoutparams_x = packet.g2();
        layoutparams_y = packet.g2();
        layoutparams_mode = packet.g1() == 1;
    }
}
