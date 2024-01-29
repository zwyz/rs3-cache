package rs3.unpack.vfx;

import rs3.util.Packet;

public class VFXAttribute8 extends VFXAttribute {
    public int unknown0;

    public VFXAttribute8(Packet packet, int version) {
        this.unknown0 = packet.g1();
    }

    @Override
    public VFXAttributeType getType() {
        return VFXAttributeType.ATTRIBUTE_8;
    }
}
