package rs3.unpack.model;

import rs3.util.Packet;

public class ModelParticleEmitter {
    public int type;
    public float x1;
    public float y1;
    public float z1;
    public int unknown1;
    public int unknown2;

    public float x2;
    public float y2;
    public float z2;
    public int unknown3;
    public int unknown4;

    public float x3;
    public float y3;
    public float z3;
    public int unknown5;
    public int unknown6;

    public ModelParticleEmitter(Packet packet) {
        type = packet.g2LE();

        x1 = packet.gFloat();
        y1 = packet.gFloatLE();
        z1 = packet.gFloatLE();
        unknown1 = packet.g2sLE();
        unknown2 = packet.g2sLE();

        x2 = packet.gFloatLE();
        y2 = packet.gFloatLE();
        z2 = packet.gFloatLE();
        unknown3 = packet.g2LE();
        unknown4 = packet.g2LE();

        x3 = packet.gFloatLE();
        y3 = packet.gFloatLE();
        z3 = packet.gFloatLE();
        unknown5 = packet.g2sLE();
        unknown6 = packet.g2sLE();
    }
}
