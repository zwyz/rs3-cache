package rs3.unpack.vfx;

import rs3.util.Packet;

public class VFXAttribute7 extends VFXAttribute {
    public VFXKeyedFloat unknown0;
    public VFXKeyedFloat unknown1;

    public VFXAttribute7(Packet packet, int version) {
        unknown0 = new VFXKeyedFloat(packet, version);
        unknown1 = new VFXKeyedFloat(packet, version);
    }

    @Override
    public VFXAttributeType getType() {
        return VFXAttributeType.ATTRIBUTE_7;
    }
}
