package rs3.unpack.vfx;

import rs3.unpack.Vector3;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class ModularParticleEmitter {
    public String name;
    public int unknown1;
    public int unknown2;
    public int unknown3;
    public int unknown4;
    public int material;
    public int maxParticles;
    public int numTiles;
    public int unknown6;
    public float unknown7;
    public float unknown8;
    public float unknown9;
    public float unknown10;
    public float unknown11;
    public int unknown12;
    public int warmupTime;
    public float unknown13;
    public float lifetime;
    public Vector3 position;
    public Vector3 rotation;
    public List<Module> modules = new ArrayList<>();

    public ModularParticleEmitter(Packet packet, int version) {
        name = packet.gjstr2();

        if (version >= 8) {
            unknown3 = packet.g1();
            unknown4 = packet.g1(); // same as rotation property unknown0
        }

        unknown1 = packet.g1();

        if (version >= 2) {
            unknown2 = packet.g1();
        }

        material = packet.g2();
        maxParticles = packet.g2();

        if (version < 4) {
            var a = packet.g1();
            var b = packet.g1();
            numTiles = b;
            unknown6 = a / b;
        } else {
            numTiles = packet.g1();
            unknown6 = packet.g1();
        }

        if (version >= 8) {
            unknown7 = packet.gFloat();
            unknown8 = packet.gFloat();
            unknown9 = packet.gFloat();
            unknown10 = packet.gFloat();
            unknown11 = packet.gFloat();
            unknown12 = packet.g1();
        }

        warmupTime = packet.g4s();

        if (version >= 8) {
            unknown13 = packet.gFloat();
        }

        lifetime = packet.gFloat();
        position = new Vector3(packet);
        rotation = new Vector3(packet);

        var count = packet.g1();

        for (var i = 0; i < count; i++) {
            var type = packet.g1();

            var module = switch (type) {
                case 0 -> new Flow(packet, version); // (byte, FlowShape, FlowShape, float, float, float, float, float, float, float)
                case 1 -> new Colour(packet, version); // (Vec3Curve, FloatCurve, Vec3Curve, FloatCurve)
                case 2 -> new Gravity(packet, version); // (FloatCurve, FloatCurve)
                case 3 -> new TriggerPlane(packet, version); // (byte, FloatCurve, Vec3Curve, Vec3Curve)
                case 4 -> new Attractor(packet, version); // (Vec3Curve, FloatCurve)
                case 5 -> new Rotation(packet, version); // (boolean, float, float, FloatCurve, FloatCurve)
                case 6 -> new Scale(packet, version); // (boolean, float, float, float, float, FloatCurve, FloatCurve, FloatCurve, FloatCurve)
                case 7 -> new Noise(packet, version); // (FloatCurve, FloatCurve)
                case 8 -> new Flipbook(packet, version); // (byte)
                case 9 -> new Acceleration(packet, version); // (float, float, FloatCurve, FloatCurve)
                case 10 -> new Lighting(packet, version); // (byte, byte, byte)
                case 11 -> new Unknown11(packet, version); // (float, float, float, float, byte)
                case 12 -> new Unknown12(packet, version); // (byte, Unknown, Vec4Curve)
                case 13 -> new Unknown13(packet, version); // (float, float, float, float, byte, Vec4Curve, FloatCurve)
                case 14 -> new Unknown14(packet, version); // (Unknown)
                case 15 -> new Unknown15(packet, version); // (Unknown, FloatCurve, FlowShape, float, float, byte)
                case 16 -> new Unknown16(packet, version); // (Unknown, FloatCurve, Unknown, FloatCurve, Vec3)
                case 17 -> new Unknown17(packet, version); // (byte, Unknown, Vec2Curve)
                default -> throw new IllegalStateException("unknown type " + type);
            };

            modules.add(module);
        }
    }
}
