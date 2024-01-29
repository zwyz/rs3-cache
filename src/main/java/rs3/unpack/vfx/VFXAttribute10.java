package rs3.unpack.vfx;

import rs3.util.Packet;

public class VFXAttribute10 extends VFXAttribute {
    public int unknown0;
    public int unknown1;
    public int unknown2;

    public VFXAttribute10(Packet packet, int version) {
        unknown0 = packet.g1();
        unknown1 = packet.g1();
        unknown2 = packet.g1();
    }

    @Override
    public VFXAttributeType getType() {
        return VFXAttributeType.ATTRIBUTE_10;
    }
}
