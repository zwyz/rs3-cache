package rs3.unpack.defaults;

import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GraphicsDefaultsUnpacker {
    public static List<String> unpack(byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        var maxhitmarks = 4;

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> {
                for (var i = 0; i < maxhitmarks; i++) {
                    lines.add("hitmarkoffset" + i + "=" + packet.g2s() + "," + packet.g2s());
                }
            }

            case 2 -> lines.add("performancemetricsmodel=" + Unpacker.format(Type.MODEL, Unpack.VERSION < 681 ? packet.g2null() : packet.gSmart2or4null()));

            case 3 -> { // GetMaxHitmarks
                maxhitmarks = packet.g1();
                lines.add("maxhitmarks=" + maxhitmarks);
            }

            case 4 -> lines.add("showemptyminimenu=no");
            case 5 -> lines.add("titleinterface=" + Unpacker.format(Type.INTERFACE, packet.g3()));
            case 6 -> lines.add("lobbyinterface=" + Unpacker.format(Type.INTERFACE, packet.g3()));

            case 7 -> { // 216 GetPlayerRecolSource GetPlayerRecolDest
                for (var i = 0; i < 10; i++) {
                    for (var j = 0; j < 4; j++) {
                        var source = packet.g2null();

                        var count = packet.g2();
                        var line = new ArrayList<String>();

                        for (var k = 0; k < count; k++) {
                            line.add(String.valueOf(packet.g2null()));
                        }

                        if (source != -1 || !line.isEmpty()) {
                            lines.add("playerrecol" + i + "s" + j + "=" + source);
                            lines.add("playerrecol" + i + "d" + j + "=" + String.join(",", line));
                        }
                    }
                }
            }

            case 8 -> lines.add("npcchat=no");
            case 9 -> lines.add("npcchattimeout=" + packet.g1()); // 216 GetNpcChatTimeout
            case 10 -> lines.add("playerchat=no");
            case 11 -> lines.add("playerchattimeout=" + packet.g1());
            case 12 -> lines.add("initialwindowsize=" + packet.g2() + "," + packet.g2());
            case 13 -> lines.add("maxheadbarssimultaneous=" + packet.g1()); // 216 GetMaxHeadbarsSimultaneous
            case 14 -> lines.add("maxheadbarsqueued=" + packet.g1()); // 216 GetMaxHeadbarsQueued
            case 15 -> lines.add("entityoverlayoffset=" + packet.g1());
            case 16 -> lines.add("cam2enabled=yes");
            case 17 -> lines.add("objstackcolourunits=" + Unpacker.formatColour(packet.g4s())); // 216 GetObjStackColourUnits
            case 18 -> lines.add("objstackcolourthousands=" + Unpacker.formatColour(packet.g4s())); // 216 GetObjStackColourThousands
            case 19 -> lines.add("objstackcolourmillions=" + Unpacker.formatColour(packet.g4s())); // 216 GetObjStackColourMillions
            case 20 -> lines.add("spotshadow=" + Unpacker.format(Type.MATERIAL, packet.g2()) + "," + packet.g1());
            case 21 -> lines.add("minimapscale=" + packet.g1());

            case 22 -> {
                var p11full = Unpacker.format(Type.FONTMETRICS, packet.gSmart2or4null());
                var p12full = Unpacker.format(Type.FONTMETRICS, packet.gSmart2or4null());
                var b12full = Unpacker.format(Type.FONTMETRICS, packet.gSmart2or4null());
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

            case 23 -> { // 216 GetPlayerRematSource GetPlayerRematDest
                for (var i = 0; i < 10; i++) {
                    for (var j = 0; j < 4; j++) {
                        var source = Unpacker.format(Type.MATERIAL, packet.g2null());

                        var count = packet.g2();
                        var line = new ArrayList<String>();

                        for (var k = 0; k < count; k++) {
                            line.add(Unpacker.format(Type.MATERIAL, packet.g2null()));
                        }

                        if (!Objects.equals(source, "null") || !line.isEmpty()) {
                            lines.add("playerretex" + i + "s" + j + "=" + source);
                            lines.add("playerretex" + i + "d" + j + "=" + String.join(",", line));
                        }
                    }
                }
            }

            case 24 -> lines.add("consolefont=" + Unpacker.format(Type.FONTMETRICS, packet.g4s()));
            case 25 -> lines.add("defaultplayermodel=" + Unpacker.format(Type.BAS, packet.gSmart2or4null()) + "," + Unpacker.format(Type.IDKIT, packet.gSmart2or4null()) + "," + Unpacker.format(Type.IDKIT, packet.gSmart2or4null()) + "," + Unpacker.format(Type.IDKIT, packet.gSmart2or4null()) + "," + Unpacker.format(Type.IDKIT, packet.gSmart2or4null()) + "," + Unpacker.format(Type.IDKIT, packet.gSmart2or4null()));
            case 26 -> lines.add("objstackcolourbillions=" + Unpacker.formatColour(packet.g4s())); // 216 GetObjStackColourBillions
            case 27 -> lines.add("objstackcolourtrillions=" + Unpacker.formatColour(packet.g4s())); // 216 GetObjStackColourTrillions
            case 28 -> lines.add("objstackcolourquadrillions=" + Unpacker.formatColour(packet.g4s())); // 216 GetObjStackColourQuadrillions
            case 29 -> lines.add("wiki=" + Unpacker.format(Type.GRAPHIC, packet.g4s()) + "," + Unpacker.format(Type.CURSOR, packet.g4s())); // icon and cursor used for "Wiki" menu action

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
