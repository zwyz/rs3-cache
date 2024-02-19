package rs3.unpack.unknown62;

import rs3.util.Packet;

public class Transition {
    public String state1;
    public String state2;
    public long duration;
    public int unknown3;
    public int unknown4;
    public String unknown5;
    public int unknown6;

    public Transition(Packet packet) {
        state1 = packet.gjstr();
        state2 = packet.gjstr();
        duration = packet.g8s();
        unknown3 = packet.g4s();
        unknown4 = packet.g4s();
        unknown5 = packet.gjstr();
        unknown6 = packet.g4s();
    }
}
