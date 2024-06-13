package rs3.unpack.map;

import rs3.util.Packet;

public class EnvVolumetricsSettings {
    public final float density;
    public final float inscattering;
    public final float outscattering;
    public final float skyboxDensityMultiplier;
    public final float exaggeration;
    public final float g;
    public final float skyG;
    public final float bilateralBlurDepth;
    public final int litFogColour;
    public final int unlitFogColour;

    public EnvVolumetricsSettings(Packet packet) {
        this.density = packet.gFloat();
        this.inscattering = packet.gFloat();
        this.outscattering = packet.gFloat();
        this.skyboxDensityMultiplier = packet.gFloat();
        this.exaggeration = packet.gFloat();
        this.g = packet.gFloat();
        this.skyG = packet.gFloat();
        this.bilateralBlurDepth = packet.gFloat();
        this.litFogColour = packet.g4s();
        this.unlitFogColour = packet.g4s();
    }
}
