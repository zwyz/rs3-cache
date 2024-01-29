package rs3.unpack.vfx;

import rs3.util.Packet;

public class VFXAttribute9 extends VFXAttribute {
    public float unknown0;
    public float unknown1;
    public VFXKeyedFloat unknown2;
    public VFXKeyedFloat unknown3;

    public VFXAttribute9(Packet packet, int version) {
        unknown0 = packet.gFloat();
        unknown1 = packet.gFloat();
        unknown2 = new VFXKeyedFloat(packet, version);
        unknown3 = new VFXKeyedFloat(packet, version);
    }

    @Override
    public VFXAttributeType getType() {
        return VFXAttributeType.ATTRIBUTE_9;
    }
}
