package rs3.unpack.interfaces;

import rs3.util.Packet;

public class TextComponent extends Component {
    public TextPart text = new TextPart();

    @Override
    protected void decodeSpecific(Packet packet) {
        text.decode(packet, version);
    }
}
