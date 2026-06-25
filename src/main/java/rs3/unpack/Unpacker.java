package rs3.unpack;

import rs3.Unpack;
import rs3.unpack.script.ScriptUnpacker;
import rs3.util.CP1252;
import rs3.util.Packet;
import rs3.util.Tuple2;

import java.util.*;

public class Unpacker {
    public static final Map<Type, Map<Integer, String>> NAME = new HashMap<>();
    public static final Map<Integer, String> SCRIPT_NAME = new HashMap<>();
    public static final Map<Integer, String> BINARY_NAME = new HashMap<>();
    public static final Map<Integer, Map<Integer, List<Type>>> DBCOLUMN_TYPE = new HashMap<>();
    public static final Map<Integer, Type> PARAM_TYPE = new HashMap<>();
    public static final Map<Integer, Type> ENUM_INPUT_TYPE = new HashMap<>();
    public static final Map<Integer, Type> ENUM_OUTPUT_TYPE = new HashMap<>();
    public static final Map<Tuple2<VarDomain, Integer>, Type> VAR_TYPE = new HashMap<>();
    public static final Map<Integer, VarDomain> VARBIT_DOMAIN = new HashMap<>();

    public static void reset() {
        NAME.clear();
        SCRIPT_NAME.clear();
        BINARY_NAME.clear();
        DBCOLUMN_TYPE.clear();
        PARAM_TYPE.clear();
        ENUM_INPUT_TYPE.clear();
        ENUM_OUTPUT_TYPE.clear();
        VAR_TYPE.clear();
        VARBIT_DOMAIN.clear();

        setSymbolName(Type.BOOLEAN, 0, "false");
        setSymbolName(Type.BOOLEAN, 1, "true");

        setSymbolName(Type.LOC_SHAPE, 0, "1");
        setSymbolName(Type.LOC_SHAPE, 1, "2");
        setSymbolName(Type.LOC_SHAPE, 2, "3");
        setSymbolName(Type.LOC_SHAPE, 3, "4");
        setSymbolName(Type.LOC_SHAPE, 4, "Q");
        setSymbolName(Type.LOC_SHAPE, 5, "W");
        setSymbolName(Type.LOC_SHAPE, 6, "E");
        setSymbolName(Type.LOC_SHAPE, 7, "R");
        setSymbolName(Type.LOC_SHAPE, 8, "T");
        setSymbolName(Type.LOC_SHAPE, 9, "5");
        setSymbolName(Type.LOC_SHAPE, 10, "8");
        setSymbolName(Type.LOC_SHAPE, 11, "9");
        setSymbolName(Type.LOC_SHAPE, 12, "A");
        setSymbolName(Type.LOC_SHAPE, 13, "S");
        setSymbolName(Type.LOC_SHAPE, 14, "D");
        setSymbolName(Type.LOC_SHAPE, 15, "F");
        setSymbolName(Type.LOC_SHAPE, 16, "G");
        setSymbolName(Type.LOC_SHAPE, 17, "H");
        setSymbolName(Type.LOC_SHAPE, 18, "Z");
        setSymbolName(Type.LOC_SHAPE, 19, "X");
        setSymbolName(Type.LOC_SHAPE, 20, "C");
        setSymbolName(Type.LOC_SHAPE, 21, "V");
        setSymbolName(Type.LOC_SHAPE, 22, "0");

        setSymbolName(Type.STAT, 0, "attack");
        setSymbolName(Type.STAT, 1, "defence");
        setSymbolName(Type.STAT, 2, "strength");
        setSymbolName(Type.STAT, 3, "hitpoints");
        setSymbolName(Type.STAT, 4, "ranged");
        setSymbolName(Type.STAT, 5, "prayer");
        setSymbolName(Type.STAT, 6, "magic");
        setSymbolName(Type.STAT, 7, "cooking");
        setSymbolName(Type.STAT, 8, "woodcutting");
        setSymbolName(Type.STAT, 9, "fletching");
        setSymbolName(Type.STAT, 10, "fishing");
        setSymbolName(Type.STAT, 11, "firemaking");
        setSymbolName(Type.STAT, 12, "crafting");
        setSymbolName(Type.STAT, 13, "smithing");
        setSymbolName(Type.STAT, 14, "mining");
        setSymbolName(Type.STAT, 15, "herblore");
        setSymbolName(Type.STAT, 16, "agility");
        setSymbolName(Type.STAT, 17, "thieving");
        setSymbolName(Type.STAT, 18, "slayer");
        setSymbolName(Type.STAT, 19, "farming");
        setSymbolName(Type.STAT, 20, "runecraft");
        setSymbolName(Type.STAT, 21, "hunter");
        setSymbolName(Type.STAT, 22, "construction");
        setSymbolName(Type.STAT, 23, "summoning");
        setSymbolName(Type.STAT, 24, "dungeoneering");
        setSymbolName(Type.STAT, 25, "divination");
        setSymbolName(Type.STAT, 26, "invention");
        setSymbolName(Type.STAT, 27, "archaeology");
        setSymbolName(Type.STAT, 28, "necromancy");

        setSymbolName(Type.NPC_STAT, 0, "attack");
        setSymbolName(Type.NPC_STAT, 1, "defence");
        setSymbolName(Type.NPC_STAT, 2, "strength");
        setSymbolName(Type.NPC_STAT, 3, "hitpoints");
        setSymbolName(Type.NPC_STAT, 4, "ranged");
        setSymbolName(Type.NPC_STAT, 5, "magic");
        setSymbolName(Type.NPC_STAT, 6, "necromancy");

        setSymbolName(Type.CLIENT_TYPE, 1, "java");
        setSymbolName(Type.CLIENT_TYPE, 2, "android");
        setSymbolName(Type.CLIENT_TYPE, 3, "ios");
        setSymbolName(Type.CLIENT_TYPE, 4, "enhanced_windows");
        setSymbolName(Type.CLIENT_TYPE, 5, "enhanced_mac");
        setSymbolName(Type.CLIENT_TYPE, 7, "enhanced_android");
        setSymbolName(Type.CLIENT_TYPE, 8, "enhanced_ios");
        setSymbolName(Type.CLIENT_TYPE, 10, "enhanced_linux");

        setSymbolName(Type.SOCIAL_NETWORK, 0, "facebook");
        setSymbolName(Type.SOCIAL_NETWORK, 4, "google");
        setSymbolName(Type.SOCIAL_NETWORK, 5, "gamerica");
        setSymbolName(Type.SOCIAL_NETWORK, 6, "axeso5");
        setSymbolName(Type.SOCIAL_NETWORK, 8, "apple");
        setSymbolName(Type.SOCIAL_NETWORK, 9, "jagex");
        setSymbolName(Type.SOCIAL_NETWORK, 10, "steam");

        setSymbolName(Type.TWITCH_EVENT, 0, "report");
        setSymbolName(Type.TWITCH_EVENT, 1, "result");
        setSymbolName(Type.TWITCH_EVENT, 2, "chat_message");
        setSymbolName(Type.TWITCH_EVENT, 3, "webcam_device_info");

        setSymbolName(Type.MINIMENU_EVENT, 0, "open");
        setSymbolName(Type.MINIMENU_EVENT, 1, "close");
        setSymbolName(Type.MINIMENU_EVENT, 2, "click");

        setSymbolName(Type.INT_INT, Integer.MIN_VALUE, "^min_32bit_int");
        setSymbolName(Type.INT_INT, Integer.MAX_VALUE, "^max_32bit_int");

        setSymbolName(Type.INT_BOOLEAN, 0, "^false");
        setSymbolName(Type.INT_BOOLEAN, 1, "^true");

        setSymbolName(Type.INT_RGB, 0xff0000, "^red");
        setSymbolName(Type.INT_RGB, 0x00ff00, "^green");
        setSymbolName(Type.INT_RGB, 0x0000ff, "^blue");
        setSymbolName(Type.INT_RGB, 0xffff00, "^yellow");
        setSymbolName(Type.INT_RGB, 0xff00ff, "^magenta");
        setSymbolName(Type.INT_RGB, 0x00ffff, "^cyan");
        setSymbolName(Type.INT_RGB, 0xffffff, "^white");
        setSymbolName(Type.INT_RGB, 0x000000, "^black");

        setSymbolName(Type.INT_CHATFILTER, 0, "^chatfilter_on");
        setSymbolName(Type.INT_CHATFILTER, 1, "^chatfilter_friends");
        setSymbolName(Type.INT_CHATFILTER, 2, "^chatfilter_off");
        setSymbolName(Type.INT_CHATFILTER, 3, "^chatfilter_hide");
        setSymbolName(Type.INT_CHATFILTER, 4, "^chatfilter_autochat");

        // https://twitter.com/TheCrazy0neTv/status/1100567742602756096
        setSymbolName(Type.INT_CHATTYPE, 0, "^chattype_gamemessage"); // GENERAL
        setSymbolName(Type.INT_CHATTYPE, 1, "^chattype_modchat"); // MOD
        setSymbolName(Type.INT_CHATTYPE, 2, "^chattype_publicchat"); // PUBLIC
        setSymbolName(Type.INT_CHATTYPE, 3, "^chattype_privatechat"); // PRIVATE_INCOMING
        setSymbolName(Type.INT_CHATTYPE, 4, "^chattype_engine"); // ENGINE
        setSymbolName(Type.INT_CHATTYPE, 5, "^chattype_loginlogoutnotification"); // LOGIN_LOGOUT
        setSymbolName(Type.INT_CHATTYPE, 6, "^chattype_privatechatout"); // PRIVATE_OUTGOING
        setSymbolName(Type.INT_CHATTYPE, 7, "^chattype_modprivatechat"); // MOD_PRIVATE_INCOMING
        setSymbolName(Type.INT_CHATTYPE, 9, "^chattype_friendschat"); // FRIEND_CHAT
        setSymbolName(Type.INT_CHATTYPE, 11, "^chattype_friendschatnotification"); // FRIEND_CHAT_NOTIFICATION
        setSymbolName(Type.INT_CHATTYPE, 17, "^chattype_quickchat_public"); // QUICKCHAT_PUBLIC
        setSymbolName(Type.INT_CHATTYPE, 18, "^chattype_quickchat_private_incoming"); // QUICKCHAT_PRIVATE_INCOMING
        setSymbolName(Type.INT_CHATTYPE, 19, "^chattype_quickchat_private_outgoing"); // QUICKCHAT_PRIVATE_OUTGOING
        setSymbolName(Type.INT_CHATTYPE, 20, "^chattype_quickchat_friend_chat"); // QUICKCHAT_FRIEND_CHAT
        setSymbolName(Type.INT_CHATTYPE, 22, "^chattype_group_same_team"); // GROUP_SAME_TEAM
        setSymbolName(Type.INT_CHATTYPE, 23, "^chattype_quickchat_group_same_team"); // QUICKCHAT_GROUP_SAME_TEAM
        setSymbolName(Type.INT_CHATTYPE, 24, "^chattype_group_all"); // GROUP_ALL
        setSymbolName(Type.INT_CHATTYPE, 25, "^chattype_quickchat_group_all"); // QUICKCHAT_GROUP_ALL
        setSymbolName(Type.INT_CHATTYPE, 26, "^chattype_snapshotfeedback"); // SNAPSHOT_FEEDBACK
        setSymbolName(Type.INT_CHATTYPE, 27, "^chattype_obj_examine"); // OBJ_EXAMINE
        setSymbolName(Type.INT_CHATTYPE, 28, "^chattype_npc_examine"); // NPC_EXAMINE
        setSymbolName(Type.INT_CHATTYPE, 29, "^chattype_loc_examine"); // LOC_EXAMINE
        setSymbolName(Type.INT_CHATTYPE, 30, "^chattype_friendnotification"); // FRIEND_NOTIFICATION
        setSymbolName(Type.INT_CHATTYPE, 31, "^chattype_ignorenotification"); // IGNORE_NOTIFICATION
        setSymbolName(Type.INT_CHATTYPE, 41, "^chattype_clanchat"); // CLANCHANNEL_AFFINED_CHAT
        setSymbolName(Type.INT_CHATTYPE, 42, "^chattype_quickchat_clanchat"); // CLANCHANNEL_AFFINED_QUICKCHAT
        setSymbolName(Type.INT_CHATTYPE, 43, "^chattype_clanmessage"); // CLANCHANNEL_AFFINED_NOTIFICATION
        setSymbolName(Type.INT_CHATTYPE, 44, "^chattype_clanguestchat"); // CLANCHANNEL_GUEST_CHAT
        setSymbolName(Type.INT_CHATTYPE, 45, "^chattype_quickchat_clanguestchat"); // CLANCHANNEL_GUEST_QUICKCHAT
        setSymbolName(Type.INT_CHATTYPE, 46, "^chattype_clanguestmessage"); // CLANCHANNEL_GUEST_NOTIFICATION
        setSymbolName(Type.INT_CHATTYPE, 96, "^chattype_console_error"); // CONSOLE_ERROR
        setSymbolName(Type.INT_CHATTYPE, 98, "^chattype_console_buffer_replace"); // CONSOLE_BUFFER_REPLACE
        setSymbolName(Type.INT_CHATTYPE, 99, "^chattype_console"); // CONSOLE
        setSymbolName(Type.INT_CHATTYPE, 100, "^chattype_tradereq"); // TRADE_REQUEST
        setSymbolName(Type.INT_CHATTYPE, 101, "^chattype_challenge"); // CHALLENGE
        setSymbolName(Type.INT_CHATTYPE, 102, "^chattype_assistreq"); // ASSIST_REQUEST
        setSymbolName(Type.INT_CHATTYPE, 103, "^chattype_trade_notification"); // TRADE_NOTIFICATION
        setSymbolName(Type.INT_CHATTYPE, 104, "^chattype_assist_notification"); // ASSIST_NOTIFICATION
        setSymbolName(Type.INT_CHATTYPE, 107, "^chattype_clan_challenge"); // CLAN_CHALLENGE
        setSymbolName(Type.INT_CHATTYPE, 108, "^chattype_ally_request"); // ALLY_REQUEST
        setSymbolName(Type.INT_CHATTYPE, 109, "^chattype_spam"); // SPAM
        setSymbolName(Type.INT_CHATTYPE, 111, "^chattype_dungeon_invite"); // DUNGEON_INVITE
        setSymbolName(Type.INT_CHATTYPE, 113, "^chattype_conquest_regular_challenge"); // CONQUEST_REGULAR_CHALLENGE
        setSymbolName(Type.INT_CHATTYPE, 115, "^chattype_system_broadcast"); // SYSTEM_BROADCAST
        setSymbolName(Type.INT_CHATTYPE, 116, "^chattype_npc_chat"); // NPC_CHAT
        setSymbolName(Type.INT_CHATTYPE, 117, "^chattype_clan_join"); // CLAN_JOIN
        setSymbolName(Type.INT_CHATTYPE, 118, "^chattype_rated_clan_war"); // RATED_CLAN_WAR
        setSymbolName(Type.INT_CHATTYPE, 119, "^chattype_ignorable_trade_notification"); // IGNORABLE_TRADE_NOTIFICATION
        setSymbolName(Type.INT_CHATTYPE, 120, "^chattype_all_panes"); // IGNORABLE_TRADE_NOTIFICATION
        setSymbolName(Type.INT_CHATTYPE, 122, "^chattype_raid_high"); // RAID_HIGH
        setSymbolName(Type.INT_CHATTYPE, 123, "^chattype_raid_dnd"); // RAID_DND
        setSymbolName(Type.INT_CHATTYPE, 125, "^chattype_broadcast"); // BROADCAST
        setSymbolName(Type.INT_CHATTYPE, 132, "^chattype_duel_any_request"); // DUEL_ANY_REQUEST
        setSymbolName(Type.INT_CHATTYPE, 133, "^chattype_temporary_event"); // TEMPORARY_EVENT
        setSymbolName(Type.INT_CHATTYPE, 134, "^chattype_hati_fenrir"); // HATI_FENRIR
        setSymbolName(Type.INT_CHATTYPE, 135, "^chattype_latest_newspost"); // LATEST_NEWSPOST
        setSymbolName(Type.INT_CHATTYPE, 136, "^chattype_motd"); // MOTD
        setSymbolName(Type.INT_CHATTYPE, 137, "^chattype_broadcast_global"); // BROADCAST_GLOBAL
        setSymbolName(Type.INT_CHATTYPE, 138, "^chattype_broadcast_world"); // BROADCAST_WORLD
        setSymbolName(Type.INT_CHATTYPE, 139, "^chattype_broadcast_friends"); // BROADCAST_FRIENDS
        setSymbolName(Type.INT_CHATTYPE, 140, "^chattype_interface_open"); // INTERFACE_OPEN
        setSymbolName(Type.INT_CHATTYPE, 142, "^chattype_timed_event"); // TIMED_EVENT
        setSymbolName(Type.INT_CHATTYPE, 143, "^chattype_interface_open_filtered"); // INTERFACE_OPEN_FILTERED
        setSymbolName(Type.INT_CHATTYPE, 144, "^chattype_group_ironman_notification"); // GROUP_IRONMAN_NOTIFICATION
        setSymbolName(Type.INT_CHATTYPE, 145, "^chattype_general_linkable"); // GENERAL_LINKABLE

        setSymbolName(Type.INT_PLATFORMTYPE, 0, "^platformtype_default");
        setSymbolName(Type.INT_PLATFORMTYPE, 1, "^platformtype_steam");
        setSymbolName(Type.INT_PLATFORMTYPE, 2, "^platformtype_android");
        setSymbolName(Type.INT_PLATFORMTYPE, 3, "^platformtype_apple");
        setSymbolName(Type.INT_PLATFORMTYPE, 5, "^platformtype_jagex");

        setSymbolName(Type.INT_IFTYPE, 0, "^iftype_layer");
        setSymbolName(Type.INT_IFTYPE, 3, "^iftype_rectangle");
        setSymbolName(Type.INT_IFTYPE, 4, "^iftype_text");
        setSymbolName(Type.INT_IFTYPE, 5, "^iftype_graphic");
        setSymbolName(Type.INT_IFTYPE, 6, "^iftype_model");
        setSymbolName(Type.INT_IFTYPE, 9, "^iftype_line");
        setSymbolName(Type.INT_IFTYPE, 10, "^iftype_button");
        setSymbolName(Type.INT_IFTYPE, 11, "^iftype_panel");
        setSymbolName(Type.INT_IFTYPE, 12, "^iftype_check");
        setSymbolName(Type.INT_IFTYPE, 13, "^iftype_input");
        setSymbolName(Type.INT_IFTYPE, 14, "^iftype_slider");
        setSymbolName(Type.INT_IFTYPE, 15, "^iftype_grid");
        setSymbolName(Type.INT_IFTYPE, 16, "^iftype_list");
        setSymbolName(Type.INT_IFTYPE, 17, "^iftype_combo");
        setSymbolName(Type.INT_IFTYPE, 18, "^iftype_pagedlayer");
        setSymbolName(Type.INT_IFTYPE, 19, "^iftype_pagedlayerheader");
        setSymbolName(Type.INT_IFTYPE, 20, "^iftype_carousel");
        setSymbolName(Type.INT_IFTYPE, 21, "^iftype_pagedcarousel");
        setSymbolName(Type.INT_IFTYPE, 22, "^iftype_radiogroup");
        setSymbolName(Type.INT_IFTYPE, 23, "^iftype_groupbox");
        setSymbolName(Type.INT_IFTYPE, 24, "^iftype_radialprogressoverlay");
        setSymbolName(Type.INT_IFTYPE, 26, "^iftype_crmview");
        setSymbolName(Type.INT_IFTYPE, 27, "^iftype_cutscenelayer");
        setSymbolName(Type.INT_IFTYPE, 28, "^iftype_modelgroup");

        setSymbolName(Type.INT_KEY, 0, "0");
        setSymbolName(Type.INT_KEY, 1, "^key_f1");
        setSymbolName(Type.INT_KEY, 2, "^key_f2");
        setSymbolName(Type.INT_KEY, 3, "^key_f3");
        setSymbolName(Type.INT_KEY, 4, "^key_f4");
        setSymbolName(Type.INT_KEY, 5, "^key_f5");
        setSymbolName(Type.INT_KEY, 6, "^key_f6");
        setSymbolName(Type.INT_KEY, 7, "^key_f7");
        setSymbolName(Type.INT_KEY, 8, "^key_f8");
        setSymbolName(Type.INT_KEY, 9, "^key_f9");
        setSymbolName(Type.INT_KEY, 10, "^key_f10");
        setSymbolName(Type.INT_KEY, 11, "^key_f11");
        setSymbolName(Type.INT_KEY, 12, "^key_f12");
        setSymbolName(Type.INT_KEY, 13, "^key_escape");
        setSymbolName(Type.INT_KEY, 16, "^key_1");
        setSymbolName(Type.INT_KEY, 17, "^key_2");
        setSymbolName(Type.INT_KEY, 18, "^key_3");
        setSymbolName(Type.INT_KEY, 19, "^key_4");
        setSymbolName(Type.INT_KEY, 20, "^key_5");
        setSymbolName(Type.INT_KEY, 21, "^key_6");
        setSymbolName(Type.INT_KEY, 22, "^key_7");
        setSymbolName(Type.INT_KEY, 23, "^key_8");
        setSymbolName(Type.INT_KEY, 24, "^key_9");
        setSymbolName(Type.INT_KEY, 25, "^key_0");
        setSymbolName(Type.INT_KEY, 26, "^key_minus");
        setSymbolName(Type.INT_KEY, 27, "^key_equals");
        setSymbolName(Type.INT_KEY, 28, "^key_console");
        setSymbolName(Type.INT_KEY, 32, "^key_q");
        setSymbolName(Type.INT_KEY, 33, "^key_w");
        setSymbolName(Type.INT_KEY, 34, "^key_e");
        setSymbolName(Type.INT_KEY, 35, "^key_r");
        setSymbolName(Type.INT_KEY, 36, "^key_t");
        setSymbolName(Type.INT_KEY, 37, "^key_y");
        setSymbolName(Type.INT_KEY, 38, "^key_u");
        setSymbolName(Type.INT_KEY, 39, "^key_i");
        setSymbolName(Type.INT_KEY, 40, "^key_o");
        setSymbolName(Type.INT_KEY, 41, "^key_p");
        setSymbolName(Type.INT_KEY, 42, "^key_left_bracket");
        setSymbolName(Type.INT_KEY, 43, "^key_right_bracket");
        setSymbolName(Type.INT_KEY, 48, "^key_a");
        setSymbolName(Type.INT_KEY, 49, "^key_s");
        setSymbolName(Type.INT_KEY, 50, "^key_d");
        setSymbolName(Type.INT_KEY, 51, "^key_f");
        setSymbolName(Type.INT_KEY, 52, "^key_g");
        setSymbolName(Type.INT_KEY, 53, "^key_h");
        setSymbolName(Type.INT_KEY, 54, "^key_j");
        setSymbolName(Type.INT_KEY, 55, "^key_k");
        setSymbolName(Type.INT_KEY, 56, "^key_l");
        setSymbolName(Type.INT_KEY, 57, "^key_semicolon");
        setSymbolName(Type.INT_KEY, 58, "^key_apostrophe");
        setSymbolName(Type.INT_KEY, 59, "^key_win_left");
        setSymbolName(Type.INT_KEY, 64, "^key_z");
        setSymbolName(Type.INT_KEY, 65, "^key_x");
        setSymbolName(Type.INT_KEY, 66, "^key_c");
        setSymbolName(Type.INT_KEY, 67, "^key_v");
        setSymbolName(Type.INT_KEY, 68, "^key_b");
        setSymbolName(Type.INT_KEY, 69, "^key_n");
        setSymbolName(Type.INT_KEY, 70, "^key_m");
        setSymbolName(Type.INT_KEY, 71, "^key_comma");
        setSymbolName(Type.INT_KEY, 72, "^key_period");
        setSymbolName(Type.INT_KEY, 73, "^key_slash");
        setSymbolName(Type.INT_KEY, 74, "^key_backslash");
        setSymbolName(Type.INT_KEY, 80, "^key_tab");
        setSymbolName(Type.INT_KEY, 81, "^key_shift_left");
        setSymbolName(Type.INT_KEY, 82, "^key_control_left");
        setSymbolName(Type.INT_KEY, 83, "^key_space");
        setSymbolName(Type.INT_KEY, 84, "^key_return");
        setSymbolName(Type.INT_KEY, 85, "^key_backspace");
        setSymbolName(Type.INT_KEY, 86, "^key_alt_left");
        setSymbolName(Type.INT_KEY, 87, "^key_numpad_add");
        setSymbolName(Type.INT_KEY, 88, "^key_numpad_subtract");
        setSymbolName(Type.INT_KEY, 89, "^key_numpad_multiply");
        setSymbolName(Type.INT_KEY, 90, "^key_numpad_divide");
        setSymbolName(Type.INT_KEY, 91, "^key_clear");
        setSymbolName(Type.INT_KEY, 96, "^key_left");
        setSymbolName(Type.INT_KEY, 97, "^key_right");
        setSymbolName(Type.INT_KEY, 98, "^key_up");
        setSymbolName(Type.INT_KEY, 99, "^key_down");
        setSymbolName(Type.INT_KEY, 100, "^key_insert");
        setSymbolName(Type.INT_KEY, 101, "^key_del");
        setSymbolName(Type.INT_KEY, 102, "^key_home");
        setSymbolName(Type.INT_KEY, 103, "^key_end");
        setSymbolName(Type.INT_KEY, 104, "^key_page_up");
        setSymbolName(Type.INT_KEY, 105, "^key_page_down");

        setSymbolName(Type.INT_SETPOSH, 0, "^setpos_abs_left");
        setSymbolName(Type.INT_SETPOSH, 1, "^setpos_abs_centre");
        setSymbolName(Type.INT_SETPOSH, 2, "^setpos_abs_right");
        setSymbolName(Type.INT_SETPOSH, 3, "^setpos_rel_left");
        setSymbolName(Type.INT_SETPOSH, 4, "^setpos_rel_centre");
        setSymbolName(Type.INT_SETPOSH, 5, "^setpos_rel_right");

        setSymbolName(Type.INT_SETPOSV, 0, "^setpos_abs_top");
        setSymbolName(Type.INT_SETPOSV, 1, "^setpos_abs_centre");
        setSymbolName(Type.INT_SETPOSV, 2, "^setpos_abs_bottom");
        setSymbolName(Type.INT_SETPOSV, 3, "^setpos_rel_top");
        setSymbolName(Type.INT_SETPOSV, 4, "^setpos_rel_centre");
        setSymbolName(Type.INT_SETPOSV, 5, "^setpos_rel_bottom");

        setSymbolName(Type.INT_SETSIZE, 0, "^setsize_abs");
        setSymbolName(Type.INT_SETSIZE, 1, "^setsize_minus");
        setSymbolName(Type.INT_SETSIZE, 2, "^setsize_rel");
        setSymbolName(Type.INT_SETSIZE, 3, "^setsize_3"); // todo
        setSymbolName(Type.INT_SETSIZE, 4, "^setsize_aspect");

        setSymbolName(Type.INT_SETTEXTALIGNH, 0, "^settextalign_left");
        setSymbolName(Type.INT_SETTEXTALIGNH, 1, "^settextalign_centre");
        setSymbolName(Type.INT_SETTEXTALIGNH, 2, "^settextalign_right");
        setSymbolName(Type.INT_SETTEXTALIGNH, 3, "^settextalignh_justified");

        setSymbolName(Type.INT_SETTEXTALIGNV, 0, "^settextalign_top");
        setSymbolName(Type.INT_SETTEXTALIGNV, 1, "^settextalign_centre");
        setSymbolName(Type.INT_SETTEXTALIGNV, 2, "^settextalign_bottom");

        // from tfu
        setSymbolName(Type.INT_WINDOWMODE, 0, "0");
        setSymbolName(Type.INT_WINDOWMODE, 1, "^windowmode_small");
        setSymbolName(Type.INT_WINDOWMODE, 2, "^windowmode_resizable");
        setSymbolName(Type.INT_WINDOWMODE, 3, "^windowmode_fullscreen");

        setSymbolName(Type.INT_CLIENTOPTION, 0, "^clientoption_ambient_occlusion");
        setSymbolName(Type.INT_CLIENTOPTION, 1, "^clientoption_anisotropic_filtering");
        setSymbolName(Type.INT_CLIENTOPTION, 2, "^clientoption_antialiasing_mode");
        setSymbolName(Type.INT_CLIENTOPTION, 3, "^clientoption_antialiasing_quality");
        setSymbolName(Type.INT_CLIENTOPTION, 4, "^clientoption_bloom");
        setSymbolName(Type.INT_CLIENTOPTION, 5, "^clientoption_brightness");
        setSymbolName(Type.INT_CLIENTOPTION, 6, "^clientoption_custom_cursors");
        setSymbolName(Type.INT_CLIENTOPTION, 7, "^clientoption_dof");
        setSymbolName(Type.INT_CLIENTOPTION, 8, "^clientoption_draw_distance");
        setSymbolName(Type.INT_CLIENTOPTION, 9, "^clientoption_ground_blending");
        setSymbolName(Type.INT_CLIENTOPTION, 10, "^clientoption_ground_decor");
        setSymbolName(Type.INT_CLIENTOPTION, 11, "^clientoption_interface_scale");
        setSymbolName(Type.INT_CLIENTOPTION, 12, "^clientoption_lighting_quality");
        setSymbolName(Type.INT_CLIENTOPTION, 13, "^clientoption_max_foreground_fps");
        setSymbolName(Type.INT_CLIENTOPTION, 14, "^clientoption_max_background_fps");
        setSymbolName(Type.INT_CLIENTOPTION, 15, "^clientoption_reflections");
        setSymbolName(Type.INT_CLIENTOPTION, 16, "^clientoption_remove_roof");
        setSymbolName(Type.INT_CLIENTOPTION, 17, "^clientoption_remove_roof_override");
        setSymbolName(Type.INT_CLIENTOPTION, 22, "^clientoption_volume_main_effects");
        setSymbolName(Type.INT_CLIENTOPTION, 18, "^clientoption_game_render_scale");
        setSymbolName(Type.INT_CLIENTOPTION, 23, "^clientoption_volume_main_music");
        setSymbolName(Type.INT_CLIENTOPTION, 19, "^clientoption_shadows");
        setSymbolName(Type.INT_CLIENTOPTION, 20, "^clientoption_shadow_quality");
        setSymbolName(Type.INT_CLIENTOPTION, 21, "^clientoption_texturing");
        setSymbolName(Type.INT_CLIENTOPTION, 24, "^clientoption_volume_background_effects");
        setSymbolName(Type.INT_CLIENTOPTION, 25, "^clientoption_volume_speech");
        setSymbolName(Type.INT_CLIENTOPTION, 26, "^clientoption_volume_login_music");
        setSymbolName(Type.INT_CLIENTOPTION, 27, "^clientoption_volumetric_lighting");
        setSymbolName(Type.INT_CLIENTOPTION, 28, "^clientoption_v_sync");
        setSymbolName(Type.INT_CLIENTOPTION, 30, "^clientoption_smooth_clip_fade");
        setSymbolName(Type.INT_CLIENTOPTION, 31, "^clientoption_canopy_cutout");
        setSymbolName(Type.INT_CLIENTOPTION, 36, "^clientoption_particle_quality");
        setSymbolName(Type.INT_CLIENTOPTION, 29, "^clientoption_entity_highlights");
        setSymbolName(Type.INT_CLIENTOPTION, 32, "^clientoption_diagnostics");
        setSymbolName(Type.INT_CLIENTOPTION, 33, "^clientoption_rich_presence");
        setSymbolName(Type.INT_CLIENTOPTION, 34, "^clientoption_language");
        setSymbolName(Type.INT_CLIENTOPTION, 35, "^clientoption_haptic_feedback");
        setSymbolName(Type.INT_CLIENTOPTION, 37, "^clientoption_custom_player_model_count");
        setSymbolName(Type.INT_CLIENTOPTION, 38, "^clientoption_volume_master");

        setSymbolName(Type.INT_FILTEROP, 1, "^filterop_lt");
        setSymbolName(Type.INT_FILTEROP, 2, "^filterop_lte");
        setSymbolName(Type.INT_FILTEROP, 3, "^filterop_eq");
        setSymbolName(Type.INT_FILTEROP, 4, "^filterop_gte");
        setSymbolName(Type.INT_FILTEROP, 5, "^filterop_gt");

        setDBColumnType(119, 0, List.of(Type.UNKNOWN, Type.UNKNOWN));

        if (Unpack.CONFIG_VERSION >= 1758896171) {
            setDBColumnType(293, 7, List.of(Type.UNKNOWN, Type.UNKNOWN));
        } else {
            setDBColumnType(293, 6, List.of(Type.UNKNOWN, Type.UNKNOWN));
        }

        if (Unpack.CONFIG_VERSION >= 1774022802) {
            setDBColumnType(351, 2, List.of(Type.UNKNOWN, Type.UNKNOWN));
            setDBColumnType(352, 6, List.of(Type.UNKNOWN, Type.UNKNOWN));
            setDBColumnType(352, 7, List.of(Type.UNKNOWN, Type.UNKNOWN, Type.UNKNOWN));
            setDBColumnType(352, 12, List.of(Type.UNKNOWN, Type.UNKNOWN, Type.UNKNOWN, Type.UNKNOWN));
            setDBColumnType(352, 13, List.of(Type.UNKNOWN, Type.UNKNOWN, Type.UNKNOWN));
            setDBColumnType(353, 5, List.of(Type.UNKNOWN, Type.UNKNOWN));
        }
    }

    public static String format(Type type, int value) {
        return format(type, value, true);
    }

    public static String format(Type type, int value, boolean safe) {
        if (type == Type.INT) return format(Type.INT_INT, value, safe);
        if (Unpack.VERSION < 917 && type == Type.FONTMETRICS) return format(Type.GRAPHIC, value, safe); // TODO figure out when fonts were reworked
        if (type == Type.VARP) return format(Type.VAR_PLAYER, value, safe);
        if (type == Type.NAMEDOBJ) return format(Type.OBJ, value, safe);
        if (type == Type.TOPLEVELINTERFACE) return format(Type.INTERFACE, value, safe);
        if (type == Type.OVERLAYINTERFACE) return format(Type.INTERFACE, value, safe);
        if (type == Type.CLIENTINTERFACE) return format(Type.INTERFACE, value, safe);

         if ((type == Type.SYNTH || type == Type.VORBIS) && Unpack.VERSION >= 757) {
             // Starting with 757, the packer auto-converts all synths to vorbis, and synth and vorbis IDs
             // share the same space. Starting with 862, the `[sound/bgsound/randomsound]vorbis=yes` config
             // properties are no longer transmitted, making it impossible to know whether a config sound
             // is a synth or vorbis.
             //
             // To avoid inconsistencies between configs and CS2, format them all as a merged "sound" type.
             return format(Type.SOUND, value, safe); // [sound/bgsound/randomsound]vorbis=yes
         }

        var name = NAME.getOrDefault(type, Map.of()).get(value);

        if (name != null) {
            return quote(name, safe);
        } else if (type == Type.TYPE) {
            return Type.byCharOrID(value).name;
        } else if (type == Type.CHAR) {
            if (value == -1) {
                return "null";
            }

            if (value == 39) return "'\\''";
            if (value == 92) return "'\\\\'";
            return "'" + CP1252.decode(value) + "'";
        } else if (type == Type.COORDGRID) {
            if (value == -1) {
                return "null";
            }

            var level = value >>> 28;
            var x = value >>> 14 & 16383;
            var z = value & 16383;
            return level + "_" + x / 64 + "_" + z / 64 + "_" + x % 64 + "_" + z % 64;
        } else if (type == Type.COMPONENT) {
            if (value == -1) {
                return "null";
            } else {
                var itf = value >> 16;
                var com = value & 0xffff;
                return quote(format(Type.INTERFACE, itf, false) + ":com_" + com, safe);
            }
        } else if (type == Type.DBCOLUMN) {
            int table;
            int column;
            int tuple;

            if (Unpack.VERSION < 911) {
                table = value >>> 8;
                column = value & 0xFF;
                tuple = -1;
            } else {
                table = value >>> 12;
                column = value >>> 4 & 255;
                tuple = (value & 15) - 1;
            }

            if (tuple != -1) {
                return quote(format(Type.DBCOLUMN, (table << 12) | (column << 4), false) + ":" + tuple, safe);
            } else {
                return quote(format(Type.DBTABLE, table, false) + ":col" + column, safe);
            }
        } else if (type == Type.CLIENTSCRIPT) {
            if (value == -1) {
                return "null";
            } else {
                return formatScriptShort(value);
            }
        } else if (type == Type.VAR_INT) {
            if (value == -1) {
                return "null";
            }

            if ((value & 0xff000000) != 0) {
                return format(getVarBitDomain(value & 0xffff).bittype, value & 0xffff);
            } else {
                return format(VarDomain.byID(value >> 16 & 0xff).type, value & 0xffff);
            }
        } else if (type == Type.INT_INT) {
            return Integer.toString(value);
        } else if (type == Type.INT_RGB) {
            if (value == -1) {
                return "null";
            } else {
                return formatColour(value);
            }
        } else if (Type.LATTICE.test(type, Type.INT)) {
            if (value == -1) {
                return "null";
            } else {
                return Integer.toString(value);
            }
        } else {
            if (value == -1) {
                return "null";
            } else {
                name = type.name.replace("_", "") + "_" + Integer.toUnsignedString(value);
            }

            Unpacker.setSymbolName(type, value, name);
            return name;
        }
    }

    public static String format(Type type, long value) {
        if (type == Type.LONG) {
            return String.valueOf(value);
        }

        if (value == -1) {
            return "null";
        }

        return type.name + "_" + value;
    }

    public static String format(Type type, String value) {
        return value;
    }

    private static String quote(String name, boolean safe) {
        if (safe && !name.matches("\\^?[a-zA-Z0-9_.:]+")) {
            return "\"" + name + "\"";
        } else {
            return name;
        }
    }

    public static String formatScriptShort(int value) {
        var result = getScriptName(value);
        result = result.substring(1, result.length() - 1);
        result = result.split(",")[1];
        return result;
    }

    public static String formatComponentShort(int value) {
        var result = format(Type.COMPONENT, value, false);
        result = result.substring(result.indexOf(':') + 1);
        return result;
    }

    public static String formatDBColumnShort(int value) {
        var result = format(Type.DBCOLUMN, value, false);
        result = result.substring(result.indexOf(':') + 1);
        return result;
    }

    public static String formatColour(int colour) {
        var hex = Integer.toHexString(colour);

        if (hex.length() > 6) {
            return "0x" + "0".repeat(8 - hex.length()) + hex;
        } else {
            return "0x" + "0".repeat(6 - hex.length()) + hex;
        }
    }

    public static String formatYesNo(int value) {
        return switch (value) {
            case 0 -> "no";
            case 1 -> "yes";
            default -> throw new IllegalArgumentException("invalid boolean");
        };
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

    public static void setSymbolName(Type type, int id, String name) {
        NAME.computeIfAbsent(type, _ -> new HashMap<>()).put(id, name);
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

    public static void setParamType(int id, Type type) {
        PARAM_TYPE.put(id, type);
    }

    public static Type getParamType(int operand) {
        return Objects.requireNonNull(PARAM_TYPE.get(operand));
    }

    public static void setDBColumnType(int table, int column, List<Type> types) {
        var tableTypes = DBCOLUMN_TYPE.computeIfAbsent(table, _ -> new HashMap<>());
        tableTypes.put(column, types);
    }

    private static List<Type> getDBColumnType(int table, int column) {
        var tableTypes = DBCOLUMN_TYPE.computeIfAbsent(table, _ -> new HashMap<>());
        var type = tableTypes.get(column);

        if (type == null) {
            System.out.println("assuming non-tuple type for untransmitted column: dbtable_" + table + ":col" + column);
            setDBColumnType(table, column, List.of(Type.UNKNOWN));
            return List.of(Type.UNKNOWN);
        }

        return type;
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
        if (Unpack.VERSION < 911) {
            return getDBColumnTypeTuple(column >>> 8, column & 255, -1);
        } else {
            return getDBColumnTypeTuple(column >>> 12, column >>> 4 & 255, (column & 15) - 1);
        }
    }

    public static void setVarType(VarDomain domain, int id, Type type) {
        VAR_TYPE.put(new Tuple2<>(domain, id), type);
    }

    public static Type getVarType(VarDomain domain, int id) {
        return Objects.requireNonNull(VAR_TYPE.get(new Tuple2<>(domain, id)));
    }

    public static void setVarBitDomain(int id, VarDomain domain) {
        VARBIT_DOMAIN.put(id, domain);
    }

    public static VarDomain getVarBitDomain(int id) {
        return Objects.requireNonNull(VARBIT_DOMAIN.get(id));
    }

    public static void unpackRecol(Packet packet, ArrayList<String> lines, int indices) {
        var count = packet.g1();

        if (indices == -1) {
            for (var i = 0; i < count; ++i) {
                unpackRecolLine(packet, lines, i);
            }
        } else {
            for (var i = 0; i < 16; i++) {
                if ((indices & 1 << i) != 0) {
                    unpackRecolLine(packet, lines, i);
                }
            }
        }
    }

    private static void unpackRecolLine(Packet packet, ArrayList<String> lines, int i) {
        var s = packet.g2();
        var d = packet.g2();
        var rgbs = ColourConversion.reverseRGBFromHSL(s);
        var rgbd = ColourConversion.reverseRGBFromHSL(d);

        if (Unpack.VERSION < 468 && s < 100 && d < 100) {
            // In older versions, values below 100 were treated as texture IDs and not
            // converted to HSL when packing, but this leaves an ambiguity. The true
            // values in the source code could be either rgbs,rgbd (if they are valid)
            // or s,d. We choose to unpack as s,d since it leads to the smaller diff
            // between the 463 and 469 caches.
            lines.add("recol" + (i + 1) + "s=" + s);
            lines.add("recol" + (i + 1) + "d=" + d);
        } else if (rgbs != -1 && rgbd != -1) {
            lines.add("recol" + (i + 1) + "s=" + rgbs);
            lines.add("recol" + (i + 1) + "d=" + rgbd);
        } else { // a new opcode was added to skip conversion when packing
            lines.add("recolhsl" + (i + 1) + "s=" + s);
            lines.add("recolhsl" + (i + 1) + "d=" + d);
        }
    }

    public static void unpackRetex(Packet packet, ArrayList<String> lines, int indices) {
        var count = packet.g1();

        if (indices == -1) {
            for (var i = 0; i < count; ++i) {
                lines.add("retex" + (i + 1) + "s=" + format(Type.MATERIAL, packet.g2()));
                lines.add("retex" + (i + 1) + "d=" + format(Type.MATERIAL, packet.g2()));
            }
        } else {
            for (var i = 0; i < 16; i++) {
                if ((indices & 1 << i) != 0) {
                    lines.add("retex" + (i + 1) + "s=" + format(Type.MATERIAL, packet.g2()));
                    lines.add("retex" + (i + 1) + "d=" + format(Type.MATERIAL, packet.g2()));
                }
            }
        }
    }

    public static String getScriptName(int id) {
        if (SCRIPT_NAME.containsKey(id)) {
            return SCRIPT_NAME.get(id);
        }

        var trigger = ScriptUnpacker.SCRIPT_TRIGGERS.get(id);

        if (trigger == null) {
            trigger = !ScriptUnpacker.CALLED.contains(id) && ScriptUnpacker.getReturnTypes(id).isEmpty() ? ScriptTrigger.CLIENTSCRIPT : ScriptTrigger.PROC;
        }

        return "[" + trigger.name().toLowerCase() + ",script" + id + "]";
    }
}
