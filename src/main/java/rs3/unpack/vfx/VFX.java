package rs3.unpack.vfx;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VFX {
    public int version;
    public String name;
    public int unknown2;
    public List<ModularParticleEmitter> emitters = new ArrayList<>();

    public VFX(Packet packet) {
        version = packet.g1();
        name = packet.gjstr2();
        unknown2 = packet.g1();

        var count = packet.g1();

        for (var i = 0; i < count; i++) {
            emitters.add(new ModularParticleEmitter(packet, version));
        }

        System.out.println(name);

        for (var emitter : emitters) {
            System.out.println("    " + emitter.name);
        }
    }
}
