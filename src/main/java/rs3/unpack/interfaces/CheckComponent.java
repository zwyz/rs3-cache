package rs3.unpack.interfaces;

import rs3.util.Packet;

public class CheckComponent extends Component {
    public boolean enabled = true;
    public boolean checked;
    public int alignment;
    public int buttonsize;
    public int trans;
    public int colour;
    public SpritePart sprite = new SpritePart();
    public TextPart text = new TextPart();

    @Override
    protected void decodeSpecific(Packet packet) {
        enabled = packet.g1() == 1;
        checked = packet.g1() == 1;
        alignment = packet.g1();
        buttonsize = packet.g1();
        trans = packet.g1();
        colour = packet.g4s();
        sprite.decode(packet, version);
        text.decode(packet, version);
    }
}
