package rs3.unpack.interfaces;

import rs3.util.Packet;

public class ButtonComponent extends Component {
    public boolean enabled = true;
    public boolean cantoggle;
    public int unknown1;
    public boolean linkobjoption1 = true;
    public boolean linkobjoption2 = true;
    public int[] textareasizeoffsets = {0, 0, 0, 0};
    public int trans;
    public int colour;
    public SpritePart sprite = new SpritePart();
    public TextPart text = new TextPart();

    @Override
    protected void decodeSpecific(Packet packet) {
        enabled = packet.g1() == 1;
        cantoggle = packet.g1() == 1;
        unknown1 = packet.g1();
        linkobjoption1 = packet.g1() == 1;
        linkobjoption2 = packet.g1() == 1;
        textareasizeoffsets = new int[]{packet.g1(), packet.g1(), packet.g1(), packet.g1()};
        trans = packet.g1();
        colour = packet.g4s();
        sprite.decode(packet, version);
        text.decode(packet, version);
    }
}
