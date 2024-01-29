package rs3.unpack.vfx;

import rs3.util.Packet;

public class VFXAttribute0 extends VFXAttribute {
    public int unknown0;
    public VFXUnknown unknown1;
    public VFXUnknown unknown2;
    public float unknown3;
    public float unknown4;
    public float unknown5; // 0-lifetime
    public float unknown10;
    public float unknown6; // 0-lifetime
    public float unknown7; // 0-lifetime
    public float unknown8;
    public float unknown9;
    public float unknown11;

    public VFXAttribute0(Packet packet, int version) {
        unknown0 = packet.g1();
        unknown1 = new VFXUnknown(packet);
        unknown2 = new VFXUnknown(packet);
        unknown3 = packet.gFloat();
        unknown4 = packet.gFloat();
        unknown5 = packet.gFloat();

        if (version >= 3) {
            unknown10 = packet.gFloat();
        }

        unknown6 = packet.gFloat();
        unknown7 = packet.gFloat();
        unknown8 = packet.gFloat();
        unknown9 = packet.gFloat();

        if (version >= 3) {
            unknown11 = packet.gFloat();
        }
    }

    @Override
    public VFXAttributeType getType() {
        return VFXAttributeType.ATTRIBUTE_0;
    }
}
