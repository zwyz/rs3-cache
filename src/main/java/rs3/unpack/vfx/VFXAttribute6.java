package rs3.unpack.vfx;

import rs3.util.Packet;

public class VFXAttribute6 extends VFXAttribute {
    public boolean unknown0;
    public float unknown1;
    public float unknown2;
    public float unknown3;
    public float unknown4;
    public VFXKeyedFloat unknown5;
    public VFXKeyedFloat unknown6;
    public VFXKeyedFloat unknown7;
    public VFXKeyedFloat unknown8;

    public VFXAttribute6(Packet packet, int version) {
        unknown0 = packet.g1() == 1;
        unknown1 = packet.gFloat();
        unknown2 = packet.gFloat();
        unknown3 = packet.gFloat();
        unknown4 = packet.gFloat();
        unknown5 = new VFXKeyedFloat(packet, version);
        unknown6 = new VFXKeyedFloat(packet, version);
        unknown7 = new VFXKeyedFloat(packet, version);
        unknown8 = new VFXKeyedFloat(packet, version);
    }

    @Override
    public VFXAttributeType getType() {
        return VFXAttributeType.ATTRIBUTE_6;
    }
}
