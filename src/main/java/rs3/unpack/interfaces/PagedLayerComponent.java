package rs3.unpack.interfaces;

import rs3.util.Packet;

public class PagedLayerComponent extends Component {
    @Override
    protected void decodeSpecific(Packet packet) {
        throw new UnsupportedOperationException("pagedlayer");
    }
}
