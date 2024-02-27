package rs3.unpack;

import rs3.util.Tuple2;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Unpacker {
    public static final Map<Integer, Type> PARAM_TYPE = new HashMap<>();
    public static final Map<Tuple2<Integer, Integer>, List<Type>> DBCOLUMN_TYPE = new HashMap<>();
    public static final Map<Tuple2<VarDomain, Integer>, Type> VAR_TYPE = new HashMap<>();
    public static final Map<Integer, VarDomain> VARBIT_DOMAIN = new HashMap<>();
    public static final Map<Integer, Type> ENUM_INPUT_TYPE = new HashMap<>();
    public static final Map<Integer, Type> ENUM_OUTPUT_TYPE = new HashMap<>();
    public static final Map<Integer, String> SCRIPT_NAMES = new HashMap<>();
    public static final Map<Integer, String> GRAPHIC_NAMES = new HashMap<>();
    public static final Map<Integer, String> WMA_NAMES = new HashMap<>();
    public static final Map<Integer, String> STYLESHEET_NAMES = new HashMap<>();

    static {
        readNamesTSV(Path.of("data/names/clientscript.tsv"), SCRIPT_NAMES);
        readNamesTSV(Path.of("data/names/graphic.tsv"), GRAPHIC_NAMES);
        readNamesTSV(Path.of("data/names/stylesheet.tsv"), STYLESHEET_NAMES);
    }

    private static void readNamesTSV(Path path, Map<Integer, String> result) {
        try {
            for (var line : Files.readAllLines(path)) {
                var parts = line.split("\t");
                result.put(Integer.parseInt(parts[0]), parts[1]);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String format(Type type, int value) {
        return switch (type) {
            case INT -> String.valueOf(value);

            case BOOLEAN -> switch (value) {
                case -1 -> "null";
                case 0 -> "false";
                case 1 -> "true";
                default -> throw new IllegalArgumentException("invalid boolean");
            };

            case COORDGRID -> {
                if (value == -1) {
                    yield "null";
                }

                var level = value >> 28;
                var x = value >>> 14 & 16383;
                var z = value & 16383;
                yield level + "_" + (x / 64) + "_" + (z / 64) + "_" + (x % 64) + "_" + (z % 64);
            }

            case TYPE -> Type.byID(value).name().toLowerCase(Locale.ROOT);

            case COMPONENT -> {
                if (value == -1) {
                    yield "null";
                }

                yield "interface_" + (value >> 16) + ":" + (value & 0xffff);
            }

            case DBCOLUMN -> {
                var table = value >>> 12;
                var column = (value >>> 4) & 255;
                var tuple = (value & 15) - 1;

                if (tuple == -1) {
                    yield format(Type.DBTABLE, table) + ":" + column;
                } else {
                    yield format(Type.DBTABLE, table) + ":" + column + ":" + tuple;
                }
            }

            case MAPAREA -> {
                if (value == -1) {
                    yield "null";
                }

                yield WMA_NAMES.getOrDefault(value, "maparea_" + value);
            }

            case STYLESHEET -> {
                if (value == -1) {
                    yield "null";
                }

                yield STYLESHEET_NAMES.getOrDefault(value, "stylesheet_" + value);
            }

            case GRAPHIC -> {
                if (value == -1) {
                    yield "null";
                }

                yield GRAPHIC_NAMES.getOrDefault(value, "graphic_" + value);
            }

            case VAR_REFERENCE_INT -> {
                if (value == -1) {
                    yield "null";
                }

                if ((value & 0xff000000) != 0) {
                    yield formatVarBit(value & 0xffff);
                } else {
                    yield formatVar(VarDomain.byID((value >> 16) & 0xff), value & 0xffff);
                }
            }

            case VAR_PLAYER -> formatVar(VarDomain.PLAYER, value);
            case VAR_NPC -> formatVar(VarDomain.NPC, value);
            case VAR_CLIENT -> formatVar(VarDomain.CLIENT, value);
            case VAR_WORLD -> formatVar(VarDomain.WORLD, value);
            case VAR_REGION -> formatVar(VarDomain.REGION, value);
            case VAR_OBJECT -> formatVar(VarDomain.OBJECT, value);
            case VAR_CLAN -> formatVar(VarDomain.CLAN, value);
            case VAR_CLAN_SETTING -> formatVar(VarDomain.CLAN_SETTING, value);
            case VAR_CONTROLLER -> formatVar(VarDomain.CONTROLLER, value);
            case VAR_PLAYER_GROUP -> formatVar(VarDomain.PLAYER_GROUP, value);
            case VAR_GLOBAL -> formatVar(VarDomain.GLOBAL, value);

            case MOVESPEED -> switch (value) {
                case 0 -> "stationary";
                case 1 -> "crawl";
                case 2 -> "walk";
                case 3 -> "run";
                case 4 -> "instant";
                default -> throw new IllegalArgumentException("invalid movespeed");
            };

            case LOC_SHAPE -> switch (value) {
                case 0 -> "1";
                case 1 -> "2";
                case 2 -> "3";
                case 3 -> "4";
                case 4 -> "Q";
                case 5 -> "W";
                case 6 -> "E";
                case 7 -> "R";
                case 8 -> "T";
                case 9 -> "5";
                case 10 -> "8";
                case 11 -> "9";
                case 12 -> "A";
                case 13 -> "S";
                case 14 -> "D";
                case 15 -> "F";
                case 16 -> "G";
                case 17 -> "H";
                case 18 -> "Z";
                case 19 -> "X";
                case 20 -> "C";
                case 21 -> "V";
                case 22 -> "0";
                default -> throw new IllegalArgumentException("" + value);
            };

            case CLIENT_TYPE -> switch (value) {
                case -1 -> "null";
                case 0 -> "java";
                case 1 -> "nxt";
                case 7 -> "android";
                case 8 -> "ios";
                default -> "client_type_" + value;
            };

            case SOCIAL_NETWORK -> switch (value) {
                case -1 -> "null";
                case 0 -> "facebook";
                case 4 -> "google";
                case 5 -> "gamerica";
                case 6 -> "axeso5";
                case 8 -> "apple";
                case 9 -> "jagex";
                case 10 -> "steam";
                default -> "social_network_" + value;
            };

            case STAT -> switch (value) {
                case -1 -> "null";
                case 0 -> "attack";
                case 1 -> "defence";
                case 2 -> "strength";
                case 3 -> "hitpoints";
                case 4 -> "ranged";
                case 5 -> "prayer";
                case 6 -> "magic";
                case 7 -> "cooking";
                case 8 -> "woodcutting";
                case 9 -> "fletching";
                case 10 -> "fishing";
                case 11 -> "firemaking";
                case 12 -> "crafting";
                case 13 -> "smithing";
                case 14 -> "mining";
                case 15 -> "herblore";
                case 16 -> "agility";
                case 17 -> "thieving";
                case 18 -> "slayer";
                case 19 -> "farming";
                case 20 -> "runecraft";
                case 21 -> "hunter";
                case 22 -> "construction";
                case 23 -> "summoning";
                case 24 -> "dungeoneering";
                case 25 -> "divination";
                case 26 -> "invention";
                case 27 -> "archaeology";
                case 28 -> "necromancy";
                default -> "stat_" + value;
            };

            case NPC_STAT -> switch (value) {
                case -1 -> "null";
                case 0 -> "attack";
                case 1 -> "defence";
                case 2 -> "strength";
                case 3 -> "hitpoints";
                case 4 -> "ranged";
                case 5 -> "magic";
                case 6 -> "necromancy";
                default -> "npc_stat_" + value;
            };

            case CLIENTSCRIPT -> {
                if (value == -1) {
                    yield "null";
                }

                var name = SCRIPT_NAMES.get(value);

                if (name == null) {
                    yield "script" + value;
                }

                name = name.substring(1, name.length() - 1);
                name = name.split(",")[1];
                yield name;
            }

            default -> {
                if (value == -1) {
                    yield "null";
                }

                yield type.name().toLowerCase(Locale.ROOT) + "_" + value;
            }
        };
    }

    public static String format(Type type, long value) {
        return switch (type) {
            case LONG -> value + "L";

            default -> {
                if (value == -1) {
                    yield "null";
                }

                yield type.name().toLowerCase(Locale.ROOT) + "_" + value;
            }
        };
    }

    public static String format(Type type, String value) {
        return value;
    }

    public static String formatTypeChar(int value) {
        return Type.byChar(value).name().toLowerCase(Locale.ROOT);
    }

    public static String formatVar(VarDomain domain, int value) {
        return "var" + domain.name().toLowerCase(Locale.ROOT) + getVarType(domain, value).name().toLowerCase(Locale.ROOT) + value;
    }

    public static String formatVarBit(int value) {
        return "var" + getVarBitDomain(value).name().toLowerCase(Locale.ROOT) + "bit" + value;
    }

    public static String formatVarDomain(int value) {
        return VarDomain.byID(value).name().toLowerCase(Locale.ROOT);
    }

    public static String formatBoolean(int value) {
        return switch (value) {
            case 0 -> "no";
            case 1 -> "yes";
            default -> throw new IllegalArgumentException("invalid boolean");
        };
    }

    public static String formatReplaceMode(int value) {
        return switch (value) {
            case 0 -> "ignore"; // continues the current animation
            case 1 -> "reset"; // resets frame and loop counter
            case 2 -> "extend"; // resets loop counter only
            default -> throw new IllegalArgumentException("" + value);
        };
    }

    public static String formatTransmitLevel(int value) {
        return switch (value) {
            case 0 -> "never";
            case 1 -> "on_set_different";
            case 2 -> "on_set_always";
            default -> throw new IllegalStateException("invalid transmitlevel");
        };
    }

    public static String formatVarLifetime(int value) {
        return switch (value) {
            case 0 -> "temp"; // https://twitter.com/JagexAsh/status/654366476674183168
            case 1 -> "perm"; // https://twitter.com/JagexAsh/status/654366476674183168
            case 2 -> "serverperm";
            default -> throw new IllegalStateException("invalid lifetime");
        };
    }

    public static String formatPreAnimMove(int value) {
        return switch (value) {
            case 0 -> "delaymove";
            case 1 -> "delayanim";
            case 2 -> "merge";
            case 3 -> "unknown_3";
            default -> throw new IllegalStateException("invalid preanim_move");
        };
    }

    public static String formatPostAnimMove(int value) {
        return switch (value) {
            case 0 -> "delaymove";
            case 1 -> "abortanim";
            case 2 -> "merge";
            default -> throw new IllegalStateException("invalid postanim_move");
        };
    }

    public static void setVarType(VarDomain domain, int id, Type type) {
        VAR_TYPE.put(new Tuple2<>(domain, id), type);
    }

    public static Type getVarType(VarDomain domain, int id) {
        return Objects.requireNonNull(VAR_TYPE.get(new Tuple2<>(domain, id)));
    }

    public static VarDomain getVarBitDomain(int id) {
        return VARBIT_DOMAIN.get(id);
    }

    public static void setVarBitDomain(int id, VarDomain domain) {
        VARBIT_DOMAIN.put(id, domain);
    }

    public static void setParamType(int id, Type type) {
        PARAM_TYPE.put(id, type);
    }

    public static Type getParamType(int operand) {
        return Objects.requireNonNull(PARAM_TYPE.get(operand));
    }

    public static void setDBColumnType(int table, int column, List<Type> types) {
        DBCOLUMN_TYPE.put(new Tuple2<>(table, column), types);
    }

    private static List<Type> getDBColumnType(int table, int column) {
        if (table == 85 && column == 13) return List.of(Type.NPC);
        if (table == 88 && column == 0) return List.of(Type.INT);
        if (table == 119 && column == 0) return List.of(Type.INT, Type.INT);
        if (table == 164 && column == 5) return List.of(Type.COMPONENT);
        if (table == 199 && column == 6) return List.of(Type.BOOLEAN);
        if (table == 199 && column == 7) return List.of(Type.BOOLEAN);
        if (table == 199 && column == 8) return List.of(Type.INT);
        if (table == 199 && column == 9) return List.of(Type.BOOLEAN);
        if (table == 267 && column == 0) return List.of(Type.INT); // todo
        if (table == 268 && column == 0) return List.of(Type.INT); // todo
        if (table == 268 && column == 2) return List.of(Type.INT); // todo

//        if (table == 268 && column == 2) return List.of(Type.STRING); // todo beta
//        if (table == 267 && column == 0) return List.of(Type.STRING); // todo beta
//        if (table == 269 && column == 0) return List.of(Type.INT); // todo beta
//        if (table == 270 && column == 0) return List.of(Type.INT); // todo beta
//        if (table == 270 && column == 2) return List.of(Type.INT); // todo beta

        return Objects.requireNonNull(DBCOLUMN_TYPE.get(new Tuple2<>(table, column)));
    }

    public static List<Type> getDBColumnTypeTuple(int table, int column, int tuple) {
        var types = getDBColumnType(table, column);

        if (tuple == -1) {
            return types;
        } else {
            return List.of(types.get(tuple));
        }
    }

    public static Type getDBColumnTypeTupleAssertSingle(int table, int column, int tuple) {
        var types = getDBColumnTypeTuple(table, column, tuple);

        if (types.size() != 1) {
            throw new IllegalStateException("required single type, got " + types.size());
        }

        return types.get(0);
    }

    public static void setEnumInputType(int id, Type type) {
        ENUM_INPUT_TYPE.put(id, type);
    }

    public static Type getEnumInputType(int id) {
        return Objects.requireNonNull(ENUM_INPUT_TYPE.get(id));
    }

    public static void setEnumOutputType(int id, Type type) {
        ENUM_OUTPUT_TYPE.put(id, type);
    }

    public static Type getEnumOutputType(int id) {
        return Objects.requireNonNull(ENUM_OUTPUT_TYPE.get(id));
    }

    public static void setWorldMapAreaName(int id, String debugname) {
        WMA_NAMES.put(id, debugname);
    }

    public static String formatIfType(int type) {
        return switch (type) {
            case 0 -> "layer";
            case 3 -> "rectangle";
            case 4 -> "text";
            case 5 -> "graphic";
            case 6 -> "model";
            case 9 -> "line";
            case 10 -> "button";
            case 11 -> "panel";
            case 12 -> "check";
            case 13 -> "input";
            case 14 -> "slider";
            case 15 -> "grid";
            case 16 -> "list";
            case 17 -> "combo";
            case 18 -> "pagedlayer";
            case 19 -> "pagedlayerheader";
            case 20 -> "carousel";
            case 21 -> "pagedcarousel";
            case 22 -> "radiogroup";
            case 23 -> "groupbox";
            case 24 -> "radialprogressoverlay";
            case 26 -> "crmview";
            case 27 -> "cutscenelayer";
            case 28 -> "modelgroup";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public static String formatColour(int colour) {
        var hex = Integer.toHexString(colour);

        if (hex.length() > 6) {
            return "0x" + "0".repeat(8 - hex.length()) + hex;
        } else {
            return "0x" + "0".repeat(6 - hex.length()) + hex;
        }
    }

    public static String formatWearPos(int slot) {
        return switch (slot) {
            case 0 -> "hat";
            case 1 -> "back";
            case 2 -> "front";
            case 3 -> "righthand";
            case 4 -> "torso";
            case 5 -> "lefthand";
            case 6 -> "arms";
            case 7 -> "legs";
            case 8 -> "head";
            case 9 -> "hands";
            case 10 -> "feet";
            case 11 -> "jaw";
            case 12 -> "ring";
            case 13 -> "quiver";
            case 14 -> "aura";
            case 15 -> "wearpos_15";
            case 16 -> "wearpos_16";
            case 17 -> "pocket";
            case 18 -> "wings";
            default -> throw new IllegalArgumentException("wearpos " + slot);
        };
    }

    public static String formatRecolRetexIndexList(int value) {
        var list = new ArrayList<Integer>();

        for (var i = 0; i < 16; i++) {
            if ((value & (1 << i)) != 0) {
                list.add(i + 1);
            }
        }

        return list.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    public static ArrayList<String> transformRecolRetexIndices(ArrayList<String> lines) {
        var recolIndices = (String[]) null;
        var retexIndices = (String[]) null;

        for (var line : lines) {
            if (line.startsWith("recolIndices=")) {
                recolIndices = line.split("=")[1].split(",");
            }

            if (line.startsWith("retexindices=")) {
                retexIndices = line.split("=")[1].split(",");
            }
        }

        if (recolIndices != null || retexIndices != null) {
            var newLines = new ArrayList<String>();

            for (var line : lines) {
                if (line.startsWith("recolindices")) {
                    continue;
                } else if (line.startsWith("retexindices")) {
                    continue;
                } else if (recolIndices != null && line.startsWith("recol")) {
                    var equal = line.indexOf('=');
                    var newIndex = recolIndices[Integer.parseInt(line.substring(5, equal - 1)) - 1];
                    line = line.substring(0, 5) + newIndex + line.substring(equal - 1);
                } else if (retexIndices != null && line.startsWith("retex")) {
                    var equal = line.indexOf('=');
                    var newIndex = retexIndices[Integer.parseInt(line.substring(5, equal - 1)) - 1];
                    line = line.substring(0, 5) + newIndex + line.substring(equal - 1);
                }

                newLines.add(line);
            }

            lines = newLines;
        }

        return lines;
    }
}
