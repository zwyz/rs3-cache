package rs3.unpack.unknown62;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

public class Animation extends State {
    public String type = "Animation";
    public String animation;
    public int unknown1;
    public int unknown2;
    public int unknown3;
    public int unknown4;
    public int unknown5;
    public float unknown6;
    public String unknown7;
    public int unknown8;

    public Animation(Packet packet) {
        animation = Unpacker.format(Type.SEQ, packet.g4s());
        unknown1 = packet.g4s();
        unknown2 = packet.g4s();
        unknown3 = packet.g4s();
        unknown4 = packet.g1(); // boolean
        unknown5 = packet.g1(); // boolean

        if (unknown5 != 0) {
            unknown6 = packet.g4s();
            unknown7 = packet.gjstr();
            unknown8 = packet.g4s();
        }
    }
}
