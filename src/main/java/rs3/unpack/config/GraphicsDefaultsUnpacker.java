package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class GraphicsDefaultsUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[graphicsdefaults_" + id + "]");

        var hitmarkcount = 4;

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> {
                for (var i = 0; i < hitmarkcount; i++) {
                    lines.add("hitmark" + i + "pos=" + packet.g2s() + "," + packet.g2s());
                }
            }

            case 2 -> lines.add("performancemetricsmodel=" + Unpacker.format(Type.MODEL, packet.gSmart2or4null()));

            case 3 -> {
                hitmarkcount = packet.g1();
                lines.add("hitmarkcount=" + hitmarkcount);
            }

            case 4 -> lines.add("unknown4=no");
            case 5 -> lines.add("titleinterface=" + Unpacker.format(Type.INTERFACE, packet.g3()));
            case 6 -> lines.add("lobbyinterface=" + Unpacker.format(Type.INTERFACE, packet.g3()));

            case 7 -> {
                for (var i = 0; i < 10; i++) {
                    for (var j = 0; j < 4; j++) {
                        lines.add("playerrecol" + i + "s" + j + "=" + packet.g2null());

                        var count = packet.g2();
                        var line = new ArrayList<String>();

                        for (var k = 0; k < count; k++) {
                            line.add(String.valueOf(packet.g2null()));
                        }

                        lines.add("playerrecol" + i + "d" + j + "=" + String.join(",", line));
                    }
                }
            }

            case 8 -> lines.add("npcchatline=no");
            case 9 -> lines.add("npcchatlineduration=" + packet.g1());
            case 10 -> lines.add("playerchatline=no");
            case 11 -> lines.add("playerchatlineduration=" + packet.g1());
            case 12 -> lines.add("initialsize=" + packet.g2() + "," + packet.g2());
            case 13 -> lines.add("headbarcount=" + packet.g1());
            case 14 -> lines.add("headbarupdatecount=" + packet.g1());
            case 15 -> lines.add("entityoverlayoffset=" + packet.g1());
            case 16 -> lines.add("somethingcamera=yes");
            case 17 -> lines.add("objnumcolour=0x" + Integer.toHexString(packet.g4s()));
            case 18 -> lines.add("objnumcolourk=0x" + Integer.toHexString(packet.g4s()));
            case 19 -> lines.add("objnumcolourm=0x" + Integer.toHexString(packet.g4s()));
            case 20 -> lines.add("entityshadow=" + packet.g2() + "," + packet.g1());
            case 21 -> lines.add("minimapscale=" + packet.g1());

            case 22 -> {
                var p11full = Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null());
                var p12full = Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null());
                var b12full = Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null());
                var hintheadicon = Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null());
                var hintmapmarker = Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null());
                var mapflag = Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null());
                var mapflagoriginx = packet.g1s();
                var mapflagoriginy = packet.g1s();
                var cross = Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null());
                var mapdot = Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null());
                var nameicon = Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null());
                var floorshadow = Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null());
                var compass = Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null());
                var otherlevel = Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null());
                var mapedge = Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null());

                lines.add("sprites=" + p11full + "," + p12full + "," + b12full + "," + hintheadicon + "," + hintmapmarker + "," + mapflag + "," + mapflagoriginx + "," + mapflagoriginy + "," + cross + "," + mapdot + "," + nameicon + "," + floorshadow + "," + compass + "," + otherlevel + "," + mapedge);
            }

            case 23 -> {
                for (var i = 0; i < 10; i++) {
                    for (var j = 0; j < 4; j++) {
                        lines.add("playerretex" + i + "s" + j + "=" + packet.g2null());

                        var count = packet.g2();
                        var line = new ArrayList<String>();

                        for (var k = 0; k < count; k++) {
                            line.add(String.valueOf(packet.g2null()));
                        }

                        lines.add("playerretex" + i + "d" + j + "=" + String.join(",", line));
                    }
                }
            }

            case 24 -> lines.add("unknown24=" + packet.g4s());
            case 25 -> lines.add("unknown25=" + packet.gSmart2or4null() + "," + packet.gSmart2or4null() + "," + packet.gSmart2or4null() + "," + packet.gSmart2or4null() + "," + packet.gSmart2or4null() + "," + packet.gSmart2or4null());
            case 26 -> lines.add("objnumcolourb=0x" + Integer.toHexString(packet.g4s()));
            case 27 -> lines.add("objnumcolourt=0x" + Integer.toHexString(packet.g4s()));
            case 28 -> lines.add("objnumcolourq=0x" + Integer.toHexString(packet.g4s()));

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
