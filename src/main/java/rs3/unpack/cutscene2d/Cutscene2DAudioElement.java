package rs3.unpack.cutscene2d;

import rs3.util.Packet;

public class Cutscene2DAudioElement {
    public final String name;
    public final int id;

    public Cutscene2DAudioElement(Packet packet) {
        name = packet.gjstr2();
        id = packet.g4s();
    }
}
