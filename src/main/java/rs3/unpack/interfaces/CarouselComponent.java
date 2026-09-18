package rs3.unpack.interfaces;

import rs3.util.Packet;

public class CarouselComponent extends Component {
    @Override
    protected void decodeSpecific(Packet packet) {
        throw new UnsupportedOperationException("carousel");
    }
}
