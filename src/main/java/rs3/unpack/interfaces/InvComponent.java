package rs3.unpack.interfaces;

import rs3.Unpack;
import rs3.util.Packet;

public class InvComponent extends Component {
    public boolean draggable;
    public boolean interactable;
    public boolean usable;
    public boolean swappable;
    public int paddingx;
    public int paddingy;
    public int[] slotoffsetx = new int[20];
    public int[] slotoffsety = new int[20];
    public int[] sloticon = new int[20];
    public String[] legacysloticon = new String[20];
    public String[] objops = new String[5];

    @Override
    protected void decodeSpecific(Packet packet) {
        draggable = packet.g1() == 1; // 0x10000000
        interactable = packet.g1() == 1; // 0x40000000
        usable = packet.g1() == 1; // 0x80000000
        if (Unpack.VERSION >= 245) swappable = packet.g1() == 1; // 0x20000000
        paddingx = packet.g1();
        paddingy = packet.g1();

        for (var i = 0; i < 20; i++) {
            if (packet.g1() == 1) {
                slotoffsetx[i] = packet.g2s();
                slotoffsety[i] = packet.g2s();

                if (Unpack.VERSION < 400) {
                    sloticon[i] = -1;
                    legacysloticon[i] = packet.gjstr();

                    if (legacysloticon[i].isEmpty()) legacysloticon[i] = null;
                } else {
                    sloticon[i] = packet.g4s();
                }
            } else {
                sloticon[i] = -1;
            }
        }

        for (var i = 0; i < 5; i++) {
            objops[i] = packet.gjstr();
        }
    }
}
