package rs3.unpack.interfaces;

import rs3.util.Packet;

public class SliderComponent extends Component {
    @Override
    protected void decodeSpecific(Packet packet) {
        throw new UnsupportedOperationException("slider");
    }
}
