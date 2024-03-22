package rs3.unpack;

import rs3.Unpack;
import rs3.unpack.script.ScriptUnpacker;
import rs3.util.Tuple2;

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

                var level = value >>> 28;
                var x = value >>> 14 & 16383;
                var z = value & 16383;
                yield level + "_" + (x / 64) + "_" + (z / 64) + "_" + (x % 64) + "_" + (z % 64);
            }

            case TYPE -> Type.byID(value).name;

            case COMPONENT -> {
                if (value == -1) {
                    yield "null";
                }

                yield "interface_" + (value >> 16) + ":com" + (value & 0xffff);
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

                var name = GRAPHIC_NAMES.getOrDefault(value, "graphic_" + value);

                if (name.contains(",")) {
                    name = "\"" + name + "\"";
                }

                yield name;
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

            case VARBIT -> formatVarBit(value);
            case VAR_PLAYER -> formatVar(VarDomain.PLAYER, value);
            case VAR_PLAYER_BIT -> "varplayerbit" + value;
            case VAR_NPC -> formatVar(VarDomain.NPC, value);
            case VAR_NPC_BIT -> "varnpcbit" + value;
            case VAR_CLIENT -> formatVar(VarDomain.CLIENT, value);
            case VAR_CLIENT_STRING -> "varclientstring" + value;
            case VAR_WORLD -> formatVar(VarDomain.WORLD, value);
            case VAR_WORLD_STRING -> "varworldstring" + value;
            case VAR_REGION -> formatVar(VarDomain.REGION, value);
            case VAR_OBJECT -> formatVar(VarDomain.OBJECT, value);
            case VAR_CLAN -> formatVar(VarDomain.CLAN, value);
            case VAR_CLAN_SETTING -> formatVar(VarDomain.CLAN_SETTING, value);
            case VAR_CONTROLLER -> formatVar(VarDomain.CONTROLLER, value);
            case VAR_PLAYER_GROUP -> formatVar(VarDomain.PLAYER_GROUP, value);
            case VAR_GLOBAL -> formatVar(VarDomain.GLOBAL, value);

            case MOVESPEED -> switch (value) {
                case -1 -> "stationary";
                case 0 -> "crawl";
                case 1 -> "walk";
                case 2 -> "run";
                case 3 -> "instant";
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
                case 1 -> "java";
                case 2 -> "android";
                case 3 -> "ios";
                case 4 -> "enhanced_windows";
                case 5 -> "enhanced_mac";
                case 7 -> "enhanced_android";
                case 8 -> "enhanced_ios";
                case 10 -> "enhanced_linux";
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

                var name = getScriptName(value);
                name = name.substring(1, name.length() - 1);
                name = name.split(",")[1];
                yield name;
            }

            case TWITCH_EVENT -> switch (value) {
                case 0 -> "report";
                case 1 -> "result";
                case 2 -> "chat_message";
                case 3 -> "webcam_device_info";
                default -> throw new IllegalArgumentException("" + value);
            };

            case MINIMENU_EVENT -> switch (value) {
                case 0 -> "open";
                case 1 -> "close";
                case 2 -> "click";
                default -> throw new IllegalArgumentException("" + value);
            };

            case INT_INT -> String.valueOf(value);

            case INT_BOOLEAN -> switch (value) {
                case -1 -> "null";
                case 0 -> "^false";
                case 1 -> "^true";
                default -> "" + value;
            };

            case INT_CHATFILTER -> switch (value) {
                case -1 -> "null";
                case 0 -> "^chatfilter_on";
                case 1 -> "^chatfilter_friends";
                case 2 -> "^chatfilter_off";
                case 3 -> "^chatfilter_hide";
                case 4 -> "^chatfilter_autochat";
                default -> "^chatfilter_" + value;
            };

            case INT_CHATTYPE -> switch (value) { // https://twitter.com/TheCrazy0neTv/status/1100567742602756096
                case -1 -> "null";
                case 0 -> "^chattype_gamemessage";
                case 1 -> "^chattype_modchat";
                case 2 -> "^chattype_publicchat";
                case 3 -> "^chattype_privatechat";
                case 4 -> "^chattype_engine";
                case 5 -> "^chattype_loginlogoutnotification";
                case 6 -> "^chattype_privatechatout";
                case 7 -> "^chattype_modprivatechat";
                case 9 -> "^chattype_friendschat";
                case 11 -> "^chattype_friendschatnotification";
                case 14 -> "^chattype_broadcast";
                case 26 -> "^chattype_snapshotfeedback";
                case 27 -> "^chattype_obj_examine";
                case 28 -> "^chattype_npc_examine";
                case 29 -> "^chattype_loc_examine";
                case 30 -> "^chattype_friendnotification";
                case 31 -> "^chattype_ignorenotification";
                case 41 -> "^chattype_clanchat";
                case 43 -> "^chattype_clanmessage";
                case 44 -> "^chattype_clanguestchat";
                case 46 -> "^chattype_clanguestmessage";
                case 90 -> "^chattype_autotyper";
                case 91 -> "^chattype_modautotyper";
                case 99 -> "^chattype_console";
                case 101 -> "^chattype_tradereq";
                case 102 -> "^chattype_trade";
                case 103 -> "^chattype_chalreq_trade";
                case 104 -> "^chattype_chalreq_friendschat";
                case 105 -> "^chattype_spam";
                case 106 -> "^chattype_playerrelated";
                case 107 -> "^chattype_10sectimeout";
                case 109 -> "^chattype_clancreationinvitation";
                case 110 -> "^chattype_chalreq_clanchat";
                case 114 -> "^chattype_dialogue";
                case 115 -> "^chattype_mesbox";
                default -> "^chattype_" + value;
            };

            case INT_PLATFORMTYPE -> switch (value) {
                case -1 -> "null";
                case 0 -> "^platformtype_default";
                case 1 -> "^platformtype_steam";
                case 2 -> "^platformtype_android";
                case 3 -> "^platformtype_apple";
                case 5 -> "^platformtype_jagex";
                default -> "^platformtype_" + value;
            };

            case INT_IFTYPE -> switch (value) {
                case -1 -> "null";
                case 0 -> "^iftype_layer";
                case 3 -> "^iftype_rectangle";
                case 4 -> "^iftype_text";
                case 5 -> "^iftype_graphic";
                case 6 -> "^iftype_model";
                case 9 -> "^iftype_line";
                case 10 -> "^iftype_button";
                case 11 -> "^iftype_panel";
                case 12 -> "^iftype_check";
                case 13 -> "^iftype_input";
                case 14 -> "^iftype_slider";
                case 15 -> "^iftype_grid";
                case 16 -> "^iftype_list";
                case 17 -> "^iftype_combo";
                case 18 -> "^iftype_pagedlayer";
                case 19 -> "^iftype_pagedlayerheader";
                case 20 -> "^iftype_carousel";
                case 21 -> "^iftype_pagedcarousel";
                case 22 -> "^iftype_radiogroup";
                case 23 -> "^iftype_groupbox";
                case 24 -> "^iftype_radialprogressoverlay";
                case 26 -> "^iftype_crmview";
                case 27 -> "^iftype_cutscenelayer";
                case 28 -> "^iftype_modelgroup";
                default -> "^iftype_" + value;
            };

            case INT_KEY -> switch (value) {
                case -1 -> "null";
                case 0 -> "0";
                case 1 -> "^key_f1";
                case 2 -> "^key_f2";
                case 3 -> "^key_f3";
                case 4 -> "^key_f4";
                case 5 -> "^key_f5";
                case 6 -> "^key_f6";
                case 7 -> "^key_f7";
                case 8 -> "^key_f8";
                case 9 -> "^key_f9";
                case 10 -> "^key_f10";
                case 11 -> "^key_f11";
                case 12 -> "^key_f12";
                case 13 -> "^key_escape";
                case 16 -> "^key_1";
                case 17 -> "^key_2";
                case 18 -> "^key_3";
                case 19 -> "^key_4";
                case 20 -> "^key_5";
                case 21 -> "^key_6";
                case 22 -> "^key_7";
                case 23 -> "^key_8";
                case 24 -> "^key_9";
                case 25 -> "^key_0";
                case 26 -> "^key_minus";
                case 27 -> "^key_equals";
                case 28 -> "^key_console";
                case 32 -> "^key_q";
                case 33 -> "^key_w";
                case 34 -> "^key_e";
                case 35 -> "^key_r";
                case 36 -> "^key_t";
                case 37 -> "^key_y";
                case 38 -> "^key_u";
                case 39 -> "^key_i";
                case 40 -> "^key_o";
                case 41 -> "^key_p";
                case 42 -> "^key_left_bracket";
                case 43 -> "^key_right_bracket";
                case 48 -> "^key_a";
                case 49 -> "^key_s";
                case 50 -> "^key_d";
                case 51 -> "^key_f";
                case 52 -> "^key_g";
                case 53 -> "^key_h";
                case 54 -> "^key_j";
                case 55 -> "^key_k";
                case 56 -> "^key_l";
                case 57 -> "^key_semicolon";
                case 58 -> "^key_apostrophe";
                case 59 -> "^key_win_left";
                case 64 -> "^key_z";
                case 65 -> "^key_x";
                case 66 -> "^key_c";
                case 67 -> "^key_v";
                case 68 -> "^key_b";
                case 69 -> "^key_n";
                case 70 -> "^key_m";
                case 71 -> "^key_comma";
                case 72 -> "^key_period";
                case 73 -> "^key_slash";
                case 74 -> "^key_backslash";
                case 80 -> "^key_tab";
                case 81 -> "^key_shift_left";
                case 82 -> "^key_control_left";
                case 83 -> "^key_space";
                case 84 -> "^key_return";
                case 85 -> "^key_backspace";
                case 86 -> "^key_alt_left";
                case 87 -> "^key_numpad_add";
                case 88 -> "^key_numpad_subtract";
                case 89 -> "^key_numpad_multiply";
                case 90 -> "^key_numpad_divide";
                case 91 -> "^key_clear";
                case 96 -> "^key_left";
                case 97 -> "^key_right";
                case 98 -> "^key_up";
                case 99 -> "^key_down";
                case 100 -> "^key_insert";
                case 101 -> "^key_del";
                case 102 -> "^key_home";
                case 103 -> "^key_end";
                case 104 -> "^key_page_up";
                case 105 -> "^key_page_down";
                default -> "^key_" + value;
            };

            case INT_SETPOSH -> switch (value) {
                case -1 -> "null";
                case 0 -> "^setposh_abs_left";
                case 1 -> "^setposh_abs_centre";
                case 2 -> "^setposh_abs_right";
                case 3 -> "^setposh_proportion_left";
                case 4 -> "^setposh_proportion_centre";
                case 5 -> "^setposh_proportion_right";
                default -> "^setposh_" + value;
            };

            case INT_SETPOSV -> switch (value) {
                case -1 -> "null";
                case 0 -> "^setposv_abs_top";
                case 1 -> "^setposv_abs_centre";
                case 2 -> "^setposv_abs_bottom";
                case 3 -> "^setposv_proportion_top";
                case 4 -> "^setposv_proportion_centre";
                case 5 -> "^setposv_proportion_bottom";
                default -> "^setposv_" + value;
            };

            case INT_SETSIZE -> switch (value) {
                case -1 -> "null";
                case 0 -> "^setsize_abs";
                case 1 -> "^setsize_minus";
                case 2 -> "^setsize_proportion";
                case 3 -> "^setsize_3"; // todo
                case 4 -> "^setsize_aspect";
                default -> "^setsize_" + value;
            };

            case INT_SETTEXTALIGNH -> switch (value) {
                case -1 -> "null";
                case 0 -> "^settextalignh_left";
                case 1 -> "^settextalignh_centre";
                case 2 -> "^settextalignh_right";
                case 3 -> "^settextalignh_justified";
                default -> "^settextalignh_" + value;
            };

            case INT_SETTEXTALIGNV -> switch (value) {
                case -1 -> "null";
                case 0 -> "^settextalignv_top";
                case 1 -> "^settextalignv_centre";
                case 2 -> "^settextalignv_bottom";
                default -> "^settextalignv_" + value;
            };

            case INT_WINDOWMODE -> switch (value) { // tfu
                case -1 -> "null";
                case 0 -> "0";
                case 1 -> "^windowmode_small";
                case 2 -> "^windowmode_resizable";
                case 3 -> "^windowmode_fullscreen";
                default -> "^windowmode_" + value;
            };

            case INT_RGB -> switch (value) {
                case -1 -> "null";
                case 0xff0000 -> "^red";
                case 0x00ff00 -> "^green";
                case 0x0000ff -> "^blue";
                case 0xffff00 -> "^yellow";
                case 0xff00ff -> "^magenta";
                case 0x00ffff -> "^cyan";
                case 0xffffff -> "^white";
                case 0x000000 -> "^black";
                default -> "0x" + Integer.toHexString(value);
            };

            case INT_CLIENTOPTION -> switch (value) {
                case -1 -> "null";
                case 0 -> "^clientoption_ambient_occlusion";
                case 1 -> "^clientoption_anisotropic_filtering";
                case 2 -> "^clientoption_antialiasing_mode";
                case 3 -> "^clientoption_antialiasing_quality";
                case 4 -> "^clientoption_bloom";
                case 5 -> "^clientoption_brightness";
                case 6 -> "^clientoption_custom_cursors";
                case 7 -> "^clientoption_dof";
                case 8 -> "^clientoption_draw_distance";
                case 9 -> "^clientoption_ground_blending";
                case 10 -> "^clientoption_ground_decor";
                case 11 -> "^clientoption_interface_scale";
                case 12 -> "^clientoption_lighting_quality";
                case 13 -> "^clientoption_max_foreground_fps";
                case 14 -> "^clientoption_max_background_fps";
                case 15 -> "^clientoption_reflections";
                case 16 -> "^clientoption_remove_roof";
                case 17 -> "^clientoption_remove_roof_override";
                case 22 -> "^clientoption_volume_main_effects";
                case 18 -> "^clientoption_game_render_scale";
                case 23 -> "^clientoption_volume_main_music";
                case 19 -> "^clientoption_shadows";
                case 20 -> "^clientoption_shadow_quality";
                case 21 -> "^clientoption_texturing";
                case 24 -> "^clientoption_volume_background_effects";
                case 25 -> "^clientoption_volume_speech";
                case 26 -> "^clientoption_volume_login_music";
                case 27 -> "^clientoption_volumetric_lighting";
                case 28 -> "^clientoption_v_sync";
                case 30 -> "^clientoption_smooth_clip_fade";
                case 31 -> "^clientoption_canopy_cutout";
                case 36 -> "^clientoption_particle_quality";
                case 29 -> "^clientoption_entity_highlights";
                case 32 -> "^clientoption_diagnostics";
                case 33 -> "^clientoption_rich_presence";
                case 34 -> "^clientoption_language";
                case 35 -> "^clientoption_haptic_feedback";
                case 37 -> "^clientoption_custom_player_model_count";
                case 38 -> "^clientoption_volume_master";
                default -> "^clientoption_" + value;
            };

            case INT_FILTEROP -> switch (value) {
                case -1 -> "null";
                case 1 -> "^filterop_lt";
                case 2 -> "^filterop_lte";
                case 3 -> "^filterop_eq";
                case 4 -> "^filterop_gte";
                case 5 -> "^filterop_gt";
                default -> "^windowmode_" + value;
            };

            default -> {
                if (value == -1) {
                    yield "null";
                }

                yield type.name + "_" + value;
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

                yield type.name + "_" + value;
            }
        };
    }

    public static String format(Type type, String value) {
        return value;
    }

    public static String formatVar(VarDomain domain, int value) {
        if (Unpack.VERSION < 700) {
            return "var" + domain.name().toLowerCase(Locale.ROOT) + value;
        } else {
            return "var" + domain.name().toLowerCase(Locale.ROOT) + getVarType(domain, value).name().toLowerCase(Locale.ROOT) + value;
        }
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
        var type = VAR_TYPE.get(new Tuple2<>(domain, id));

        if (Unpack.VERSION < 700 && type == null) {
            return Type.UNKNOWN_INT; // only varcs have types in older versions
        }

        return Objects.requireNonNull(type);
    }

    public static VarDomain getVarBitDomain(int id) {
        if (Unpack.VERSION < 700) {
            return VarDomain.PLAYER;
        }

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

    public static List<Type> getDBColumnTypeTuple(int column) {
        if (Unpack.VERSION < 930) {
            return getDBColumnTypeTuple(column >>> 8, column & 255, -1);
        } else {
            return getDBColumnTypeTuple(column >>> 12, (column >>> 4) & 255, (column & 15) - 1);
        }
    }

    public static Type getDBColumnTypeTupleAssertSingle(int column) {
        var types = getDBColumnTypeTuple(column);

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
            if (line.startsWith("recolindices=")) {
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

    public static String getScriptName(int id) {
        var name = "[" + (ScriptUnpacker.CLIENTSCRIPT.contains(id) ? "clientscript" : "proc") + ",script" + id + "]";

        if (SCRIPT_NAMES.containsKey(id)) {
            name = SCRIPT_NAMES.get(id);
        }

        return name;
    }
}
