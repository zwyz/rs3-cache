package rs3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rs3.js4.Jagfile;
import rs3.js4.Js4ResourceProvider;
import rs3.js4.OpenRS2Js4ResourceProvider;
import rs3.js5.*;
import rs3.unpack.*;
import rs3.unpack.config.*;
import rs3.unpack.cutscene2d.Cutscene2D;
import rs3.unpack.font.FontMetrics;
import rs3.unpack.map.MapSquare;
import rs3.unpack.script.Command;
import rs3.unpack.script.ScriptUnpacker;
import rs3.unpack.ui_anim.Anim;
import rs3.unpack.ui_anim.AnimCurve;
import rs3.unpack.unknown62.AnimatorController;
import rs3.unpack.vfx.VFXUnpacker;
import rs3.unpack.worldmap.WorldMapUnpacker;
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
import java.util.concurrent.StructuredTaskScope;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

// todo: clean this up
public class Unpack {
    public static final boolean DUMP_CONFIG_IDS = true;

    public static int VERSION;
    public static int ID;
    public static Js5ResourceProvider PROVIDER;
    public static Js5MasterIndex MASTER_INDEX;
    public static int CONFIG_VERSION;
    public static final Gson GSON = new GsonBuilder().serializeSpecialFloatingPointValues().create();
    public static final Gson GSON_PRETTY = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();

    public static void main(String[] args) throws IOException, InterruptedException {
        unpackLive("unpacked/live", 949, 1, 0, "content.runescape.com", 43594, ClientTokenProvider.getClientToken());
//        unpackOpenRS2("unpacked/2026-03-16", 946, "runescape", 2495);
    }

    public static void unpackOpenRS2(String path, int version, String scope, int id) throws IOException {
        VERSION = version;
        ID = id;

        if (Unpack.VERSION < 400) {
            unpackLegacy(Path.of(path), new OpenRS2Js4ResourceProvider(scope, id));
        } else {
            unpack(Path.of(path), new MemoryCacheResourceProvider(new FileSystemCacheResourceProvider(
                    Path.of(System.getProperty("user.home") + "/.rscache/rs3"),
                    new OpenRS2Js5ResourceProvider(scope, id))
            ));
        }
    }

    public static void unpackLive(String path, int version, int subversion, int language, String host, int port, String token) throws IOException {
        VERSION = version;
        ID = -1;

        unpack(Path.of(path), new MemoryCacheResourceProvider(new FileSystemCacheResourceProvider(
                Path.of(System.getProperty("user.home") + "/.rscache/rs3"),
                new TcpJs5ResourceProvider(host, port, token, version, subversion, language))
        ));
    }

    public static void unpack(Path root, Js5ResourceProvider provider) throws IOException {
        PROVIDER = provider;
        MASTER_INDEX = new Js5MasterIndex(Js5Util.decompress(PROVIDER.get(255, 255, false, 0)));
        CONFIG_VERSION = MASTER_INDEX.getArchiveData(Js5Archive.JS5_CONFIG.id).getVersion();
        Command.reset(); // todo: make non-static
        Unpacker.reset(); // todo: make non-static
        ScriptUnpacker.reset(); // todo: make non-static

        Files.createDirectories(root);
        Files.createDirectories(root.resolve("config"));
        Files.createDirectories(root.resolve("script"));
        Files.createDirectories(root.resolve("interface"));

        loadDebugNames(0, Type.COMPONENT);
        loadDebugNames(5, Type.BAS);
        loadDebugNames(9, Type.CATEGORY);
        loadDebugNames(12, Type.CURSOR);
        loadDebugNames(14, Type.DBROW);
        loadDebugNames(15, Type.DBTABLE);
        loadDebugNames(16, Type.ENUM);
        loadDebugNames(20, Type.HEADBAR);
        loadDebugNames(21, Type.HITMARK);
        loadDebugNames(24, Type.INTERFACE);
        loadDebugNames(25, Type.INV);
        loadDebugNames(28, Type.LOC);
        loadDebugNames(29, Type.MAPELEMENT);
        loadDebugNames(32, Type.MATERIAL);
        loadDebugNames(34, Type.MODEL);
        loadDebugNames(35, Type.NPC);
        loadDebugNames(36, Type.OBJ);
        loadDebugNames(37, Type.PARAM);
        loadDebugNames(41, Type.QUEST);
        loadDebugNames(44, Type.SEQ);
        loadDebugNames(49, Type.GRAPHIC);
        loadDebugNames(50, Type.STRUCT);
        loadDebugNames(55, Type.VAR_CLAN);
        loadDebugNames(56, Type.VAR_CLAN_SETTING);
        loadDebugNames(57, Type.VAR_CLIENT);
        loadDebugNames(59, Type.VAR_NPC);
        loadDebugNames(60, Type.VAR_OBJECT);
        loadDebugNames(61, Type.VAR_PLAYER);
        loadDebugNames(64, Type.SOUND);
        loadDebugNames(69, Type.MIDI);
        loadDebugNames(80, Type.VAR_PLAYER_GROUP);
        loadDebugNames(89, Type.ACHIEVEMENT);
        loadDebugNames(90, Type.FONTMETRICS);
        loadDebugNames(92, Type.STYLESHEET);
        loadDebugNames(96, Type.UI_ANIM_CURVE);
        loadDebugNames(97, Type.UI_ANIM);

        // load names
        loadGroupNamesScriptTrigger(12, Unpacker.SCRIPT_NAME);
        loadGroupNames(Path.of("data/names/scripts.txt"), 12, Unpacker.SCRIPT_NAME::put);
        loadGroupNames(Path.of("data/names/graphics.txt"), 8,  (id, name) -> Unpacker.setSymbolName(Type.GRAPHIC, id, name));
        loadGroupNames(Path.of("data/names/binaries.txt"), 10, Unpacker.BINARY_NAME::put);

        // things stuff depends on
        if (Unpack.VERSION < 751) {
            if (Unpack.VERSION < 488) {
                unpackConfigGroup(2, 14, VarPlayerBitUnpacker::unpack, root.resolve("config/dump.varbit"));
            } else {
                unpackConfigArchive(22, 10, VarPlayerBitUnpacker::unpack, root.resolve("config/dump.varbit"));
            }
            unpackConfigGroup(2, 15, VarClientStringUnpacker::unpack, root.resolve("config/dump.varcstr"));
            unpackConfigGroup(2, 16, VarPlayerUnpacker::unpack, root.resolve("config/dump.varp"));
            unpackConfigGroup(2, 19, VarClientUnpacker::unpack, root.resolve("config/dump.varc"));
            unpackConfigGroup(2, 20, VarObjUnpacker::unpack, root.resolve("config/dump.varobj"));
            unpackConfigGroup(2, 21, VarPlayerUnpacker::unpack, root.resolve("config/dump.varpstr"));
            unpackConfigGroup(2, 22, VarSharedUnpacker::unpack, root.resolve("config/dump.vars"));
            unpackConfigGroup(2, 23, VarSharedStringUnpacker::unpack, root.resolve("config/dump.varsstr"));
            unpackConfigGroup(2, 24, VarNpcUnpacker::unpack, root.resolve("config/dump.varn"));
            unpackConfigGroup(2, 25, VarNpcBitUnpacker::unpack, root.resolve("config/dump.varnbit"));
            unpackConfigGroup(2, 47, VarClanUnpacker::unpack, root.resolve("config/dump.varclan"));
            unpackConfigGroup(2, 54, VarClanSettingUnpacker::unpack, root.resolve("config/dump.varclansetting"));
        } else {
            unpackConfigGroup(2, 60, (id, data) -> VarUnpacker.unpack(VarDomain.PLAYER, id, data), root.resolve("config/dump.varp"));
            unpackConfigGroup(2, 61, (id, data) -> VarUnpacker.unpack(VarDomain.NPC, id, data), root.resolve("config/dump.varn"));
            unpackConfigGroup(2, 62, (id, data) -> VarUnpacker.unpack(VarDomain.CLIENT, id, data), root.resolve("config/dump.varc"));
            unpackConfigGroup(2, 63, (id, data) -> VarUnpacker.unpack(VarDomain.WORLD, id, data), root.resolve("config/dump.varworld")); // client ignores
            unpackConfigGroup(2, 64, (id, data) -> VarUnpacker.unpack(VarDomain.REGION, id, data), root.resolve("config/dump.varregion")); // client ignores
            unpackConfigGroup(2, 65, (id, data) -> VarUnpacker.unpack(VarDomain.OBJECT, id, data), root.resolve("config/dump.varobj"));
            unpackConfigGroup(2, 66, (id, data) -> VarUnpacker.unpack(VarDomain.CLAN, id, data), root.resolve("config/dump.varclan"));
            unpackConfigGroup(2, 67, (id, data) -> VarUnpacker.unpack(VarDomain.CLAN_SETTING, id, data), root.resolve("config/dump.varclansetting"));
            unpackConfigGroup(2, 68, (id, data) -> VarUnpacker.unpack(VarDomain.CONTROLLER, id, data), root.resolve("config/dump.varcontroller")); // client ignores
            unpackConfigGroup(2, 75, (id, data) -> VarUnpacker.unpack(VarDomain.GLOBAL, id, data), root.resolve("config/dump.varglobal")); // client ignores
            unpackConfigGroup(2, 80, (id, data) -> VarUnpacker.unpack(VarDomain.PLAYER_GROUP, id, data), root.resolve("config/dump.varplayergroup"));
            unpackConfigGroup(2, 69, VarBitUnpacker::unpack, root.resolve("config/dump.varbit"));
        }

        unpackConfigGroup(2, 11, ParamUnpacker::unpack, root.resolve("config/dump.param"));

        // regular configs
        unpackConfigGroup(2, 1, FloorUnderlayUnpacker::unpack, root.resolve("config/dump.flu"));
        unpackConfigGroup(2, 2, HuntUnpacker::unpack, root.resolve("config/dump.hunt")); // client ignores
        unpackConfigGroup(2, 3, IDKUnpacker::unpack, root.resolve("config/dump.idk"));
        unpackConfigGroup(2, 4, FloorOverlayUnpacker::unpack, root.resolve("config/dump.flo"));
        unpackConfigGroup(2, 5, InvUnpacker::unpack, root.resolve("config/dump.inv"));

        if (Unpack.VERSION < 488) {
            unpackConfigGroup(2, 6, LocUnpacker::unpack, root.resolve("config/dump.loc"));
        } else {
            unpackConfigArchive(16, 8, LocUnpacker::unpack, root.resolve("config/dump.loc")); // 6
        }

        unpackConfigGroup(2, 7, MesAnimUnpacker::unpack, root.resolve("config/dump.mesanim")); // client ignores

        if (Unpack.VERSION < 488) {
            unpackConfigGroup(2, 8, EnumUnpacker::unpack, root.resolve("config/dump.enum"));
        } else {
            unpackConfigArchive(17, 8, EnumUnpacker::unpack, root.resolve("config/dump.enum")); // 8
        }

        if (Unpack.VERSION < 488) {
            unpackConfigGroup(2, 9, NPCUnpacker::unpack, root.resolve("config/dump.npc"));
        } else {
            unpackConfigArchive(18, 7, NPCUnpacker::unpack, root.resolve("config/dump.npc")); // 9
        }

        if (Unpack.VERSION < 488) {
            unpackConfigGroup(2, 10, ObjUnpacker::unpack, root.resolve("config/dump.obj"));
        } else {
            unpackConfigArchive(19, 8, ObjUnpacker::unpack, root.resolve("config/dump.obj")); // 10
        }

        if (Unpack.VERSION < 488) {
            unpackConfigGroup(2, 12, SeqUnpacker::unpack, root.resolve("config/dump.seq"));
        } else {
            unpackConfigArchive(20, 7, SeqUnpacker::unpack, root.resolve("config/dump.seq")); // 12
        }

        if (Unpack.VERSION < 488) {
            unpackConfigGroup(2, 13, EffectAnimUnpacker::unpack, root.resolve("config/dump.spot"));
        } else {
            unpackConfigArchive(21, 8, EffectAnimUnpacker::unpack, root.resolve("config/dump.spot")); // 13
        }

        unpackConfigGroup(2, 18, AreaUnpacker::unpack, root.resolve("config/dump.area")); // client ignores

        if (Unpack.VERSION < 763) {
            unpackConfigGroup(2, 26, StructUnpacker::unpack, root.resolve("config/dump.struct"));
        } else {
            unpackConfigArchive(22, 5, StructUnpacker::unpack, root.resolve("config/dump.struct")); // 26
        }

        unpackConfigGroup(2, 29, SkyBoxUnpacker::unpack, root.resolve("config/dump.skybox"));
        unpackConfigGroup(2, 31, LightUnpacker::unpack, root.resolve("config/dump.light"));
        unpackConfigGroup(2, 32, BASUnpacker::unpack, root.resolve("config/dump.bas"));
        unpackConfigGroup(2, 33, CursorUnpacker::unpack, root.resolve("config/dump.cursor"));
        unpackConfigGroup(2, 34, MSIUnpacker::unpack, root.resolve("config/dump.msi"));
        unpackConfigGroup(2, 35, QuestUnpacker::unpack, root.resolve("config/dump.quest"));
        unpackConfigGroup(2, 36, MapElementUnpacker::unpack, root.resolve("config/dump.mel")); // todo
        unpackConfigGroup(2, 40, DBTableUnpacker::unpack, root.resolve("config/dump.dbtable")); // todo: use dbtableindex
        unpackConfigGroup(2, 41, DBRowUnpacker::unpack, root.resolve("config/dump.dbrow"));
        unpackConfigGroup(2, 42, ControllerUnpacker::unpack, root.resolve("config/dump.controller")); // client ignores
        unpackConfigGroup(2, 46, HitmarkUnpacker::unpack, root.resolve("config/dump.hitmark"));
        unpackConfigGroup(2, 48, ItemCodeUnpacker::unpack, root.resolve("config/dump.itemcode")); // client ignores
        unpackConfigGroup(2, 49, CategoryUnpacker::unpack, root.resolve("config/dump.category")); // client ignores
        unpackConfigGroup(2, 70, GameLogEventUnpacker::unpack, root.resolve("config/dump.gamelogevent")); // client ignores
        unpackConfigGroup(2, 72, HeadbarUnpacker::unpack, root.resolve("config/dump.headbar"));
        unpackConfigGroup(2, 73, BugTemplateUnpacker::unpack, root.resolve("config/dump.bugtemplate"));
        unpackConfigGroup(2, 76, WaterUnpacker::unpack, root.resolve("config/dump.water"));
        unpackConfigGroup(2, 77, SeqGroupUnpacker::unpack, root.resolve("config/dump.seqgroup"));
        unpackConfigGroup(2, 83, WorldAreaUnpacker::unpack, root.resolve("config/dump.worldarea"));
        unpackConfigArchive(57, 7, AchievementUnpacker::unpack, root.resolve("config/dump.achievement")); // 85
        unpackConfigGroup(27, 0, ParticleEmitterUnpacker::unpack, root.resolve("config/dump.particleemitter"));
        unpackConfigGroup(27, 1, ParticleEffectorUnpacker::unpack, root.resolve("config/dump.particleeffector"));
        unpackConfigGroup(29, 0, BillboardUnpacker::unpack, root.resolve("config/dump.billboard"));
        unpackConfigGroup(24, 0, QuickChatCatUnpacker::unpack, root.resolve("config/dump.quickchatcat"));
        unpackConfigGroup(24, 1, QuickChatPhraseUnpacker::unpack, root.resolve("config/dump.quickchatphrase"));

        WorldMapUnpacker.unpack(root.resolve("worldmap"));

        // defaults
        unpackDefaults(28, 3, GraphicsDefaultsUnpacker::unpack, root.resolve("config/graphics.defaults"));
        unpackDefaults(28, 4, AudioDefaultsUnpacker::unpack, root.resolve("config/audio.defaults"));
        unpackDefaults(28, 6, WearPosDefaultsUnpacker::unpack, root.resolve("config/wearpos.defaults"));
        unpackDefaults(28, 10, WorldMapDefaultsUnpacker::unpack, root.resolve("config/worldmap.defaults"));
        unpackDefaults(28, 12, TitleDefaultsUnpacker::unpack, root.resolve("config/title.defaults"));

        // scripts
        if (!Command.MISSING_OPCODES) unpackScripts(12, root.resolve("script"));

        // interface
        unpackInterfaces(3, InterfaceUnpacker::unpack, root.resolve("interface"));

        // materials
//        unpackConfigArchive(9, 0, TextureUnpacker::unpack, root.resolve("config/dump.texture")); // TODO: buggy in some revs
//        unpackConfigArchive(26, 0, MaterialUnpacker::unpack, root.resolve("config/dump.material")); // todo: buggy in some revs

        // other
        unpackConfigArchive(60, 0, StylesheetUnpacker::unpack, root.resolve("config/dump.stylesheet"));
//        iterateArchive(8, SpriteUnpacker::unpack);
//        unpackArchiveTransformed(47, b -> GSON.toJson(new Model(new Packet(b))), root.resolve("model"), ".json");
//        iterateArchive(54, TextureUnpacker::unpack);
        unpackBinaries(root.resolve("binary"));
        unpackArchiveTransformed(58, b -> GSON_PRETTY.toJson(new FontMetrics(new Packet(b))), root.resolve("fontmetrics"), ".json");
        unpackTTF(root.resolve("ttf"));
        unpackArchiveTransformed(61, VFXUnpacker::unpack, root.resolve("vfx"), ".json");
        unpackArchiveTransformed(62, b -> GSON_PRETTY.toJson(AnimatorController.decode(new Packet(b))), root.resolve("animator"), ".json");
        unpackGroupTransformed(65, 0, b -> GSON_PRETTY.toJson(new AnimCurve(new Packet(b))), root.resolve("uianimcurve"), ".json");
        unpackGroupTransformed(65, 1, b -> GSON_PRETTY.toJson(new Anim(new Packet(b))), root.resolve("uianim"), ".json");
        unpackArchiveTransformed(66, b -> GSON_PRETTY.toJson(new Cutscene2D(new Packet(b))), root.resolve("cutscene2d"), ".json");

        // maps
//        unpackMaps(root);
        unpackWorldAreaMap(root);
    }

    public static void unpackLegacy(Path root, Js4ResourceProvider provider) throws IOException {
        Files.createDirectories(root);
        Files.createDirectories(root.resolve("config"));
        var config = new Jagfile(provider.get(0, 2));
        unpackLegacyConfig(config, "idk", IDKUnpacker::unpack, root.resolve("config/dump.idk"));
        unpackLegacyConfig(config, "flo", FloorOverlayUnpacker::unpack, root.resolve("config/dump.flo"));
        unpackLegacyConfig(config, "loc", LocUnpacker::unpack, root.resolve("config/dump.loc"));
        unpackLegacyConfig(config, "mesanim", MesAnimUnpacker::unpack, root.resolve("config/dump.mesanim"));
        unpackLegacyConfig(config, "npc", NPCUnpacker::unpack, root.resolve("config/dump.npc"));
        unpackLegacyConfig(config, "obj", ObjUnpacker::unpack, root.resolve("config/dump.obj"));
        unpackLegacyConfig(config, "param", ParamUnpacker::unpack, root.resolve("config/dump.param"));
        unpackLegacyConfig(config, "seq", SeqUnpacker::unpack, root.resolve("config/dump.seq"));
        unpackLegacyConfig(config, "spotanim", EffectAnimUnpacker::unpack, root.resolve("config/dump.spot"));
    }

    private static void unpackLegacyConfig(Jagfile jagfile, String name, BiFunction<Integer, byte[], List<String>> unpacker, Path path) throws IOException {
        var indexBytes = jagfile.read(name + ".idx", null);

        if (indexBytes == null) {
            return;
        }

        var index = new Packet(indexBytes);
        var data = jagfile.read(name + ".dat", null);
        var count = index.g2();
        var offset = 2;
        var offsets = new int[count];

        for (var i = 0; i < count; i++) {
            offsets[i] = offset;
            offset += index.g2();
        }

        var lines = new ArrayList<String>();

        for (var i = 0; i < count; i++) {
            var start = offsets[i];
            var end = i == count - 1 ? data.length : offsets[i + 1];
            var file = Arrays.copyOfRange(data, start, end);
            lines.addAll(unpacker.apply(i, file));
            lines.add("");
        }

        Files.write(path, lines);
    }

    private static void loadGroupNames(Path path, int archive, BiConsumer<Integer, String> consumer) throws IOException {
        if (archive >= MASTER_INDEX.getArchiveCount() || MASTER_INDEX.getArchiveData(archive).getCrc() == 0) {
            return; // empty archives don't get packed
        }

        var unhash = new HashMap<Integer, String>();
        generateNames(path, unhash);
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, archive, false, 0)));

        if (archiveIndex.groupNameHash == null) {
            return; // clientscript names disabled in some older revs
        }

        for (var group : archiveIndex.groupId) {
            var hash = archiveIndex.groupNameHash[group];

            if (unhash.containsKey(hash)) {
                consumer.accept(group, unhash.get(hash));
            }
        }
    }

    private static void loadGroupNamesScriptTrigger(int archive, Map<Integer, String> names) throws IOException {
        if (archive >= MASTER_INDEX.getArchiveCount() || MASTER_INDEX.getArchiveData(archive).getCrc() == 0) {
            return; // empty archives don't get packed
        }

        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, archive, false, 0)));

        if (archiveIndex.groupNameHash == null) {
            return; // clientscript names disabled in some older revs
        }

        var archiveIndexConfig = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, 2, false, 0)));
        var archiveIndexInterface = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, 3, false, 0)));
        var maxMapElement = 0;
        var maxCutscene = 0;

        if (MASTER_INDEX.getArchiveCount() > 35 && MASTER_INDEX.getArchiveData(35).getCrc() != 0) {
            maxCutscene = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, 35, false, 0))).groupArraySize;
        }

        if (archiveIndexConfig.groupMaxFileId.length > 36) {
            maxMapElement = archiveIndexConfig.groupMaxFileId[36];
        }

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
                } else if (subject > 0x10000 && trigger.category) {
                    var category = subject - 0x10000;

                    if (category < maxCategory) {
                        names.put(group, "[" + trigger.name().toLowerCase(Locale.ROOT) + ",_" + Unpacker.format(Type.CATEGORY, category) + "]");
                    }
                } else if (trigger.type == Type.MAPELEMENT && subject < maxMapElement) {
                    names.put(group, "[" + trigger.name().toLowerCase(Locale.ROOT) + "," + Unpacker.format(trigger.type, subject) + "]");
                } else if (trigger.type == Type.CUTSCENE && subject < maxCutscene) {
                    names.put(group, "[" + trigger.name().toLowerCase(Locale.ROOT) + "," + Unpacker.format(trigger.type, subject) + "]");
                } else if (trigger.type == Type.INTERFACE && subject < maxInterface) {
                    names.put(group, "[" + trigger.name().toLowerCase(Locale.ROOT) + "," + Unpacker.format(trigger.type, subject) + "]");
                } else if (trigger.type == Type.TWITCH_EVENT && subject < 4) {
                    names.put(group, "[" + trigger.name().toLowerCase(Locale.ROOT) + "," + Unpacker.format(trigger.type, subject) + "]");
                } else if (trigger.type == Type.MINIMENU_EVENT && subject < 4) {
                    names.put(group, "[" + trigger.name().toLowerCase(Locale.ROOT) + "," + Unpacker.format(trigger.type, subject) + "]");
                }
            }
        }
    }

    private static void generateNames(Path path, Map<Integer, String> names) throws IOException {
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

    private static void loadDebugNames(int group, Type type) {
        if (Js5Archive.JS5_GAMEVALS.id >= MASTER_INDEX.getArchiveCount() || MASTER_INDEX.getArchiveData(Js5Archive.JS5_GAMEVALS.id).getCrc() == 0) {
            return; // empty archives don't get packed
        }

        int[] hashes = null;
        if (type == Type.GRAPHIC) {
            var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, Js5Archive.JS5_SPRITES.id, false, 0)));
            hashes = archiveIndex.groupNameHash;
        }

        var data = Js5Util.decompress(PROVIDER.get(Js5Archive.JS5_GAMEVALS.id, group, false, 0));
        var buf = new Packet(data);

        var packType = buf.g4s();
        if (packType != 1 && packType != 2) {
            throw new IllegalStateException("Unknown pack type: " + packType);
        }

        var count = buf.g4s();
        var ids = new int[count];
        var offsets = new int[count];

        for (int i = 0; i < count; i++) {
            ids[i] = (packType == 1) ? i : buf.g4s();
            offsets[i] = buf.g4s();
        }

        var lastNoPrefix = -1;
        var tableStart = buf.pos;

        for (int i = 0; i < count; i++) {
            var offset = offsets[i];
            if (offset == -1) {
                continue;
            }

            buf.pos = tableStart + offset;
            var name = buf.gjstr().toLowerCase();
            var id = ids[i];

            var finalType = type;
            if (type == Type.VAR_CLAN || type == Type.VAR_CLAN_SETTING || type == Type.VAR_CLIENT || type == Type.VAR_NPC || type == Type.VAR_OBJECT || type == Type.VAR_PLAYER || type == Type.VAR_PLAYER_GROUP) {
                if (name.startsWith("_")) {
                    finalType = VarDomain.byType(type).bittype;
                    name = name.substring(1);
                    id = id - lastNoPrefix - 1;
                } else {
                    lastNoPrefix = id;
                }
            } else if (type == Type.GRAPHIC && hashes != null && id < hashes.length) {
                // try to reverse 'some_name_1' -> 'some_name,1'
                int lastUnderscore = name.lastIndexOf("_");
                if (lastUnderscore != -1) {
                    String possibleName = name.substring(0, lastUnderscore) + "," + name.substring(lastUnderscore + 1);
                    if (possibleName.hashCode() == hashes[id]) {
                        name = possibleName;
                    }
                }
            } else if (type == Type.COMPONENT) {
                // replace 'interface__component' -> 'interface:component'
                name = name.replace("__", ":");
            }
            Unpacker.setSymbolName(finalType, id, name);
        }
    }

    private static void unpackWorldAreaMap(Path root) throws IOException {
        if (Unpack.VERSION < 910) return; // TODO: broken for old revs
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
        if (archive >= MASTER_INDEX.getArchiveCount() || MASTER_INDEX.getArchiveData(archive).getCrc() == 0) {
            return; // empty archives don't get packed
        }

        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, archive, false, 0)));
        var groups = preloadGroups(archive);

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, groups[group]);

            for (var file : files.keySet()) {
                ScriptUnpacker.load(group, files.get(file));
            }
        }

        ScriptUnpacker.decompile();

        for (var group : archiveIndex.groupId) {
            var lines = new ArrayList<>(ScriptUnpacker.unpack(group));
            lines.addFirst("// " + group);
            Files.write(path.resolve(Unpacker.getScriptName(group) + ".cs2"), lines);
        }
    }

    private static byte[][] preloadGroups(int id) {
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, id, false, 0)));
        var groups = new byte[archiveIndex.groupArraySize][];

        try (var scope = StructuredTaskScope.open()) {
            for (int group : archiveIndex.groupId) {
                scope.fork(() -> {
                    groups[group] = PROVIDER.get(id, group, false, 0);
                    return null;
                });
            }

            scope.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return groups;
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

    private static void unpackBinaries(Path path) throws IOException {
        Files.createDirectories(path);
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, Js5Archive.JS5_BINARY.id, false, 0)));

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, PROVIDER.get(Js5Archive.JS5_BINARY.id, group, false, 0));
            Files.write(path.resolve(Unpacker.BINARY_NAME.getOrDefault(group, group + ".dat")), files.get(0));
        }
    }

    private static void unpackTTF(Path path) throws IOException {
        if (Js5Archive.JS5_TRUETYPEFONTS.id >= MASTER_INDEX.getArchiveCount() || MASTER_INDEX.getArchiveData(Js5Archive.JS5_TRUETYPEFONTS.id).getCrc() == 0) {
            return; // empty archives don't get packed
        }

        Files.createDirectories(path);
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, Js5Archive.JS5_TRUETYPEFONTS.id, false, 0)));

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, PROVIDER.get(Js5Archive.JS5_TRUETYPEFONTS.id, group, false, 0));
            Files.write(path.resolve(group + ".dat"), files.get(0));
        }
    }

    private static void unpackArchiveTransformed(int archive, Function<byte[], String> unpack, Path path, String extension) throws IOException {
        if (archive >= MASTER_INDEX.getArchiveCount() || MASTER_INDEX.getArchiveData(archive).getCrc() == 0) {
            return; // empty archives don't get packed
        }

        Files.createDirectories(path);
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, archive, false, 0)));

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, PROVIDER.get(archive, group, false, 0));

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
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, archive, false, 0)));

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, PROVIDER.get(archive, group, false, 0));
            unpack.accept(group, files);
        }
    }

    private static void unpackGroupTransformed(int archive, int group, Function<byte[], String> unpack, Path path, String extension) throws IOException {
        if (archive >= MASTER_INDEX.getArchiveCount() || MASTER_INDEX.getArchiveData(archive).getCrc() == 0) {
            return; // empty archives don't get packed
        }

        Files.createDirectories(path);
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, archive, false, 0)));
        var files = Js5Util.unpackGroup(archiveIndex, group, PROVIDER.get(archive, group, false, 0));

        for (var file : files.keySet()) {
            Files.writeString(path.resolve(file + extension), unpack.apply(files.get(file)));
        }
    }

    private static void iterateGroupFiles(int archive, int group, BiConsumer<Integer, byte[]> unpack) throws IOException {
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, archive, false, 0)));

        var files = Js5Util.unpackGroup(archiveIndex, group, PROVIDER.get(archive, group, false, 0));

        for (var file : files.keySet()) {
            unpack.accept(file, files.get(file));
        }
    }

    private static void unpackConfigGroup(int archive, int group, BiFunction<Integer, byte[], List<String>> unpack, Path result) throws IOException {
        var lines = new ArrayList<String>();
        if (archive >= MASTER_INDEX.getArchiveCount() || MASTER_INDEX.getArchiveData(archive).getCrc() == 0) {
            return; // empty archives don't get packed
        }

        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, archive, false, 0)));

        if (Arrays.binarySearch(archiveIndex.groupId, group) < 0) {
            return; // empty groups don't get packed
        }

        var files = Js5Util.unpackGroup(archiveIndex, group, PROVIDER.get(archive, group, false, 0));

        for (var file : files.keySet()) {
            if (DUMP_CONFIG_IDS) {
                lines.add("// " + file);
            }
            lines.addAll(unpack.apply(file, files.get(file)));
            lines.add("");
        }

        Files.write(result, lines);
    }

    private static void unpackDefaults(int archive, int group, BiFunction<Integer, byte[], List<String>> unpack, Path result) throws IOException {
        var lines = new ArrayList<String>();

        if (archive >= MASTER_INDEX.getArchiveCount() || MASTER_INDEX.getArchiveData(archive).getCrc() == 0) {
            return; // empty archives don't get packed
        }

        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, archive, false, 0)));

        if (Arrays.binarySearch(archiveIndex.groupId, group) < 0) {
            return; // empty groups don't get packed
        }

        var files = Js5Util.unpackGroup(archiveIndex, group, PROVIDER.get(archive, group, false, 0));

        for (var file : files.keySet()) {
            lines.addAll(unpack.apply(file, files.get(file)));
            lines.add("");
        }

        Files.write(result, lines);
    }

    private static void unpackConfigArchive(int archive, int bits, BiFunction<Integer, byte[], List<String>> unpack, Path result) throws IOException {
        if (archive >= MASTER_INDEX.getArchiveCount() || MASTER_INDEX.getArchiveData(archive).getCrc() == 0) {
            return; // empty archives don't get packed
        }

        var lines = new ArrayList<String>();
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, archive, false, 0)));
        var groups = preloadGroups(archive);

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, groups[group]);

            for (var file : files.keySet()) {
                if (DUMP_CONFIG_IDS) {
                    lines.add("// " + ((group << bits) + file));
                }
                lines.addAll(unpack.apply((group << bits) + file, files.get(file)));
                lines.add("");
            }
        }

        Files.write(result, lines);
    }

    private static void unpackInterfaces(int archive, BiFunction<Integer, byte[], List<String>> unpack, Path result) throws IOException {
        var archiveIndex = new Js5ArchiveIndex(Js5Util.decompress(PROVIDER.get(255, archive, false, 0)));
        var groups = preloadGroups(archive);

        for (var group : archiveIndex.groupId) {
            var files = Js5Util.unpackGroup(archiveIndex, group, groups[group]);
            var lines = new ArrayList<String>();
            boolean scripted = false;

            for (var file : files.keySet()) {
                var data = files.get(file);
                scripted |= data[0] == -1;
                if (DUMP_CONFIG_IDS) {
                    lines.add("// " + group + ":" + file);
                }
                lines.addAll(unpack.apply((group << 16) + file, data));
                lines.add("");
            }

            String extension = scripted ? "if3" : "if";
            Files.write(result.resolve(Unpacker.format(Type.INTERFACE, group) + "." + extension), lines);
        }
    }
}
