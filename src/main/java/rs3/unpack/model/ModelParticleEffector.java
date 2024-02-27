package rs3.unpack.model;

import rs3.util.Packet;

public class ModelParticleEffector {
    public int type;
    public float x;
    public float y;
    public float z;
    public int unknown1;
    public int unknown2;

    public ModelParticleEffector(Packet packet) {
        type = packet.g2LE();
        x = packet.gFloatLE();
        y = packet.gFloatLE();
        z = packet.gFloatLE();
        unknown1 = packet.g2sLE();
        unknown2 = packet.g2sLE();
    }
}
