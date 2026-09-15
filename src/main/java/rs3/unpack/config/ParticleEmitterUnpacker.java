package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class ParticleEmitterUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.PARTICLE_EMITTER, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("coneangle=" + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2());
            case 2 -> lines.add("unknown2=" + packet.g1());
            case 3 -> lines.add("speed=" + packet.g4s() + "," + packet.g4s());
            case 4 -> lines.add("speedcontrol=" + packet.g1() + "," + packet.g1s());
            case 5 -> lines.add("size=" + packet.g2());
            case 6 -> lines.add("rgba=" + packet.g4s() + "," + packet.g4s());
            case 7 -> lines.add("lifetime=" + packet.g2() + "," + packet.g2());
            case 8 -> lines.add("emissionrate=" + packet.g2() + "," + packet.g2());

            case 9 -> {
                var count = packet.g1();
                var parts = new ArrayList<String>();

                for (var i = 0; i < count; i++) {
                    parts.add("" + packet.g2());
                }

                lines.add("worldeffectors=" + String.join(",", parts));
            }

            case 10 -> {
                var count = packet.g1();
                var parts = new ArrayList<String>();

                for (var i = 0; i < count; i++) {
                    parts.add("" + packet.g2());
                }

                lines.add("localeffectors=" + String.join(",", parts));
            }

            case 12 -> lines.add("killbelowlevel=" + packet.g1s());
            case 13 -> lines.add("killabovelevel=" + packet.g1s());
            case 14 -> lines.add("unknown14=" + packet.g2());
            case 15 -> lines.add("material=" + packet.g2());
            case 16 -> lines.add("unknown16=" + packet.g1() + "," + packet.g2() + "," + packet.g2() + "," + packet.g1());
            case 17 -> lines.add("unknown17=" + packet.g2());
            case 18 -> lines.add("rgbatarget=" + packet.g4s());
            case 19 -> lines.add("unknown19=" + packet.g1());
            case 20 -> lines.add("rgbtransitionpercent=" + packet.g1());
            case 21 -> lines.add("alphatransitionpercent=" + packet.g1());
            case 22 -> lines.add("speedtarget=" + packet.g4s());
            case 23 -> lines.add("speedtargetpercent=" + packet.g1());
            case 24 -> lines.add("linkcolourchannels=no");

            case 25 -> {
                var count = packet.g1();
                var parts = new ArrayList<String>();

                for (var i = 0; i < count; i++) {
                    parts.add("" + packet.g2());
                }

                lines.add("globaleffectors=" + String.join(",", parts));
            }

            case 26 -> lines.add("enabled=no");
            case 27 -> lines.add("sizetarget=" + packet.g2());
            case 28 -> lines.add("sizetargetpercent=" + packet.g1());

            case 29 -> {
                if (packet.g1() == 0) {
                    lines.add("angularvelocity=" + packet.g2s());
                } else {
                    lines.add("angularvelocity=" + packet.g2s() + "," + packet.g2s());
                }
            }

            case 30 -> lines.add("unknown30=yes");
            case 31 -> lines.add("size=" + packet.g2() + "," + packet.g2());
            case 32 -> lines.add("lighting=no");
            case 33 -> lines.add("unknown33=yes");
            case 34 -> lines.add("unknown34=no");

            case 35 -> {
                if (packet.g1() == 0) {
                    lines.add("rotationspawn=" + packet.g2s());
                } else {
                    lines.add("rotationspawn=" + packet.g2s() + "," + packet.g2s() + "," + packet.g1());
                }
            }

            case 36 -> lines.add("unknown36=yes");
            case 37 -> lines.add("unknown37=yes");

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
