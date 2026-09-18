package rs3.unpack.interfaces;

import rs3.util.Packet;

public class GraphicComponent extends Component {
    public SpritePart sprite = new SpritePart();

    @Override
    protected void decodeSpecific(Packet packet) {
        sprite.decode(packet, version);
    }
}
