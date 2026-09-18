package rs3.unpack.interfaces;

import rs3.util.Packet;

public class CRMViewComponent extends Component {
    public boolean unknown21;
    public int[] unknown22 = new int[0];
    public String unknown23;
    public int unknown24;
    public String[] unknown25 = new String[0];
    public int[] unknown26 = new int[0];

    @Override
    protected void decodeSpecific(Packet packet) {
        unknown21 = packet.g1() == 1;
        unknown22 = new int[packet.g2()];

        for (var i = 0; i < unknown22.length; i++) {
            unknown22[i] = packet.g2();
        }

        unknown23 = packet.gjstr();
        unknown24 = packet.g1();

        unknown25 = new String[packet.g2()];

        for (var i = 0; i < unknown25.length; i++) {
            unknown25[i] = packet.gjstr();
        }

        unknown26 = new int[packet.g2()];

        for (var i = 0; i < unknown26.length; i++) {
            unknown26[i] = packet.g4s();
        }
    }
}
