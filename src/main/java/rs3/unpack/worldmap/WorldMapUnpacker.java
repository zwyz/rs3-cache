package rs3.unpack.worldmap;

import rs3.Unpack;
import rs3.js5.Js5ArchiveIndex;
import rs3.js5.Js5Util;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WorldMapUnpacker {

    public static void unpack(Path root) throws IOException {
        if (Unpack.MASTER_INDEX.getArchiveCount() < 24) {
            return;
        }
        Files.createDirectories(root);
        var index = new Js5ArchiveIndex(Js5Util.decompress(Unpack.PROVIDER.get(255, 23, false, 0)));
        if (Unpack.VERSION < 506) {
            unpackLegacy(index, root, "main");
        } else {
            unpack(index, root);
        }
    }

    private static void unpack(Js5ArchiveIndex index, Path root) throws IOException {
        int detailsId;
        if (Unpack.VERSION > 742) {
            detailsId = 0;
        } else {
            detailsId = index.findGroup("details");
            if (detailsId == -1) {
                throw new IllegalArgumentException("Group not found: details");
            }
        }
        var files = Js5Util.unpackGroup(index, detailsId, Unpack.PROVIDER.get(23, detailsId, false, 0));
        var lines = new ArrayList<String>();
        for (var id : files.keySet()) {
            var debugname = unpackDetails(id, lines, files.get(id));
            unpackStaticElements(index, lines, debugname);
            unpackLabels(index, lines, debugname);

            lines.add("");
        }
        Files.write(root.resolve("dump.wma"), lines);
    }

    private static void unpackStaticElements(Js5ArchiveIndex index, List<String> lines, String debugname) {
        var staticElements = index.findGroup(debugname + "_staticelements");
        if (staticElements == -1) {
            return;
        }
        var staticFiles = Js5Util.unpackGroup(index, staticElements, Unpack.PROVIDER.get(23, staticElements, false, 0));
        for (var id : staticFiles.keySet()) {
            var packet = new Packet(staticFiles.get(id));
            lines.add("element=" + Unpacker.format(Type.COORDGRID, packet.g4s()) + "," + Unpacker.format(Type.MAPELEMENT, packet.g2()) + "," + packet.g1());
        }
    }

    private static void unpackLabels(Js5ArchiveIndex index, ArrayList<String> lines, String debugname) {
        var labels = index.findGroup(debugname + "_labels");
        if (labels == -1) {
            return;
        }
        var labelFiles = Js5Util.unpackGroup(index, labels, Unpack.PROVIDER.get(23, labels, false, 0));
        for (var id : labelFiles.keySet()) {
            var packet = new Packet(labelFiles.get(id));
            if (Unpack.VERSION > 547) {
                lines.add("label=" + Unpacker.format(Type.COORDGRID, packet.g4s()) + "," + Unpacker.format(Type.MAPELEMENT, packet.g2()) + "," + packet.g1());
            } else {
                String text = packet.gjstr();
                int settings = packet.g1s();
                int x = packet.g2();
                int y = packet.g2();
                int colour = packet.g4s();
                lines.add("label=" + text + ",0x" + Integer.toHexString(settings) + "," + x + "," + y + "," + Unpacker.formatColour(colour));
            }
        }
    }

    private static void unpackLegacy(Js5ArchiveIndex index, Path root, String name) throws IOException {
        int group = index.findGroup(name);
        if (group == -1) {
            throw new IllegalArgumentException("Group not found: " + name);
        }
        int detailsId = index.findFile(group, "details.dat");
        int labelsId = index.findFile(group, "labels.dat");
        if (detailsId == -1 || labelsId == -1) {
            throw new IllegalArgumentException("Files not found: " + name);
        }
        var files = Js5Util.unpackGroup(index, group, Unpack.PROVIDER.get(23, group, false, 0));

        byte[] detailsData = files.get(detailsId);
        byte[] labelsData = files.get(labelsId);

        var details = new Packet(detailsData);
        List<String> lines = new ArrayList<>();
        lines.add("[" + name + "]");
        lines.add("origin=" + details.g2() + "," + details.g2());
        lines.add("min=" + details.g2() + "," + details.g2());
        lines.add("max=" + details.g2() + "," + details.g2());
        lines.add("");
        var labels = new Packet(labelsData);
        var labelCount = labels.g2();
        for (var i = 0; i < labelCount; i++) {
            var text = labels.gjstr();
            var x = labels.g2();
            var y = labels.g2();
            var type = labels.g1();
            lines.add("label=" + x + "," + y + "," + text + "," + type);
        }
        Files.write(root.resolve("dump.wma"), lines);
    }

    private static String unpackDetails(int id, List<String> lines, byte[] data) {
        var packet = new Packet(data);
        var debugname = packet.gjstr();
        Unpacker.setWorldMapAreaName(id, debugname);
        lines.add("[" + Unpacker.format(Type.MAPAREA, id) + "]");
        lines.add("name=" + packet.gjstr());
        if (Unpack.VERSION >= 537) {
            lines.add("origin=" + Unpacker.format(Type.COORDGRID, packet.g4s()));
        } else {
            lines.add("origin=" + packet.g2() + "," + packet.g2());
        }
        lines.add("background=" + Unpacker.formatColour(packet.g4s()));
        lines.add("listed=" + (packet.g1() == 1 ? "yes" : "no"));
        if (Unpack.VERSION >= 526) {
            int defaultZoom = packet.g1();
            lines.add("zoom=" + (defaultZoom == 255 ? "default" : defaultZoom));
        }
        if (Unpack.VERSION >= 566) {
            lines.add("buildarea=" + packet.g1());
        }

        var count = packet.g1();
        for (var i = 0; i < count; i++) {
            unpackSubarea(packet, lines);
        }
        return debugname;
    }

    private static void unpackSubarea(Packet packet, List<String> lines) {
        if (Unpack.VERSION < 537) {
            lines.add("subarea=" + packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2());
        } else if (Unpack.VERSION < 555) {
            lines.add("subarea=" + packet.g1() + "," + packet.g1() + "," +
                    packet.g2() + "," +
                    packet.g2() + "," +
                    packet.g2() + "," +
                    packet.g2() + "," +
                    packet.g2() + "," +
                    packet.g2() + "," +
                    packet.g2() + "," +
                    packet.g2());
        } else {
            lines.add("subarea=" + packet.g1() + "," +
                    packet.g2() + "," +
                    packet.g2() + "," +
                    packet.g2() + "," +
                    packet.g2() + "," +
                    packet.g2() + "," +
                    packet.g2() + "," +
                    packet.g2() + "," +
                    packet.g2());
        }
    }
}
