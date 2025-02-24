package rs3.unpack.worldmap;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class MapAreaUnpacker {
    public static List<String> unpack(int id, byte[] file) {
        var packet = new Packet(file);
        var lines = new ArrayList<String>();

        var debugname = packet.gjstr();
        Unpacker.setWorldMapAreaName(id, debugname);
        lines.add("[" + Unpacker.format(Type.MAPAREA, id) + "]");

        lines.add("name=" + packet.gjstr());
        lines.add("origin=" + Unpacker.format(Type.COORDGRID, packet.g4s()));
        lines.add("background=" + Unpacker.formatColour(packet.g4s()));
        lines.add("unknown5=" + (packet.g1() == 1 ? "yes" : "no"));
        lines.add("zoom=" + packet.g1());
        lines.add("buildarea=" + packet.g1());

        var count = packet.g1();

        for (var i = 0; i < count; i++) {
            lines.add("subarea=" + packet.g1() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2());
        }

        return lines;
    }
}
