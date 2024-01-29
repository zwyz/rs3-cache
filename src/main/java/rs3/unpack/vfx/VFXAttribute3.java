package rs3.unpack.vfx;

import rs3.util.Packet;

public class VFXAttribute3 extends VFXAttribute {
    public int unknown0;
    public VFXKeyedFloat unknown1;
    public VFXKeyedVector3 unknown2;
    public VFXKeyedVector3 unknown3;

    public VFXAttribute3(Packet packet, int version) {
        unknown0 = packet.g1();
        unknown1 = new VFXKeyedFloat(packet, version);
        unknown2 = new VFXKeyedVector3(packet, version);
        unknown3 = new VFXKeyedVector3(packet, version);
    }

    @Override
    public VFXAttributeType getType() {
        return VFXAttributeType.ATTRIBUTE_3;
    }
}
