package rs3.unpack.map;

import rs3.Unpack;
import rs3.util.Packet;

public class Environment {
    public final EnvLightingSettings lighting;
    public final EnvFogSettings fog;
    public final EnvScatteringSettings scattering;
    public final EnvVolumetricSettings volumetrics;
    public final float unknown;
    public final EnvToneMapSettings toneMap;
    public final EnvBloomSettings unknown6;
    public final EnvSkySettings skybox;
    public final EnvColourGradingSettings colourRemap;
    public final EnvLightProbeSettings lightProbe;

    public Environment(Packet packet) {
        this.lighting = new EnvLightingSettings(packet);
        this.fog = new EnvFogSettings(packet);
        this.scattering = new EnvScatteringSettings(packet);
        this.volumetrics = new EnvVolumetricSettings(packet);
        this.unknown = Unpack.VERSION >= 942 ? packet.gFloat() : (1.0F - 512.0F) * 10.0F;
        this.toneMap = new EnvToneMapSettings(packet);
        this.unknown6 = new EnvBloomSettings(packet);
        this.skybox = new EnvSkySettings(packet);
        // g2s, g1 for reflection
        this.colourRemap = new EnvColourGradingSettings(packet);
        this.lightProbe = new EnvLightProbeSettings(packet);
    }
}
