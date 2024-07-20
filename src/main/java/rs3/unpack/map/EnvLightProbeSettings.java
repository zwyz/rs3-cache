package rs3.unpack.map;

import rs3.unpack.Vector3;
import rs3.util.Packet;

public class EnvLightProbeSettings {

    public float unknown1;
    public Vector3 unknown2;

    public EnvLightProbeSettings(Packet packet) {
        this.unknown1 = packet.gFloat();
        this.unknown2 = new Vector3(packet);
    }
}
