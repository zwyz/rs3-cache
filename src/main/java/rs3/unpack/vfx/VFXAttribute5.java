package rs3.unpack.vfx;

import rs3.util.Packet;

public class VFXAttribute5 extends VFXAttribute {
    public boolean unknown0;
    public float unknown1;
    public float unknown2;
    public VFXKeyedFloat unknown3;
    public VFXKeyedFloat unknown4;

    public VFXAttribute5(Packet packet, int version) {
        unknown0 = packet.g1() == 1;
        unknown1 = packet.gFloat();
        unknown2 = packet.gFloat();
        unknown3 = new VFXKeyedFloat(packet, version);
        unknown4 = new VFXKeyedFloat(packet, version);
    }

    @Override
    public VFXAttributeType getType() {
        return VFXAttributeType.ATTRIBUTE_5;
    }
}
