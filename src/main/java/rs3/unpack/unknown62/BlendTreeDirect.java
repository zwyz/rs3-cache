package rs3.unpack.unknown62;

import rs3.util.Packet;

public class BlendTreeDirect extends State {
    public String type = "BlendTreeDirect";
    public int unknown0;
    public int unknown1;
    public String unknown2;
    public State unknown3;
    public State unknown4;

    public BlendTreeDirect(Packet packet) {
        unknown0 = packet.g4s();
        unknown1 = packet.g4s();
        unknown2 = packet.gjstr();
        unknown3 = State.decodeBlendTree(packet);
        unknown4 = State.decodeBlendTree(packet);
    }
}
