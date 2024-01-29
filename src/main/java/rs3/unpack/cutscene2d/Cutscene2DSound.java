package rs3.unpack.cutscene2d;

import rs3.util.Packet;

public class Cutscene2DSound {
    public final String name;
    public final int id;

    public Cutscene2DSound(Packet packet) {
        name = packet.gjstr2();
        id = packet.g4s();
    }
}
