package rs3.unpack.model;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class ModelBillboard {
    public int unknown1;
    public int unknown2;
    public int unknown3;
    public List<ModelBillboardVertex> vertices;

    public ModelBillboard(Packet packet) {
        unknown1 = packet.g1();
        unknown2 = packet.g2LE();
        unknown3 = packet.g2LE();

        var count = packet.g2LE();
        vertices = new ArrayList<>(count);

        for (var i = 0; i < count; i++) {
            vertices.add(new ModelBillboardVertex(packet));
        }
    }
}
