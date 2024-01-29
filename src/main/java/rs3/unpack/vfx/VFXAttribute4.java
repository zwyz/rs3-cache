package rs3.unpack.vfx;

import rs3.util.Packet;

public class VFXAttribute4 extends VFXAttribute {
    public VFXKeyedVector3 unknown0;
    public VFXKeyedFloat unknown1;

    public VFXAttribute4(Packet packet, int version) {
        unknown0 = new VFXKeyedVector3(packet, version);
        unknown1 = new VFXKeyedFloat(packet, version);
    }

    @Override
    public VFXAttributeType getType() {
        return VFXAttributeType.ATTRIBUTE_4;
    }
}
