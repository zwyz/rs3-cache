package rs3.unpack.map;

import rs3.util.Packet;

public class EnvToneMapSettings {
    public final boolean toneMapEnabled;
    public final int toneMapOperator;
    public final float minBlackLum;
    public final float maxWhiteLum;
    public final float exposureKey;
    public final float minAutoExposure;
    public final float maxAutoExposure;

    public EnvToneMapSettings(Packet packet) {
        this.toneMapEnabled = packet.g1() == 1;
        this.toneMapOperator = packet.g1();
        this.minBlackLum = packet.gFloat();
        this.maxWhiteLum = packet.gFloat();
        this.exposureKey = packet.gFloat();
        this.minAutoExposure = packet.gFloat();
        this.maxAutoExposure = packet.gFloat();
    }
}
