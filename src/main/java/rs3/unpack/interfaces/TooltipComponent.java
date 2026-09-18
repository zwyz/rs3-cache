package rs3.unpack.interfaces;

import rs3.util.Packet;

public class TooltipComponent extends Component {
    public String text = "";

    @Override
    protected void decodeSpecific(Packet packet) {
        text = packet.gjstr();
    }
}
