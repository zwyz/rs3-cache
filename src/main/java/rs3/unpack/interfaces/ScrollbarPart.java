package rs3.unpack.interfaces;

import rs3.util.Packet;

public class ScrollbarPart {
    public boolean unknown17;
    public boolean unknown18;
    public int unknown19;
    public int unknown20;
    public SpritePart background = new SpritePart();
    public SpritePart button = new SpritePart();
    public SpritePart handle = new SpritePart();

    public void decode(Packet packet, int version) {
        unknown17 = packet.g1() == 1;
        unknown18 = packet.g1() == 1;
        unknown19 = packet.g1();
        unknown20 = packet.g1();
        background.decode(packet, version);
        button.decode(packet, version);
        handle.decode(packet, version);
    }
}
