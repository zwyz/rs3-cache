package rs3.unpack.map;

import rs3.util.Packet;

public class EnvColourGradingSettings {
    public final int tex1;
    public final float weighting1;
    public final int tex2;
    public final float weighting2;

    public EnvColourGradingSettings(Packet packet) {
        this.tex1 = packet.g2s();
        this.weighting1 = packet.gFloat();
        this.tex2 = packet.g2s();
        this.weighting2 = packet.gFloat();
    }
}
