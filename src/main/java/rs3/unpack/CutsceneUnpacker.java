package rs3.unpack;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class CutsceneUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.CUTSCENE, id) + "]");

        unpackHeader(lines, packet);
        lines.add("");

        // Templates
        var templateCount = packet.g1();

        for (var i = 0; i < templateCount; i++) {
            lines.add("[template]");
            lines.add("origin=" + Unpacker.format(Type.COORDGRID, packet.g4s()));
            lines.add("width=" + packet.g1());
            lines.add("length=" + packet.g1());
            lines.add("level=" + packet.g1());
            lines.add("x=" + packet.g1());
            lines.add("z=" + packet.g1());
            lines.add("angle=" + packet.g1());
            lines.add("");
        }

        // Splines
        var splineCount = packet.gSmart1or2();

        for (var i = 0; i < splineCount; i++) {
            lines.add("[spline]");
            var pointCount = packet.gSmart1or2();

            for (var j = 0; j < pointCount; j++) {
                lines.add("point=" + packet.g2() + "," + packet.g2() + "," + packet.g2s() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2s() + "," + packet.g2s());
            }

            lines.add("");
        }

        // Entities
        var entityCount = packet.gSmart1or2();

        for (var i = 0; i < entityCount; i++) {
            lines.add("[entity]");

            switch (packet.g1()) {
                case 0 -> lines.add("type=" + Unpacker.format(Type.NPC, packet.gSmart2or4null()));
                case 1 -> lines.add("type=player");
            }

            lines.add("unknown=" + packet.gjstr());
            lines.add("");
        }

        // Locations
        var locationCount = packet.gSmart1or2();

        for (var i = 0; i < locationCount; i++) {
            lines.add("[location]");
            lines.add("type=" + Unpacker.format(Type.LOC, packet.gSmart2or4null()));
            lines.add("shape=" + Unpacker.format(Type.LOC_SHAPE, packet.g1()));
            lines.add("");
        }

        // Route
        var routeCount = packet.gSmart1or2();

        for (var i = 0; i < routeCount; i++) {
            lines.add("[route]");
            var waypointCount = packet.gSmart1or2();

            for (var j = 0; j < waypointCount; j++) {
                lines.add("waypoint=" + Unpacker.format(Type.MOVESPEED, packet.g1()) + "," + packet.g2() + "," + packet.g2());
            }

            lines.add("");
        }

        // Actions
        var actionCount = packet.gSmart1or2();
        lines.add("[script]");
        var prevTime = 0;

        for (var action = 0; action < actionCount; action++) {
            var opcode = packet.g1();
            var time = packet.g2();

            if (time != prevTime) {
                lines.add("delay(" + (time - prevTime) + ");");
            }

            prevTime = time;

            lines.add(switch (opcode) {
                case 0 -> "cam_move(" + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2() + ");";
                case 1 -> "cam_movealong(spline_" + packet.g2() + ",spline_" + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2() + ");";
                case 2 -> "unknown2();";
                case 3 -> "unknown3();";
                case 10 -> "entity_move(entity_" + packet.g2() + "," + getLocalCoord(packet.g4s()) + "," + packet.g1() + "," + packet.gSmart1or2s() + ");";
                case 11 -> "entity_del(entity_" + packet.g2() + ");";
                case 12 -> "entity_route(entity_" + packet.g2() + ",route_" + packet.g2() + "," + packet.g1() + ");";
                case 13 -> "entity_say(entity_" + packet.g2() + ",\"" + packet.gjstr() + "\",0x" + Integer.toHexString(packet.g4s()) + "," + packet.g2() + ");";
                case 14 -> "entity_anim(entity_" + packet.g2() + "," + Unpacker.format(Type.SEQ, packet.gSmart2or4null()) + "," + packet.g4s() + ");";

                case 15 -> {
                    var command = "entity_hitmark(entity_" + packet.g2();
                    var flags = packet.g1();

                    if ((flags & 1) != 0) {
                        command += "," + packet.g2();
                        command += "," + packet.g2();
                    } else {
                        command += ",-1";
                        command += ",-1";
                    }

                    if ((flags & 2) != 0) {
                        command += "," + packet.g2();
                        command += "," + packet.g2();
                    } else {
                        command += ",-1";
                        command += ",-1";
                    }

                    if ((flags & 4) != 0) {
                        command += "," + packet.g2();
                        command += "," + packet.g2();
                    } else {
                        command += ",-1";
                        command += ",-1";
                    }

                    yield command + ");";
                }

                case 16 -> "entity_look(entity_" + packet.g2() + "," + packet.g2() + ");";
                case 17 -> "entity_spotanim(" + Unpacker.format(Type.SPOTANIM, packet.g2()) + "," + packet.g2() + "," + packet.g1() + ",entity_" + packet.g2() + "," + packet.g1() + "," + packet.g2() + ");";

                case 20 -> "loc_create(locinstance_" + packet.g2() + "," + getLocalCoord(packet.g4s()) + "," + packet.g1() + "," + packet.g1() + ");";
                case 21 -> "loc_del(locinstance_" + packet.g2() + ");";
                case 22 -> "loc_anim(locinstance_" + packet.g2() + "," + Unpacker.format(Type.SEQ, packet.gSmart2or4null()) + ");";

                case 30 -> "sound_song(" + Unpacker.format(Type.MIDI, packet.g2()) + "," + packet.g1() + ");";
                case 31 -> "sound_unknown(" + packet.g2() + "," + packet.g1() + "," + packet.g1() + "," + packet.g1() + ");";
                case 32 -> "sound_jingle(" + Unpacker.format(Type.MIDI, packet.g2()) + "," + packet.g1() + ");";
                case 33 -> "sound_vorbis(" + Unpacker.format(Type.VORBIS, packet.g2()) + "," + packet.g1() + "," + packet.g1() + "," + packet.g1() + ");";

                case 40 -> "fade(" + packet.g2() + ",0x" + Integer.toHexString(packet.g4s()) + ");";
                case 41 -> "text_coord(" + packet.g2() + "," + packet.g2() + ",\"" + packet.gjstr() + "\",0x" + Integer.toHexString(packet.g4s()) + "," + packet.g2() + ");";
                case 42 -> "map_anim(" + Unpacker.format(Type.SPOTANIM, packet.g2()) + "," + packet.g2() + "," + packet.g1() + "," + packet.g4s() + "," + packet.g1() + ");";
                case 43 -> "unknown43();";

                case 50 -> "projanim(entity_" + packet.g2() + ",entity_" + packet.g2() + "," + Unpacker.format(Type.SPOTANIM, packet.g2()) + "," + packet.g1() + "," + packet.g1() + "," + packet.g3() + "," + packet.g2() + "," + packet.g1() + ");";
                case 51 -> "projanim(" + getLocalCoord(packet.g4s()) + ",entity_" + packet.g2() + "," + Unpacker.format(Type.SPOTANIM, packet.g2()) + "," + packet.g1() + "," + packet.g1() + "," + packet.g3() + "," + packet.g2() + "," + packet.g1() + ");";
                case 52 -> "projanim(" + getLocalCoord(packet.g4s()) + "," + getLocalCoord(packet.g4s()) + "," + Unpacker.format(Type.SPOTANIM, packet.g2()) + "," + packet.g1() + "," + packet.g1() + "," + packet.g3() + "," + packet.g2() + "," + packet.g1() + ");";
                case 53 -> "projanim(entity_" + packet.g2() + "," + getLocalCoord(packet.g4s()) + "," + Unpacker.format(Type.SPOTANIM, packet.g2()) + "," + packet.g1() + "," + packet.g1() + "," + packet.g3() + "," + packet.g2() + "," + packet.g1() + ");";

                case 60 -> "%var_" + packet.g2() + " = " + packet.g4s() + ");";
                case 61 -> "%varbit_" + packet.g2() + " = " + packet.g4s() + ");";

                case 70 -> "subtitle(\"" + packet.gjstr() + "\", " + packet.g2() + ");";

                case 255 -> "";

                default -> throw new IllegalStateException("unknown command " + opcode);
            });
        }

        return lines;
    }

    private static void unpackHeader(List<String> lines, Packet packet) {
        while (true) {
            var opcode = packet.g1();

            switch (opcode) {
                case 0 -> lines.add("unknown0=" + packet.g2() + "," + packet.g2());

                case 255 -> {
                    return;
                }

                default -> throw new IllegalStateException("unknown header opcode " + opcode);
            }
        }
    }

    public static String getLocalCoord(int coord) {
        return "localcoord_" + ((coord >> 16) + "_" + (coord & 0xffff));
    }
}
