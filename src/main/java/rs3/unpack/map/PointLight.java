package rs3.unpack.map;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class PointLight {
    public int level;
    public int x;
    public int y;
    public int z;
    public boolean enabled;
    public boolean extendAbove;
    public boolean extendBelow;
    public int radius;
    public List<Integer> mask = new ArrayList<>();
    public String type;
    public int phase;
    public int colour;
    public String id;
    public float intensity;
    public float attenuationFalloff;
    public float something;
    public boolean shadow;
    public float shadowFactor;

    public PointLight(Packet packet) {
        this.level = packet.g1();
        this.extendAbove = (this.level & 8) != 0;
        this.extendBelow = (this.level & 16) != 0;
        this.level &= 7;
        this.x = packet.g2();
        this.z = packet.g2();
        this.y = packet.g2();
        this.radius = packet.g1();

        for (var i = 0; i < radius * 2 + 1; ++i) {
            var entry = packet.g2();
            this.mask.add(entry);
        }

        this.colour = packet.g2();

        var light = packet.g1();
        var lightType = light & 0b11111;
        var lightPhase = light >> 5;
        this.type = lightType == 31 ? Unpacker.format(Type.LIGHT, packet.g2null()) : "builtin_" + lightType;
        this.phase = lightPhase;
        this.id = Unpacker.format(Type.POINTLIGHT, packet.g2null());
        this.intensity = packet.gFloat();
        this.attenuationFalloff = packet.gFloat();
        this.something = packet.gFloat();
        this.shadow = packet.g1() == 1;
        this.shadowFactor = packet.gFloat();
        this.enabled = packet.g1() == 1;
    }
}
