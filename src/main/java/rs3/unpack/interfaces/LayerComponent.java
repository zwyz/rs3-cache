package rs3.unpack.interfaces;

import rs3.Unpack;
import rs3.util.Packet;

public class LayerComponent extends Component {
    public int scrollwidth;
    public int scrollheight;
    public int[] margin = {0, 0, 0, 0};
    public int[] children;
    public int[] childX;
    public int[] childY;

    @Override
    protected void decodeSpecific(Packet packet) {
        if (version == -2) {
            scrollheight = packet.g2();
            hide = packet.g1() == 1;

            if (Unpack.VERSION < 410) {
                int count = packet.g2();
                children = new int[count];
                childX = new int[count];
                childY = new int[count];

                for (int var16 = 0; var16 < count; var16++) {
                    children[var16] = packet.g2();
                    childX[var16] = packet.g2s();
                    childY[var16] = packet.g2s();
                }
            }

            return;
        }

        scrollwidth = packet.g2();
        scrollheight = packet.g2();

        if (version == -1) {
            if (Unpack.VERSION >= 495) {
                noclickthrough = packet.g1() == 1;
            }
        } else if (version >= 9) {
            margin = new int[]{packet.g1(), packet.g1(), packet.g1(), packet.g1()};
        } else if (version >= 6) {
            margin = new int[]{packet.g2(), packet.g2(), packet.g2(), packet.g2()};
        }
    }
}
