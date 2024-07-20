package rs3.unpack.vfx;

import rs3.unpack.Vector3;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class ModularParticleEmitter {
    public String name;
    public int unknown1;
    public int unknown2;
    public int material;
    public int maxParticles;
    public int numTiles;
    public int unknown6;
    public int warmupTime;
    public float lifetime;
    public Vector3 position;
    public Vector3 rotation;
    public List<Module> modules = new ArrayList<>();

    public ModularParticleEmitter(Packet packet, int version) {
        name = packet.gjstr2();
        unknown1 = packet.g1();

        if (version > 1) {
            unknown2 = packet.g1();
        }

        material = packet.g2();
        maxParticles = packet.g2();
        numTiles = packet.g1();
        unknown6 = packet.g1();
        warmupTime = packet.g4s();
        lifetime = packet.gFloat();
        position = new Vector3(packet);
        rotation = new Vector3(packet);

        var count = packet.g1();

        for (var i = 0; i < count; i++) {
            var type = packet.g1();

            var module = switch (type) {
                case 0 -> new Flow(packet, version); // (Byte, Unknown, Unknown, Float, Float, Float, Float, Float, Float, Float)
                case 1 -> new Colour(packet, version); // (KeyedVector3, KeyedFloat, KeyedVector3, KeyedFloat)
                case 2 -> new Gravity(packet, version); // (KeyedFloat, KeyedFloat)
                case 3 -> new TriggerPlane(packet, version); // (Byte, KeyedFloat, KeyedVector3, KeyedVector3)
                case 4 -> new Attractor(packet, version); // (KeyedVector3, KeyedFloat)
                case 5 -> new Rotation(packet, version); // (boolean, Float, Float, KeyedFloat, KeyedFloat)
                case 6 -> new Scale(packet, version); // (boolean, Float, Float, Float, Float, KeyedFloat, KeyedFloat, KeyedFloat, KeyedFloat)
                case 7 -> new Noise(packet, version); // (KeyedFloat, KeyedFloat)
                case 8 -> new Flipbook(packet, version); // (Byte)
                case 9 -> new Acceleration(packet, version); // (Float, Float, KeyedFloat, KeyedFloat)
                case 10 -> new Lighting(packet, version); // (Byte, Byte, Byte)
                default -> throw new IllegalStateException("unknown type " + type);
            };

            modules.add(module);
        }
    }
}
