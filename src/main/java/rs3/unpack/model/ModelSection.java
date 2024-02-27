package rs3.unpack.model;

import rs3.util.Packet;

public class ModelSection {
    public final int flags;
    public final int priority;
    public final int material;
    public final int unknown3;
    public final int[] indices;

    public ModelSection(Packet packet, boolean large) {
        flags = packet.g4sLE();
        priority = packet.g1();
        material = packet.g2LE() - 1;
        unknown3 = packet.g1();

        var indexCount = packet.g2LE();
        indices = new int[indexCount];

        for (var i = 0; i < indexCount; i++) {
            indices[i] = large ? packet.g4sLE() : packet.g2LE();
        }
    }
}
