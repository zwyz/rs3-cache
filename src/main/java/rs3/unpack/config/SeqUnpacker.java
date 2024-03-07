package rs3.unpack.config;

import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class SeqUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);

        lines.add("[" + Unpacker.format(Type.SEQ, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> {
                if (Unpack.VERSION < 400) {
                    var count = packet.g1();

                    for (var i = 0; i < count; i++) {
                        lines.add("frame" + i + "=f" + packet.g2());
                        lines.add("iframe" + i + "=" + packet.g2());
                        lines.add("delay" + i + "=" + packet.g2());
                    }
                } else {
                    var count = packet.g2();
                    var delay = new int[count];
                    var frame = new int[count];
                    var anim = new int[count];

                    for (var i = 0; i < count; i++) {
                        delay[i] = packet.g2();
                    }

                    for (var i = 0; i < count; i++) {
                        frame[i] = packet.g2();
                    }

                    for (var i = 0; i < count; i++) {
                        anim[i] = packet.g2();
                    }

                    for (var i = 0; i < count; i++) {
                        lines.add("frame" + (i + 1) + "=anim_" + anim[i] + "_f" + (frame[i] + 1));
                        lines.add("delay" + (i + 1) + "=" + delay[i]);
                    }
                }
            }

            case 2 -> lines.add("loopframes=" + packet.g2());

            case 3 -> {
                var count = Unpack.VERSION < 600 ? packet.g1() : packet.gSmart1or2();
                var result = new ArrayList<String>();

                for (var i = 0; i < count; i++) {
                    result.add("label_" + (Unpack.VERSION < 600 ? packet.g1() : packet.gSmart1or2()));
                }

                lines.add("walkmerge=" + String.join(",", result));
            }

            case 4 -> lines.add("stretches=yes");
            case 5 -> lines.add("priority=" + packet.g1());
            case 6 -> lines.add("lefthand=" + packet.g2null());
            case 7 -> lines.add("righthand=" + packet.g2null());
            case 8 -> lines.add("loopcount=" + packet.g1());
            case 9 -> lines.add("preanim_move=" + Unpacker.formatPreAnimMove(packet.g1()));
            case 10 -> lines.add("postanim_move=" + Unpacker.formatPostAnimMove(packet.g1()));
            case 11 -> lines.add("replacemode=" + Unpacker.formatReplaceMode(packet.g1()));

            case 12 -> {
                var count = packet.g1();
                var frame = new int[count];
                var anim = new int[count];

                for (var i = 0; i < count; i++) {
                    frame[i] = packet.g2();
                }

                for (var i = 0; i < count; i++) {
                    anim[i] += packet.g2();
                }

                for (var i = 0; i < count; i++) {
                    lines.add("iframe" + (i + 1) + "=anim_" + anim[i] + "_f" + (frame[i] + 1));
                }
            }

            case 112 -> {
                var count = packet.g2();
                var frame = new int[count];
                var anim = new int[count];

                for (var i = 0; i < count; i++) {
                    frame[i] = packet.g2();
                }

                for (var i = 0; i < count; i++) {
                    anim[i] += packet.g2();
                }

                for (var i = 0; i < count; i++) {
                    lines.add("iframe" + (i + 1) + "=anim_" + anim[i] + "_f" + (frame[i] + 1));
                }
            }

            case 13 -> {
                if (Unpack.VERSION < 500) {
                    var count = packet.g1();

                    for (var i = 0; i < count; i++) {
                        var value = packet.g3();

                        if (value != 0) {
                            var type = value >> 8;
                            var loops = value >> 4 & 7;
                            var range = value & 15;
                            lines.add("sound" + i + "=" + Unpacker.format(Type.SYNTH, type) + "," + loops + "," + range);
                        }
                    }
                } else {
                    var count = packet.g2();

                    for (var i = 0; i < count; i++) {
                        var count2 = packet.g1();

                        if (count2 > 0) {
                            var value = packet.g3();
                            var type = value >> 8;
                            var loops = value >> 4 & 7;
                            var range = value & 15;
                            var line = "sound" + i + "=" + Unpacker.format(Type.SYNTH, type) + "," + loops + "," + range;

                            for (var j = 1; j < count2; ++j) {
                                line += "," + packet.g2();
                            }

                            lines.add(line);
                        }
                    }
                }
            }

            case 14 -> lines.add("unknown14=yes");
            case 15 -> lines.add("unknown15=yes");
            case 16 -> lines.add("unknown16=yes");
            case 18 -> lines.add("unknown18=yes");
            case 19 -> lines.add("unknown19=" + packet.g1() + "," + packet.g1());
            case 119 -> lines.add("unknown19=" + packet.g2() + "," + packet.g1());
            case 20 -> lines.add("unknown20=" + packet.g1() + "," + packet.g2() + "," + packet.g2());
            case 120 -> lines.add("unknown20=" + packet.g2() + "," + packet.g2() + "," + packet.g2());
            case 22 -> lines.add("unknown22=" + packet.g1());
            case 23 -> lines.add("unknown23=" + packet.g2());
            case 24 -> lines.add("group=" + Unpacker.format(Type.SEQGROUP, packet.g2()));
            case 25 -> lines.add("keyframeset=" + packet.g2());
            case 26 -> lines.add("keyframerange=" + packet.g2() + "," + packet.g2());

            case 249 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    if (packet.g1() == 1) {
                        lines.add("param=" + Unpacker.format(Type.PARAM, packet.g3()) + "," + packet.gjstr());
                    } else {
                        var param = packet.g3();
                        lines.add("param=" + Unpacker.format(Type.PARAM, param) + "," + Unpacker.format(Unpacker.getParamType(param), packet.g4s()));
                    }
                }
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
