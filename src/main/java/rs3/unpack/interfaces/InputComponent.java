package rs3.unpack.interfaces;

import rs3.util.Packet;

public class InputComponent extends Component {
    public boolean enabled;
    public int filtermode;
    public int visibilitymode;
    public int unknown8;
    public int[] margin = {0, 0, 0, 0};
    public int keyhandlingmode;
    public int trans;
    public int colour;
    public SpritePart sprite = new SpritePart();
    public TextPart text = new TextPart();
    public ScrollbarPart scrollbar = new ScrollbarPart();

    @Override
    protected void decodeSpecific(Packet packet) {
        enabled = packet.g1() == 1;
        filtermode = packet.g1();
        visibilitymode = packet.g1();
        unknown8 = packet.g2();

        if (version >= 9) {
            margin = new int[]{packet.g1(), packet.g1(), packet.g1(), packet.g1()};
        }

        if (version >= 7) {
            keyhandlingmode = packet.g1();
        }

        trans = packet.g1();
        colour = packet.g4s();
        sprite.decode(packet, version);
        text.decode(packet, version);
        scrollbar.decode(packet, version);
    }
}
