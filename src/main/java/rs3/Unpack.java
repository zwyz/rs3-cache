package rs3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rs3.unpack.InterfaceUnpacker;
import rs3.unpack.MaterialUnpacker;
import rs3.unpack.StylesheetUnpacker;
import rs3.unpack.VarDomain;
import rs3.unpack.config.*;
import rs3.unpack.map.MapSquare;
import rs3.unpack.script.ScriptUnpacker;
import rs3.unpack.uianim.Anim;
import rs3.unpack.uianim.AnimCurve;
import rs3.js5.Js5Util;
import rs3.js5.Js5ArchiveIndex;
import rs3.unpack.cutscene2d.*;
import rs3.unpack.vfx.*;
import rs3.unpack.worldmap.MapAreaUnpacker;
import rs3.util.Packet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

// todo: clean this up
public class Unpack {
    private static final Path BASE_PATH = Path.of(System.getProperty("user.home") + "/.rscache/rs3");
    public static final Gson GSON = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();

    public static void main(String[] args) throws IOException {
        var root = Path.of("unpacked");

        // config-like
        unpackConfig(root.resolve("scripts"));
        unpackConfigGroup(23, 0, MapAreaUnpacker::unpack, root.resolve("scripts/dump.wma")); // worldmapdata details
//        unpackConfigGroup(42, 0, MapLabelUnpacker::unpack, root.resolve("scripts/dump.mwl")); // worldmaplabels
        unpackDefaults(root.resolve("defaults"));
        unpackScripts(12, root.resolve("scripts/dump.cs2"));
        unpackConfigArchive(3, 16, InterfaceUnpacker::unpack, root.resolve("scripts/dump.if3"));
        unpackConfigArchive(60, 0, StylesheetUnpacker::unpack, root.resolve("scripts/dump.stylesheet"));

        unpackConfigArchive(26, 0, MaterialUnpacker::unpack, root.resolve("scripts/dump.material"));
//        unpackArchive(58, root.resolve("unknown58"), ".unknown58"); // todo
//        unpackArchive(62, root.resolve("unknown62"), ".unknown62"); // todo

//        iterateArchive(8, SpriteUnpacker::unpack);
//        iterateArchive(54, TextureUnpacker::unpack);
        unpackArchive(10, root.resolve("binary"), ".dat");
        unpackArchive(59, root.resolve("ttf"), ".ttf");
        unpackArchiveTransformed(61, VFXUnpacker::unpack, root.resolve("vfx"), ".json");
        unpackGroupTransformed(65, 0, b -> GSON.toJson(new AnimCurve(new Packet(b))), root.resolve("uianimcurve"), ".json");
        unpackGroupTransformed(65, 1, b -> GSON.toJson(new Anim(new Packet(b))), root.resolve("uianim"), ".json");
        unpackArchiveTransformed(66, b -> GSON.toJson(new Cutscene2D(new Packet(b))), root.resolve("cutscene2d"), ".json");
//        unpackMaps(root);
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

    // 2.6 config_inv
    // 2.11 config_param
    // 2.18 config_area
    // 2.33 config_cursor
    // 2.35 config_quest
    // 2.36 config_mel
    // 2.40 config_dbtable
    // 2.41 config_dbrow
    // 2.70 config_gamelogevent
    // 16 config_loc
    // 17 config_enum
    // 18 config_npc
    // 19 config_obj
    // 22 config_struct
    // 2.60 var_player
    // 2.62 var_client

    // 3 interfaces
    // 12 clientscripts
    // 23 worldmapdata
    // 24 quickchat
    // 33 loadingscreens
    // 35 cutscenes
    // 49 dbtableindex
    // 57 config_achievement

    // 1: bases
    // 2: config
    // 2.1: config_flu
    // 2.2: ?
    // 2.3: config_idk
    // 2.4: config_flo
    // 2.5: config_inv
    // 2.7: ?
    // 2.11: config_param
    // 2.18: config_area
    // 2.29: config_skybox
    // 2.31: config_light
    // 2.32: config_bas
    // 2.33: config_cursor
    // 2.34: config_msi
    // 2.35: config_quest
    // 2.36: config_mel
    // 2.40: config_dbtable
    // 2.41: config_dbrow
    // 2.42: ?
    // 2.46: config_hitmark
    // 2.48: config_itemcode
    // 2.49: config_category
    // 2.60: var_player
    // 2.61: var_npc
    // 2.62: var_client
    // 2.63: var_world
    // 2.64: var_region
    // 2.65: var_object
    // 2.66: var_clan
    // 2.67: var_clan_setting
    // 2.68: var_controller
    // 2.69: var_bit
    // 2.70: config_gamelogevent
    // 2.72: config_headbar
    // 2.73: ?
    // 2.75: var_global
    // 2.76: config_water
    // 2.77: config_seqgroup
    // 2.80: var_player_group
    // 2.83: config_worldarea
    // 3: interfaces
    // 5.x.0: map_loc
    // 5.x.1: map_loc_underwater
    // 5.x.2: map_npc
    // 5.x.3: map_floor
    // 5.x.4: map_sound
    // 5.x.5: map_?
    // 5.x.6: map_?
    // 5.x.7: map_?
    // 5.x.8: map_?
    // 8: sprites
    // 10: binary
    // 12: clientscripts
    // 13: fontmetrics
    // 14: vorbis
    // 16: config_loc
    // 17: config_enum
    // 18: config_npc
    // 19: config_obj
    // 20: config_seq
    // 21: config_spot
    // 22: config_struct
    // 23: worldmapdata
    // 24: quickchat
    // 26: materials
    // 27: particles
    // 28: defaults
    // 29: billboards
    // 32: loadingsprites
    // 33: loadingscreens
    // 34: loadingspritesraw
    // 35: cutscenes
    // 40: audiostreams
    // 41: worldmapareadata
    // 42: worldmaplabels
    // 47: modelsrt7
    // 48: animsrt7
    // 49: dbtableindex
    // 52: textures_dxt
    // 53: textures_png
    // 54: textures_png_mipped
    // 55: textures_etc
    // 56: anim_keyframes
    // 57: config_achievement
    // 58: ?
    // 59: ttf
    // 60: stylesheet
    // 61: vfx
    // 62: ?
    // 65: uianim
    // 66: cutscene2d

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

    private static void unpackConfig(Path path) throws IOException {
        Files.createDirectories(path);

        // things stuff depends on
        unpackConfigGroup(2, 60, (id, data) -> VarUnpacker.unpack(VarDomain.PLAYER, id, data), path.resolve("dump.varplayer"));
        unpackConfigGroup(2, 61, (id, data) -> VarUnpacker.unpack(VarDomain.NPC, id, data), path.resolve("dump.varnpc"));
        unpackConfigGroup(2, 62, (id, data) -> VarUnpacker.unpack(VarDomain.CLIENT, id, data), path.resolve("dump.varclient"));
        unpackConfigGroup(2, 63, (id, data) -> VarUnpacker.unpack(VarDomain.WORLD, id, data), path.resolve("dump.varworld")); // client ignores
        unpackConfigGroup(2, 64, (id, data) -> VarUnpacker.unpack(VarDomain.REGION, id, data), path.resolve("dump.varregion")); // client ignores
        unpackConfigGroup(2, 65, (id, data) -> VarUnpacker.unpack(VarDomain.OBJECT, id, data), path.resolve("dump.varobject"));
        unpackConfigGroup(2, 66, (id, data) -> VarUnpacker.unpack(VarDomain.CLAN, id, data), path.resolve("dump.varclan"));
        unpackConfigGroup(2, 67, (id, data) -> VarUnpacker.unpack(VarDomain.CLAN_SETTING, id, data), path.resolve("dump.varclansetting"));
        unpackConfigGroup(2, 68, (id, data) -> VarUnpacker.unpack(VarDomain.CONTROLLER, id, data), path.resolve("dump.varcontroller")); // client ignores
        unpackConfigGroup(2, 75, (id, data) -> VarUnpacker.unpack(VarDomain.SHARED, id, data), path.resolve("dump.varshared")); // client ignores
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
        var lines = new ArrayList<String>();
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
