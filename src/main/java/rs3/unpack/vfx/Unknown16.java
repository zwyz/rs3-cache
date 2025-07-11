package rs3.unpack.vfx;

import rs3.unpack.Vector3;
import rs3.util.Packet;

public class Unknown16 extends Module {
    public Unknown unknown1;
    public FloatCurve unknown2;
    public Unknown unknown3;
    public FloatCurve unknown4;
    public Vector3 unknown5;

    public Unknown16(Packet packet, int version) {
        this.unknown1 = new Unknown(packet, version);
        this.unknown2 = new FloatCurve(packet, version);
        this.unknown3 = new Unknown(packet, version);
        this.unknown4 = new FloatCurve(packet, version);
        this.unknown5 = new Vector3(packet);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.UNKNOWN16;
    }
}
