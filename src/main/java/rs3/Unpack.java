package rs3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rs3.js5.Js5ArchiveIndex;
import rs3.js5.Js5Util;
import rs3.unpack.*;
import rs3.unpack.config.*;
import rs3.unpack.cutscene2d.Cutscene2D;
import rs3.unpack.map.MapSquare;
import rs3.unpack.script.ScriptUnpacker;
import rs3.unpack.uianim.Anim;
import rs3.unpack.uianim.AnimCurve;
import rs3.unpack.unknown62.AnimatorController;
import rs3.unpack.vfx.VFXUnpacker;
import rs3.unpack.worldmap.MapAreaUnpacker;
import rs3.util.Packet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

// todo: clean this up
public class Unpack {
    public static final int VERSION = 10000;
    private static final Path BASE_PATH = Path.of(System.getProperty("user.home") + "/.rscache/" + (VERSION == 10000 ? "rs3" : VERSION));
    public static final Gson GSON = new GsonBuilder().serializeSpecialFloatingPointValues().create();
    public static final Gson GSON_PRETTY = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();

    public static void main(String[] args) throws IOException {
        var root = Path.of("unpacked");

        // load names
        loadGroupNamesScriptTrigger(12, Unpacker.SCRIPT_NAMES);
        loadGroupNames(Path.of("data/names/scripts.txt"), 12, Unpacker.SCRIPT_NAMES);
        loadGroupNames(Path.of("data/names/graphics.txt"), 8, Unpacker.GRAPHIC_NAMES);

        // unpack
        unpackConfig(root.resolve("scripts"));
        unpackConfigGroup(23, 0, MapAreaUnpacker::unpack, root.resolve("scripts/dump.wma")); // worldmapdata details
        unpackDefaults(root.resolve("defaults"));
        unpackScripts(12, root.resolve("scripts/dump.cs2"));
        unpackConfigArchive(3, 16, InterfaceUnpacker::unpack, root.resolve("scripts/dump.if3"));
        unpackConfigArchive(60, 0, StylesheetUnpacker::unpack, root.resolve("scripts/dump.stylesheet"));
        unpackConfigArchive(26, 0, MaterialUnpacker::unpack, root.resolve("scripts/dump.material"));
//        iterateArchive(8, SpriteUnpacker::unpack);
//        unpackArchiveTransformed(47, b -> GSON.toJson(new Model(new Packet(b))), root.resolve("model"), ".json");
//        iterateArchive(54, TextureUnpacker::unpack);
        unpackArchive(10, root.resolve("binary"), ".dat");
        unpackArchive(59, root.resolve("ttf"), ".ttf");
        unpackArchiveTransformed(61, VFXUnpacker::unpack, root.resolve("vfx"), ".json");
        unpackArchiveTransformed(62, b -> GSON_PRETTY.toJson(AnimatorController.decode(new Packet(b))), root.resolve("animators"), ".json");
        unpackGroupTransformed(65, 0, b -> GSON_PRETTY.toJson(new AnimCurve(new Packet(b))), root.resolve("uianimcurve"), ".json");
        unpackGroupTransformed(65, 1, b -> GSON_PRETTY.toJson(new Anim(new Packet(b))), root.resolve("uianim"), ".json");
        unpackArchiveTransformed(66, b -> GSON_PRETTY.toJson(new Cutscene2D(new Packet(b))), root.resolve("cutscene2d"), ".json");
        unpackMaps(root);
        unpackWorldAreaMap(root);
    }

    private static void loadGroupNames(Path path, int archive, Map<Integer, String> names) throws IOException {
        var unhash = new HashMap<Integer, String>();
        generateNames(path, unhash);
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));

        for (var group : archiveIndex.groupId) {
            var hash = archiveIndex.groupNameHash[group];

            if (unhash.containsKey(hash)) {
                names.put(group, unhash.get(hash));
            }
        }
    }

    private static void loadGroupNamesScriptTrigger(int archive, Map<Integer, String> names) throws IOException {
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));
        var archiveIndexConfig = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + 2 + ".dat"))));
        var archiveIndexCutscene = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + 35 + ".dat"))));
        var archiveIndexInterface = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + 3 + ".dat"))));
        var maxMapElement = archiveIndexConfig.groupMaxFileId[36];
        var maxCutscene = archiveIndexCutscene.groupArraySize;
        var maxInterface = archiveIndexInterface.groupArraySize;
        var maxCategory = 6000;

        for (var group : archiveIndex.groupId) {
            var hash = archiveIndex.groupNameHash[group];

            if (ScriptTrigger.isValidID(hash & 0x3ff)) {
                var trigger = ScriptTrigger.byID(hash & 0x3ff);
                var subject = hash >>> 10;

                if (trigger == ScriptTrigger.PROC || trigger == ScriptTrigger.CLIENTSCRIPT) {
                    continue;
                }

                if (subject == 0xffff) {
                    names.put(group, "[" + trigger.name().toLowerCase(Locale.ROOT) + ",_]");
                } else if (trigger.type != null) {
                    if (subject > 0x10000 && trigger.type == Type.MAPELEMENT) {
                        var category = subject - 0x10000;

                        if (category < maxCategory) {
                            names.put(group, "[" + trigger.name().toLowerCase(Locale.ROOT) + "," + Unpacker.format(Type.CATEGORY, category) + "]");
                        }
                    } else {
                        if (trigger.type == Type.MAPELEMENT && subject < maxMapElement) {
                            names.put(group, "[" + trigger.name().toLowerCase(Locale.ROOT) + "," + Unpacker.format(trigger.type, subject) + "]");
                        } else if (trigger.type == Type.CUTSCENE && subject < maxCutscene) {
                            names.put(group, "[" + trigger.name().toLowerCase(Locale.ROOT) + "," + Unpacker.format(trigger.type, subject) + "]");
                        } else if (trigger.type == Type.INTERFACE && subject < maxInterface) {
                            names.put(group, "[" + trigger.name().toLowerCase(Locale.ROOT) + "," + Unpacker.format(trigger.type, subject) + "]");
                        } else if (trigger == ScriptTrigger.TWITCH_EVENT && subject < 4) {
                            names.put(group, "[" + trigger.name().toLowerCase(Locale.ROOT) + "," + Unpacker.format(trigger.type, subject) + "]");
                        } else if (trigger == ScriptTrigger.MINIMENU_EVENT && subject < 4) {
                            names.put(group, "[" + trigger.name().toLowerCase(Locale.ROOT) + "," + Unpacker.format(trigger.type, subject) + "]");
                        }
                    }
                }
            }
        }
    }

    private static void generateNames(Path path, HashMap<Integer, String> names) throws IOException {
        for (var name : Files.readAllLines(path)) {
            generateNames(name, names);
        }
    }

    private static void generateNames(String name, Map<Integer, String> map) {
        if (name.indexOf('#') != -1) {
            var index = name.indexOf('#');
            var a = name.substring(0, index);
            var b = name.substring(index + 1);

            for (var i = 0; i < 500; i++) {
                generateNames(a + i + b, map);
            }
        } else {
            map.put(name.hashCode(), name);
        }
    }

    private static void unpackWorldAreaMap(Path root) throws IOException {
        var width = 128 * 8;
        var height = 256 * 8;
        var image = new int[width * height];

        iterateGroupFiles(23, 3, (file, data) -> {
            var squareX = (file >> 0) & 0x7f;
            var squareZ = (file >> 7) & 0xff;
            var colors = decodeWorldMapColor(data);

            for (var zoneX = 0; zoneX < 8; zoneX++) {
                for (var zoneZ = 0; zoneZ < 8; zoneZ++) {
                    var x = 8 * squareX + zoneX;
                    var z = 8 * squareZ + zoneZ;
                    image[width * (height - 1 - z) + x] = colors[8 * zoneX + zoneZ];
                }
            }
        });

        var rgbData = new DataBufferInt(image, image.length);
        var raster = Raster.createPackedRaster(rgbData, width, height, width, new int[]{0xff0000, 0x00ff00, 0x0000ff}, null);
        var colorModel = new DirectColorModel(24, 0xff0000, 0x00ff00, 0x0000ff);
        ImageIO.write(new BufferedImage(colorModel, raster, false, null), "png", root.resolve("areas.png").toFile());
    }

    private static void unpackScripts(int archive, Path path) throws IOException {
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));

            for (var file : files.keySet()) {
                ScriptUnpacker.load(group, files.get(file));
            }
        }

        ScriptUnpacker.decompile();

        var lines = new ArrayList<String>();
        for (var group : archiveIndex.groupId) {
            lines.addAll(ScriptUnpacker.unpack(group));
            lines.add("");
        }

        Files.write(path, lines);
    }

    private static void unpackMaps(Path root) throws IOException {
        var maps = root.resolve("maps");
        Files.createDirectories(maps);

        iterateArchiveGroups(5, (group, files) -> {
            var x = group & 0b1111111;
            var z = group >> 7;

            try {
                Files.writeString(maps.resolve(x + "_" + z + ".json"), GSON.toJson(new MapSquare(files)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static int[] decodeWorldMapColor(byte[] data) {
        var result = new int[64];
        var packet = new Packet(data);

        var index = 0;
        var target = 0;

        while (target < 64) {
            var value = packet.g3();

            if (packet.pos >= packet.arr.length) {
                target = 64; // fill remaining with `value`
            } else {
                target += packet.g1(); // fill n with `value`
            }

            while (index < target) {
                result[index++] = value;
            }
        }

        return result;
    }

    private static void unpackConfig(Path path) throws IOException {
        Files.createDirectories(path);

        // things stuff depends on
        unpackConfigGroup(2, 60, (id, data) -> VarUnpacker.unpack(VarDomain.PLAYER, id, data), path.resolve("dump.varp"));
        unpackConfigGroup(2, 61, (id, data) -> VarUnpacker.unpack(VarDomain.NPC, id, data), path.resolve("dump.varn"));
        unpackConfigGroup(2, 62, (id, data) -> VarUnpacker.unpack(VarDomain.CLIENT, id, data), path.resolve("dump.varc"));
        unpackConfigGroup(2, 63, (id, data) -> VarUnpacker.unpack(VarDomain.WORLD, id, data), path.resolve("dump.varworld")); // client ignores
        unpackConfigGroup(2, 64, (id, data) -> VarUnpacker.unpack(VarDomain.REGION, id, data), path.resolve("dump.varregion")); // client ignores
        unpackConfigGroup(2, 65, (id, data) -> VarUnpacker.unpack(VarDomain.OBJECT, id, data), path.resolve("dump.varobject"));
        unpackConfigGroup(2, 66, (id, data) -> VarUnpacker.unpack(VarDomain.CLAN, id, data), path.resolve("dump.varclan"));
        unpackConfigGroup(2, 67, (id, data) -> VarUnpacker.unpack(VarDomain.CLAN_SETTING, id, data), path.resolve("dump.varclansetting"));
        unpackConfigGroup(2, 68, (id, data) -> VarUnpacker.unpack(VarDomain.CONTROLLER, id, data), path.resolve("dump.varcontroller")); // client ignores
        unpackConfigGroup(2, 75, (id, data) -> VarUnpacker.unpack(VarDomain.GLOBAL, id, data), path.resolve("dump.varglobal")); // client ignores
        unpackConfigGroup(2, 80, (id, data) -> VarUnpacker.unpack(VarDomain.PLAYER_GROUP, id, data), path.resolve("dump.varplayergroup"));
        unpackConfigGroup(2, 69, VarBitUnpacker::unpack, path.resolve("dump.varbit"));
        unpackConfigGroup(2, 11, ParamUnpacker::unpack, path.resolve("dump.param"));

        // regular configs
        unpackConfigGroup(2, 1, FloorUnderlayUnpacker::unpack, path.resolve("dump.flu"));
        unpackConfigGroup(2, 2, HuntUnpacker::unpack, path.resolve("dump.hunt")); // client ignores
        unpackConfigGroup(2, 3, IDKUnpacker::unpack, path.resolve("dump.idk"));
        unpackConfigGroup(2, 4, FloorOverlayUnpacker::unpack, path.resolve("dump.flo"));
        unpackConfigGroup(2, 5, InvUnpacker::unpack, path.resolve("dump.inv"));
        unpackConfigArchive(16, 8, LocUnpacker::unpack, path.resolve("dump.loc")); // 6
        unpackConfigGroup(2, 7, MesAnimUnpacker::unpack, path.resolve("dump.mesanim")); // client ignores
        unpackConfigArchive(17, 8, EnumUnpacker::unpack, path.resolve("dump.enum")); // 8
        unpackConfigArchive(18, 7, NPCUnpacker::unpack, path.resolve("dump.npc")); // 9
        unpackConfigArchive(19, 8, ObjUnpacker::unpack, path.resolve("dump.obj")); // 10
        unpackConfigArchive(20, 7, SeqUnpacker::unpack, path.resolve("dump.seq")); // 12
        unpackConfigArchive(21, 8, EffectAnimUnpacker::unpack, path.resolve("dump.spot")); // 13
        unpackConfigGroup(2, 18, AreaUnpacker::unpack, path.resolve("dump.area")); // client ignores
        unpackConfigArchive(22, 5, StructUnpacker::unpack, path.resolve("dump.struct")); // 26
        unpackConfigGroup(2, 29, SkyBoxUnpacker::unpack, path.resolve("dump.skybox"));
        unpackConfigGroup(2, 31, LightUnpacker::unpack, path.resolve("dump.light"));
        unpackConfigGroup(2, 32, BASUnpacker::unpack, path.resolve("dump.bas"));
        unpackConfigGroup(2, 33, CursorUnpacker::unpack, path.resolve("dump.cursor"));
        unpackConfigGroup(2, 34, MSIUnpacker::unpack, path.resolve("dump.msi"));
        unpackConfigGroup(2, 35, QuestUnpacker::unpack, path.resolve("dump.quest"));
        unpackConfigGroup(2, 36, MapElementUnpacker::unpack, path.resolve("dump.mel"));
        unpackConfigGroup(2, 40, DBTableUnpacker::unpack, path.resolve("dump.dbtable")); // todo: use dbtableindex
        unpackConfigGroup(2, 41, DBRowUnpacker::unpack, path.resolve("dump.dbrow"));
        unpackConfigGroup(2, 42, ControllerUnpacker::unpack, path.resolve("dump.controller")); // client ignores
        unpackConfigGroup(2, 46, HitmarkUnpacker::unpack, path.resolve("dump.hitmark"));
        unpackConfigGroup(2, 48, ItemCodeUnpacker::unpack, path.resolve("dump.itemcode")); // client ignores
        unpackConfigGroup(2, 49, CategoryUnpacker::unpack, path.resolve("dump.category")); // client ignores
        unpackConfigGroup(2, 70, GameLogEventUnpacker::unpack, path.resolve("dump.gamelogevent")); // client ignores
        unpackConfigGroup(2, 72, HeadbarUnpacker::unpack, path.resolve("dump.headbar"));
        unpackConfigGroup(2, 73, Config73Unpacker::unpack, path.resolve("dump.config73"));
        unpackConfigGroup(2, 76, WaterUnpacker::unpack, path.resolve("dump.water"));
        unpackConfigGroup(2, 77, SeqGroupUnpacker::unpack, path.resolve("dump.seqgroup"));
        unpackConfigGroup(2, 83, WorldAreaUnpacker::unpack, path.resolve("dump.worldarea"));
        unpackConfigArchive(57, 7, AchievementUnpacker::unpack, path.resolve("dump.achievement")); // 85
        unpackConfigGroup(27, 0, ParticleEmitterUnpacker::unpack, path.resolve("dump.particleemitter"));
        unpackConfigGroup(27, 1, ParticleEffectorUnpacker::unpack, path.resolve("dump.particleeffector"));
        unpackConfigGroup(29, 0, BillboardUnpacker::unpack, path.resolve("dump.billboard"));
        unpackConfigGroup(24, 0, QuickChatCatUnpacker::unpack, path.resolve("dump.quickchatcat"));
        unpackConfigGroup(24, 1, QuickChatPhraseUnpacker::unpack, path.resolve("dump.quickchatphrase"));
    }

    private static void unpackDefaults(Path path) throws IOException {
        Files.createDirectories(path);
        unpackDefaults(28, 3, GraphicsDefaultsUnpacker::unpack, path.resolve("graphics.defaults"));
        unpackDefaults(28, 4, AudioDefaultsUnpacker::unpack, path.resolve("audio.defaults"));
        unpackDefaults(28, 6, WearPosDefaultsUnpacker::unpack, path.resolve("wearpos.defaults"));
        unpackDefaults(28, 10, WorldMapDefaultsUnpacker::unpack, path.resolve("worldmap.defaults"));
        unpackDefaults(28, 12, TitleDefaultsUnpacker::unpack, path.resolve("title.defaults"));
    }

    private static void unpackArchive(int archive, Path path, String extension) throws IOException {
        Files.createDirectories(path);
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));

            if (files.size() == 1 && files.containsKey(0)) {
                Files.write(path.resolve(group + extension), files.get(0));
            } else {
                var groupDirectory = path.resolve(String.valueOf(group));
                Files.createDirectories(groupDirectory);

                for (var file : files.keySet()) {
                    Files.write(groupDirectory.resolve(file + extension), files.get(file));
                }
            }
        }
    }

    private static void unpackArchiveTransformed(int archive, Function<byte[], String> unpack, Path path, String extension) throws IOException {
        Files.createDirectories(path);
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));

            if (files.size() == 1 && files.containsKey(0)) {
                Files.writeString(path.resolve(group + extension), unpack.apply(files.get(0)));
            } else {
                var groupDirectory = path.resolve(String.valueOf(group));
                Files.createDirectories(groupDirectory);

                for (var file : files.keySet()) {
                    Files.writeString(groupDirectory.resolve(file + extension), unpack.apply(files.get(file)));
                }
            }
        }
    }

    private static void iterateArchiveGroups(int archive, BiConsumer<Integer, Map<Integer, byte[]>> unpack) throws IOException {
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));
            unpack.accept(group, files);
        }
    }

    private static void unpackGroup(int archive, int group, Path path, String extension) throws IOException {
        Files.createDirectories(path);
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));
        var files = Js5Util.unpackGroup(archiveIndex, group, Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));

        for (var file : files.keySet()) {
            Files.write(path.resolve(file + extension), files.get(file));
        }
    }

    private static void unpackGroupTransformed(int archive, int group, Function<byte[], String> unpack, Path path, String extension) throws IOException {
        Files.createDirectories(path);
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));
        var files = Js5Util.unpackGroup(archiveIndex, group, Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));

        for (var file : files.keySet()) {
            Files.writeString(path.resolve(file + extension), unpack.apply(files.get(file)));
        }
    }

    private static void iterateArchive(int archive, BiConsumer<Integer, byte[]> unpack) throws IOException {
        var lines = new ArrayList<String>();
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));

            if (files.size() == 1 && files.containsKey(0)) {
                unpack.accept(group, files.get(0));
            } else {
                throw new IllegalStateException();
            }
        }
    }

    private static void iterateGroup(int archive, BiConsumer<Integer, byte[]> unpack) throws IOException {
        var lines = new ArrayList<String>();
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));

            if (files.size() == 1 && files.containsKey(0)) {
                unpack.accept(group, files.get(0));
            } else {
                throw new IllegalStateException();
            }
        }
    }

    private static void iterateGroupFiles(int archive, int group, BiConsumer<Integer, byte[]> unpack) throws IOException {
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));

        var files = Js5Util.unpackGroup(archiveIndex, group, Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));

        for (var file : files.keySet()) {
            unpack.accept(file, files.get(file));
        }
    }

    private static void unpackConfigGroup(int archive, int group, BiFunction<Integer, byte[], List<String>> unpack, Path result) throws IOException {
        var lines = new ArrayList<String>();
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));

        var files = Js5Util.unpackGroup(archiveIndex, group, Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));

        for (var file : files.keySet()) {
            lines.addAll(unpack.apply(file, files.get(file)));
            lines.add("");
        }

        Files.write(result, lines);
    }

    private static void unpackDefaults(int archive, int group, BiFunction<Integer, byte[], List<String>> unpack, Path result) throws IOException {
        var lines = new ArrayList<String>();
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));

        var files = Js5Util.unpackGroup(archiveIndex, group, Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));

        for (var file : files.keySet()) {
            lines.addAll(unpack.apply(file, files.get(file)));
            lines.add("");
        }

        Files.write(result, lines);
    }

    private static void unpackConfigArchive(int archive, int bits, BiFunction<Integer, byte[], List<String>> unpack, Path result) throws IOException {
        var lines = new ArrayList<String>();
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));

            for (var file : files.keySet()) {
                lines.addAll(unpack.apply((group << bits) + file, files.get(file)));
                lines.add("");
            }
        }

        Files.write(result, lines);
    }

    private static void unpackArchive(int archive, BiFunction<Integer, byte[], List<String>> unpack, Path result) throws IOException {
        var lines = new ArrayList<String>();
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve("255/" + archive + ".dat"))));

        for (var group : archiveIndex.groupId) {
            var file = Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));
            lines.addAll(unpack.apply(group, file));
            lines.add("");
        }

        Files.write(result, lines);
    }

    private static byte[] get(int archive, int group) throws IOException {
        return Js5Util.decompress(Files.readAllBytes(BASE_PATH.resolve(archive + "/" + group + ".dat")));
    }
}
