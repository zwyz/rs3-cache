package rs3.unpack.vfx;

import rs3.util.Packet;

public class VFXAttribute1 extends VFXAttribute {
    public VFXKeyedVector3 unknown0; // color?
    public VFXKeyedFloat unknown1; // alpha?
    public VFXKeyedVector3 unknown2; // color?
    public VFXKeyedFloat unknown3; // alpha?

    public VFXAttribute1(Packet packet, int version) {
        unknown0 = new VFXKeyedVector3(packet, version);
        unknown1 = new VFXKeyedFloat(packet, version);
        unknown2 = new VFXKeyedVector3(packet, version);
        unknown3 = new VFXKeyedFloat(packet, version);
    }

    @Override
    public VFXAttributeType getType() {
        return VFXAttributeType.ATTRIBUTE_1;
    }
}
