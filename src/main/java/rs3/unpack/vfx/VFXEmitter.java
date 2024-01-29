package rs3.unpack.vfx;

import rs3.unpack.Vector3;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class VFXEmitter {
    public String name;
    public int unknown1;
    public int unknown2;
    public int material;
    public int unknown4;
    public int unknown5;
    public int unknown6;
    public int unknown7;
    public float lifetime;
    public Vector3 position;
    public Vector3 rotation;
    public List<VFXAttribute> attributes = new ArrayList<>();

    public VFXEmitter(Packet packet, int version) {
        name = packet.gjstr2();
        unknown1 = packet.g1();

        if (version > 1) {
            unknown2 = packet.g1();
        }

        material = packet.g2();
        unknown4 = packet.g2();
        unknown5 = packet.g1();
        unknown6 = packet.g1();
        unknown7 = packet.g4s();
        lifetime = packet.gFloat();
        position = new Vector3(packet);
        rotation = new Vector3(packet);

        var count = packet.g1();

        for (var i = 0; i < count; i++) {
            var type = packet.g1();

            var attribute = switch (type) {
                case 0 -> new VFXAttribute0(packet, version); // (Byte, Unknown, Unknown, Float, Float, Float, Float, Float, Float, Float)
                case 1 -> new VFXAttribute1(packet, version); // (KeyedVector3, KeyedFloat, KeyedVector3, KeyedFloat)
                case 2 -> new VFXAttribute2(packet, version); // (KeyedFloat, KeyedFloat)
                case 3 -> new VFXAttribute3(packet, version); // (Byte, KeyedFloat, KeyedVector3, KeyedVector3)
                case 4 -> new VFXAttribute4(packet, version); // (KeyedVector3, KeyedFloat)
                case 5 -> new VFXAttribute5(packet, version); // (boolean, Float, Float, KeyedFloat, KeyedFloat)
                case 6 -> new VFXAttribute6(packet, version); // (boolean, Float, Float, Float, Float, KeyedFloat, KeyedFloat, KeyedFloat, KeyedFloat)
                case 7 -> new VFXAttribute7(packet, version); // (KeyedFloat, KeyedFloat)
                case 8 -> new VFXAttribute8(packet, version); // (Byte)
                case 9 -> new VFXAttribute9(packet, version); // (Float, Float, KeyedFloat, KeyedFloat)
                case 10 -> new VFXAttribute10(packet, version); // (Byte, Byte, Byte)
                default -> throw new IllegalStateException("unknown type " + type);
            };

            attributes.add(attribute);
        }
    }
}
