package rs3.unpack.vfx;

import rs3.unpack.Vector3;
import rs3.util.Packet;

public class VFXUnknown {
    public int type2;
    public boolean unknown1;
    public Vector3 unknown2; // position?
    public Vector3 unknown3; // angle
    public float unknown38;
    public float unknown3C;
    public float unknown40;

    public VFXUnknown(Packet packet) {
        type2 = packet.g1();
        unknown1 = packet.g1() == 1;
        unknown2 = new Vector3(packet);
        unknown3 = new Vector3(packet);

        switch (type2) {
            case 0 -> {
                // no extra data
            }

            case 1 -> {
                unknown38 = packet.gFloat();
            }

            case 2 -> {
                unknown40 = packet.gFloat();
                unknown38 = packet.gFloat();
            }

            case 3 -> {
                unknown40 = packet.gFloat();
                unknown3C = packet.gFloat();
                unknown38 = packet.gFloat();
            }

            case 4, 6, 7 -> {
                unknown38 = packet.gFloat();
            }

            case 5 -> {
                unknown3C = packet.gFloat();
                unknown38 = packet.gFloat();
                unknown40 = packet.gFloat();
            }

            default -> {
                throw new IllegalStateException("unknown type " + type2);
            }
        }
    }
}
