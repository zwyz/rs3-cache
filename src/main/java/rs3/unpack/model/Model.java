package rs3.unpack.model;

import rs3.util.Packet;

import java.util.ArrayList;

public class Model {
    public ModelHeader header;
    public ArrayList<ModelSection> sections;
    public ArrayList<ModelBillboard> billboards;
    public ArrayList<ModelParticleEmitter> particleEmitters;
    public ArrayList<ModelParticleEffector> particleEffectors;

    public Model(Packet packet) {
        var unknownA = packet.g1();
        var unknownB = packet.g1();
        var unknownC = packet.g1();

        var sectionCount = packet.g2LE();
        var billboardCount = packet.g1();
        var particleEmitterCount = packet.g1();
        var particleEffectorCount = packet.g1();

        header = new ModelHeader(packet);

        sections = new ArrayList<>(sectionCount);

        for (var i = 0; i < sectionCount; i++) {
            sections.add(new ModelSection(packet, header.vertexX.length >= 0xffff));
        }

        billboards = new ArrayList<>(billboardCount);

        for (var i = 0; i < billboardCount; i++) {
            billboards.add(new ModelBillboard(packet));
        }

        particleEmitters = new ArrayList<>(particleEmitterCount);

        for (var i = 0; i < particleEmitterCount; i++) {
            particleEmitters.add(new ModelParticleEmitter(packet));
        }

        particleEffectors = new ArrayList<>(particleEffectorCount);

        for (var index = 0; index < particleEffectorCount; index++) {
            particleEffectors.add(new ModelParticleEffector(packet));
        }
    }
}
