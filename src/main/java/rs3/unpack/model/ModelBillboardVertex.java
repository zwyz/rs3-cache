package rs3.unpack.model;

import rs3.util.Packet;

public class ModelBillboardVertex {
    public int unknown1;
    public float x;
    public float y;
    public float z;
    public float unknown5;
    public int unknown6;
    public int unknown7;
    public int id;
    public int unknown9;
    public int unknown10;
    public int unknown11;
    public int distance;

    public ModelBillboardVertex(Packet packet) {
        unknown1 = packet.g4sLE();
        x = packet.gFloatLE();
        y = packet.gFloatLE();
        z = packet.gFloatLE();
        unknown5 = packet.gFloatLE();
        unknown6 = packet.g2LE();
        unknown7 = packet.g1();
        id = packet.g2LE();
        unknown9 = packet.g2LE();
        unknown10 = packet.g2LE();
        unknown11 = packet.g2LE();
        distance = packet.g1();
    }
}
