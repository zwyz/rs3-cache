package rs3.unpack.map;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapSquare {
    public Environment environment;
    public List<PointLight> lights;
    public List<WaterPatch> water;
    private Tile[][][] tiles;

    public MapSquare(Map<Integer, byte[]> files) {
        if (files.containsKey(6)) {
            environment = new Environment(new Packet(files.get(6)));
        }

        if (files.containsKey(7)) {
            decodeLights(new Packet(files.get(7)));
        }

        if (files.containsKey(8)) {
            decodeWater(new Packet(files.get(8)));
        }
    }

    private void decodeFloor(Packet packet) {
        tiles = new Tile[4][64][64];

        while (packet.arr.length - packet.pos > 0) {
            var level = packet.g1();

            for (var x = -1; x < 65; x++) {
                for (var z = -1; z < 65; z++) {
                    var tile = new Tile(packet);

                    if (x >= 0 && z >= 0 && x < 64 && z < 64) {
                        tiles[level][x][z] = tile;
                    }
                }
            }
        }
    }

    private void decodeWater(Packet packet) {
        var count = packet.g1();
        water = new ArrayList<>(count);

        for (var i = 0; i < count; i++) {
            water.add(new WaterPatch(packet));
        }
    }

    private void decodeLights(Packet packet) {
        var count = packet.g1();
        lights = new ArrayList<>(count);

        for (var i = 0; i < count; i++) {
            lights.add(new PointLight(packet));
        }
    }
}
