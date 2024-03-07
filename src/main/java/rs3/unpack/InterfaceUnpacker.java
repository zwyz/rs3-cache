package rs3.unpack;

import rs3.Unpack;
import rs3.unpack.script.ScriptUnpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InterfaceUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.COMPONENT, id) + "]");

        if (Unpack.VERSION < 500 && data[0] != 0xff) {
            return lines; // todo if1
        }

        var version = packet.g1();

        if (version == 255) {
            version = -1;
        }

        var type = packet.g1();
        line(lines, "type=", Unpacker.formatIfType(type));

        if ((type & 128) != 0) {
            type &= 127;
            line(lines, "name=", packet.gjstr());
        }

        line(lines, "contenttype=", packet.g2(), 0);
        decode(lines, packet, version, type);

        if (packet.pos != packet.arr.length) {
            throw new IllegalStateException("end of file not reached");
        }

        return lines;
    }

    private static void decode(ArrayList<String> lines, Packet packet, int version, int type) {
        line(lines, "x=", packet.g2s(), 0); // if_getx
        line(lines, "y=", packet.g2s(), 0); // if_gety
        line(lines, "width=", packet.g2(), 0); // if_getwidth
        line(lines, "height=", packet.g2(), 0); // if_getheight
        var widthmode = 0;
        var heightmode = 0;

        if (Unpack.VERSION >= 500) {
            widthmode = packet.g1s();
            heightmode = packet.g1s();
            line(lines, "widthmode=", decodeSizeMode(widthmode), "abs");
            line(lines, "heightmode=", decodeSizeMode(heightmode), "abs");
            line(lines, "xmode=", decodeXMode(packet.g1s()), "abs_left");
            line(lines, "ymode=", decodeYMode(packet.g1s()), "abs_top");
        }

        if (widthmode == 4 || heightmode == 4) {
            line(lines, "aspectwidth=", packet.g2(), 1); // if_setaspect
            line(lines, "aspectheight=", packet.g2(), 1); // if_setaspect
        }

        var layer = packet.g2null();

        if (layer != -1) {
            line(lines, "layer=", layer); // if_getlayer
        }

        var flags = packet.g1();
        line(lines, "hide=", ((flags & 1) != 0 ? "yes" : "no"), "no"); // if_sethide

        if (version >= 0) {
            line(lines, "noclickthrough=", ((flags & 2) != 0 ? "yes" : "no"), "no"); // if_setnoclickthrough
        }

        switch (type) {
            case 0 -> decodeLayer(lines, packet, version);
            case 3 -> decodeRectangle(lines, packet, version);
            case 4 -> decodeText(lines, packet, version);
            case 5 -> decodeGraphic(lines, packet, version);
            case 6 -> decodeModel(lines, packet, version, widthmode, heightmode);
            case 9 -> decodeLine(lines, packet, version);
            case 10 -> decodeButton(lines, packet, version);
            case 11 -> decodePanel(lines, packet, version);
            case 12 -> decodeCheck(lines, packet, version);
            case 13 -> decodeInput(lines, packet, version);
            case 14 -> decodeSlider(lines, packet, version);
            case 15 -> decodeGrid(lines, packet, version);
            case 16 -> decodeList(lines, packet, version);
            case 17 -> decodeCombo(lines, packet, version);
            case 18 -> decodePagedLayer(lines, packet, version);
            case 19 -> decodePagedLayerHeader(lines, packet, version);
            case 20 -> decodeCarousel(lines, packet, version);
            case 21 -> decodePagedCarousel(lines, packet, version);
            case 22 -> decodeRadioGroup(lines, packet, version);
            case 23 -> drcodeGroupBox(lines, packet, version);
            case 24 -> decodeRadialProgressOverlay(lines, packet, version);
            case 26 -> decodeCRMView(lines, packet, version);
            case 27 -> decodeCutsceneLayer(lines, packet, version);
            case 28 -> decodeModelGroup(lines, packet, version);
            default -> throw new AssertionError("invalid type " + type);
        }

        if (version >= 6) {
            line(lines, "stylesheet=", Unpacker.format(Type.STYLESHEET, packet.g4s()), "null");
        }

        if (version >= 9) {
            line(lines, "unknown10=", packet.g1(), 0);
        }

        var events = 0;

        if (version < 6) {
            events = packet.g3();
        } else {
            events = packet.g4s();
        }

        line(lines, "events=", events, 0); // if_setevents

        var value = packet.g1();

        while (value != 0) {
            var index = (value >> 4) - 1;
            var rate = (value << 8 | packet.g1()) & 4095;

            if (rate == 4095) {
                rate = -1;
            }

            line(lines, "opkey" + index + "=", rate + "," + packet.g1s() + "," + packet.g1s()); // if_setopkey
            value = packet.g1();
        }

        line(lines, "opbase=", packet.gjstr(), ""); // if_setopbase
        var var14 = packet.g1();
        var opcount = var14 & 15;
        var opcursorcount = var14 >> 4;

        if (opcount > 0) {
            for (var i = 0; i < opcount; ++i) {
                line(lines, "op" + i + "=", packet.gjstr(), ""); // if_setop
            }
        }

        if (opcursorcount > 0) {
            line(lines, "opcursor" + packet.g1() + "=", Unpacker.format(Type.CURSOR, packet.g2())); // if_setopcursor
        }

        if (opcursorcount > 1) {
            line(lines, "opcursor" + packet.g1() + "=", Unpacker.format(Type.CURSOR, packet.g2())); // if_setopcursor
        }

        line(lines, "pausetext=", packet.gjstr(), ""); // if_setpausetext
        line(lines, "dragdeadzone=", packet.g1(), 0); // if_setdragdeadzone
        line(lines, "dragdeadtime=", packet.g1(), 0); // if_setdragdeadtime
        line(lines, "dragrenderbehaviour=", packet.g1(), 0); // if_setdragrenderbehaviour
        line(lines, "targetverb=", packet.gjstr(), ""); // if_settargetverb

        if ((events & 0x3f800) != 0) {
            line(lines, "targetcursor0=", Unpacker.format(Type.CURSOR, packet.g2null()), "null"); // if_settargetcursors
            line(lines, "targetcursor1=", Unpacker.format(Type.CURSOR, packet.g2null()), "null"); // if_settargetcursors
            line(lines, "targetcursor2=", Unpacker.format(Type.CURSOR, packet.g2null()), "null"); // if_settargetcursors
        }

        if (version >= 0) {
            line(lines, "mouseovercursor=", Unpacker.format(Type.CURSOR, packet.g2null()), "null"); // if_setmouseovercursor
        }

        if (version >= 0) {
            var intparamcount = packet.g1();

            for (var i = 0; i < intparamcount; ++i) {
                line(lines, "param=", Unpacker.format(Type.PARAM, packet.g3()) + "," + packet.g4s()); // if_setparam_int
            }

            var stringparamcount = packet.g1();

            for (var i = 0; i < stringparamcount; ++i) {
                line(lines, "param=", Unpacker.format(Type.PARAM, packet.g3()) + "," + packet.gjstr2()); // if_setparam_string
            }
        }

        line(lines, "onload=", decodeHook(packet), "null");
        line(lines, "onmouseover=", decodeHook(packet), "null"); // if_setonmouseover
        line(lines, "onmouseleave=", decodeHook(packet), "null"); // if_setonmouseleave
        line(lines, "ontargetleave=", decodeHook(packet), "null"); // if_setontargetleave
        line(lines, "ontargetenter=", decodeHook(packet), "null"); // if_setontargetenter
        line(lines, "onvartransmit=", decodeHook(packet), "null"); // if_setonvartransmit
        line(lines, "oninvtransmit=", decodeHook(packet), "null"); // if_setoninvtransmit
        line(lines, "onstattransmit=", decodeHook(packet), "null"); // if_setonstattransmit
        line(lines, "ontimer=", decodeHook(packet), "null"); // if_setontimer
        line(lines, "onop=", decodeHook(packet), "null"); // if_setonop

        if (version >= 0) {
            line(lines, "onopt=", decodeHook(packet), "null"); // if_setonopt
        }

        line(lines, "onmouserepeat=", decodeHook(packet), "null"); // if_setonmouserepeat
        line(lines, "onclick=", decodeHook(packet), "null"); // if_setonclick
        line(lines, "onclickrepeat=", decodeHook(packet), "null"); // if_setonclickrepeat
        line(lines, "onrelease=", decodeHook(packet), "null"); // if_setonrelease
        line(lines, "onhold=", decodeHook(packet), "null"); // if_setonhold
        line(lines, "ondrag=", decodeHook(packet), "null"); // if_setondrag
        line(lines, "ondragcomplete=", decodeHook(packet), "null"); // if_setondragcomplete
        line(lines, "onscrollwheel=", decodeHook(packet), "null"); // if_setonscrollwheel
        line(lines, "onvarctransmit=", decodeHook(packet), "null"); // if_setonvarctransmit
        line(lines, "onvarcstrtransmit=", decodeHook(packet), "null"); // if_setonvarcstrtransmit

        if (version >= 6) {
            line(lines, "onbuttonclick=", decodeHook(packet), "null"); // if_setonbuttonclick
            line(lines, "onhook51=", decodeHook(packet), "null"); // if_setonhook51
            line(lines, "onlistselect=", decodeHook(packet), "null"); //
        }

        if (version >= 8) {
            line(lines, "onupdated=", decodeHook(packet), "null"); // if_crmview_setonupdated
        }

        line(lines, "onvartransmitlist=", decodeHookTransmitList(packet), "null");
        line(lines, "oninvtransmitlist=", decodeHookTransmitList(packet), "null");
        line(lines, "onstattransmitlist=", decodeHookTransmitList(packet), "null");
        line(lines, "onvarctransmitlist=", decodeHookTransmitList(packet), "null");
        line(lines, "onvarcstrtransmitlist=", decodeHookTransmitList(packet), "null");
    }


    private static String decodeHook(Packet packet) {
        var count = packet.g1();

        if (count == 0) {
            return "null";
        }

        packet.g1();
        var script = packet.g4s();
        var arguments = new ArrayList<String>();

        for (var i = 0; i < count - 1; ++i) {
            var value = switch (packet.g1()) {
                case 0 -> packet.g4s();
                case 1 -> packet.gjstr();
                default -> throw new IllegalStateException("Unexpected value: " + packet.g1());
            };

            arguments.add(formatHookArgument(value, ScriptUnpacker.SCRIPT_PARAMETERS.get(script).get(i)));
        }

        if (arguments.isEmpty()) {
            return Unpacker.format(Type.CLIENTSCRIPT, script);
        } else {
            return Unpacker.format(Type.CLIENTSCRIPT, script) + "(" + String.join(", ", arguments) + ")";
        }
    }

    private static String formatHookArgument(Object value, Type type) {
        if (ScriptUnpacker.ASSUME_UNKNOWN_TYPES_ARE_BASE) {
            if (type == Type.UNKNOWN_INT) type = Type.INT_INT;
            if (type == Type.UNKNOWN_LONG) type = Type.LONG;
            if (type == Type.UNKNOWN_OBJECT) type = Type.STRING;
        }

        if (Objects.equals(value, "event_opbase")) return "event_opbase";
        if (Objects.equals(value, "event_text")) return "event_text";
        if (Objects.equals(value, Integer.MIN_VALUE + 1)) return "event_mousex";
        if (Objects.equals(value, Integer.MIN_VALUE + 2)) return "event_mousey";
        if (Objects.equals(value, Integer.MIN_VALUE + 3)) return "event_com";
        if (Objects.equals(value, Integer.MIN_VALUE + 4)) return "event_opindex";
        if (Objects.equals(value, Integer.MIN_VALUE + 5)) return "event_comsubid";
        if (Objects.equals(value, Integer.MIN_VALUE + 6)) return "event_com2";
        if (Objects.equals(value, Integer.MIN_VALUE + 7)) return "event_comsubid2";
        if (Objects.equals(value, Integer.MIN_VALUE + 8)) return "event_key";
        if (Objects.equals(value, Integer.MIN_VALUE + 9)) return "event_keychar";
        if (Objects.equals(value, Integer.MIN_VALUE + 10)) return "event_gamepadvalue";
        if (Objects.equals(value, Integer.MIN_VALUE + 11)) return "event_gamepadbutton";

        if (value instanceof Integer i) {
            return Unpacker.format(type, i);
        }

        return "\"" + value + "\"";
    }

    private static String decodeHookTransmitList(Packet packet) {
        var count = packet.g1();

        if (count == 0) {
            return "null";
        }

        var sb = new StringBuilder();

        for (var i = 0; i < count; ++i) {
            if (i > 0) {
                sb.append(",");
            }

            sb.append(packet.g4s());
        }

        return sb.toString();
    }

    private static void decodeLayer(ArrayList<String> lines, Packet packet, int version) {
        line(lines, "scrollwidth=", packet.g2(), 0); // if_getscrollwidth
        line(lines, "scrollheight=", packet.g2(), 0); // if_getscrollheight

        if (version == -1) {
            if (Unpack.VERSION >= 500) {
                line(lines, "noclickthrough=", ((packet.g1() == 1) ? "yes" : "no"), "no"); // if_setnoclickthrough
            }
        } else if (version >= 9) {
            line(lines, "margin=", packet.g1() + "," + packet.g1() + "," + packet.g1() + "," + packet.g1(), "0,0,0,0"); // if_margin_set
        } else if (version >= 6) {
            line(lines, "margin=", packet.g2() + "," + packet.g2() + "," + packet.g2() + "," + packet.g2(), "0,0,0,0"); // if_margin_set
        }
    }

    private static void decodeGraphic(ArrayList<String> lines, Packet packet, int version) {
        decodeSpritePart("", lines, packet, version, "0xffffffff", "yes");
    }

    private static void decodeModel(ArrayList<String> lines, Packet packet, int version, int widthmode, int heightmode) {
        line(lines, "model=", Unpacker.format(Type.MODEL, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()));

        if (Unpack.VERSION < 600) {
            line(lines, "modelorigin_x=", packet.g2s()); // if_setmodelorigin
            line(lines, "modelorigin_y=", packet.g2s()); // if_setmodelorigin
            line(lines, "modelangle_x=", packet.g2()); // if_getmodelangle_x
            line(lines, "modelangle_y=", packet.g2()); // if_getmodelangle_y
            line(lines, "modelangle_z=", packet.g2()); // if_getmodelangle_z
            line(lines, "modelzoom=", packet.g2()); // if_setmodelzoom
            line(lines, "modelanim=", Unpacker.format(Type.SEQ, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()), "null"); // if_setmodelanim
            line(lines, "modelorthog=", Unpacker.formatBoolean(packet.g1()), "no"); // if_setmodelorthog
            line(lines, "unknown100=", packet.g2());
            line(lines, "unknown101=", packet.g2());
            line(lines, "unknown103=", Unpacker.formatBoolean(packet.g1()), "no");
        } else {
            var flags = packet.g1();
            var flag1 = (flags & 1) != 0;
            var flag2 = (flags & 2) != 0;
            var flag4 = (flags & 4) != 0;
            var flag8 = (flags & 8) != 0;

            line(lines, "modelprecisezoom=", (flag2 ? "yes" : "no"), "no"); // todo
            line(lines, "modelorthog=", (flag4 ? "yes" : "no"), "no"); // if_setmodelorthog
            line(lines, "modelnodepth=", (flag8 ? "yes" : "no"), "no"); // todo

            if (flag1) {
                line(lines, "modelorigin_x=", packet.g2s()); // if_setmodelorigin
                line(lines, "modelorigin_y=", packet.g2s()); // if_setmodelorigin
                line(lines, "modelangle_x=", packet.g2()); // if_getmodelangle_x
                line(lines, "modelangle_y=", packet.g2()); // if_getmodelangle_y
                line(lines, "modelangle_z=", packet.g2()); // if_getmodelangle_z
                line(lines, "modelzoom=", packet.g2()); // if_setmodelzoom
            } else if (flag2) {
                line(lines, "modelorigin_x=", packet.g2s()); // if_setmodelorigin
                line(lines, "modelorigin_y=", packet.g2s()); // if_setmodelorigin
                line(lines, "modelorigin_z=", packet.g2s()); // if_setmodelorigin
                line(lines, "modelangle_x=", packet.g2()); // if_getmodelangle_x
                line(lines, "modelangle_y=", packet.g2()); // if_getmodelangle_y
                line(lines, "modelangle_z=", packet.g2()); // if_getmodelangle_z
                line(lines, "modelzoom=", packet.g2()); // if_setmodelzoom
            }

            line(lines, "modelanim=", Unpacker.format(Type.SEQ, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()), "null"); // if_setmodelanim
        }

        if (widthmode != 0) {
            line(lines, "modelobjwidth=", packet.g2()); // todo
        }

        if (heightmode != 0) {
            line(lines, "modelobjheight=", packet.g2()); // todo
        }
    }

    private static void decodeText(ArrayList<String> lines, Packet packet, int version) {
        decodeTextPart("", lines, packet, version, 0, 0, "no");
    }

    private static void decodeRectangle(ArrayList<String> lines, Packet packet, int version) {
        line(lines, "colour=", Unpacker.formatColour(packet.g4s())); // if_setcolour
        line(lines, "fill=", (packet.g1() == 1 ? "yes" : "no"), "no"); // if_setfill
        line(lines, "trans=", packet.g1(), 0); // if_settrans
    }

    private static void decodeLine(ArrayList<String> lines, Packet packet, int version) {
        line(lines, "linewid=", packet.g1(), 1); // if_setlinewid
        line(lines, "colour=", Unpacker.formatColour(packet.g4s())); // if_setcolour
        line(lines, "linedirection=", (packet.g1() == 1 ? "yes" : "no"), "no"); // if_setlinedirection
    }

    private static void decodeButton(ArrayList<String> lines, Packet packet, int version) {
        line(lines, "enabled=", (packet.g1() == 1 ? "yes" : "no"), "yes"); // if_setenabled
        line(lines, "cantoggle=", (packet.g1() == 1 ? "yes" : "no"), "no"); // if_button_setcantoggle
        line(lines, "unknown1=", packet.g1(), 1);
        line(lines, "setlinkobjoptions1=", (packet.g1() == 1 ? "yes" : "no"), "yes"); // if_button_setlinkobjoptions
        line(lines, "setlinkobjoptions2=", (packet.g1() == 1 ? "yes" : "no"), "yes"); // if_button_setlinkobjoptions
        line(lines, "textareasizeoffsets=", packet.g1() + "," + packet.g1() + "," + packet.g1() + "," + packet.g1(), "0,0,0,0"); // if_button_settextareasizeoffsets

        line(lines, "trans=", packet.g1(), 0); // if_settrans
        line(lines, "colour=", Unpacker.formatColour(packet.g4s()), "0xffffff"); // if_setcolour
        decodeSpritePart("sprite.", lines, packet, version, "0xffffff", "no"); // todo
        decodeTextPart("text.", lines, packet, version, 1, 1, "yes"); // todo
    }

    private static void decodePanel(ArrayList<String> lines, Packet packet, int version) {
        line(lines, "scrollwidth=", packet.g2(), 0); // if_setscrollsize
        line(lines, "scrollheight=", packet.g2(), 0); // if_setscrollsize
        line(lines, "isvertical=", (((packet.g1() == 1) ? "yes" : "no"))); // if_panel_setisvertical
        line(lines, "childspacing=", packet.g1()); // if_setchildspacing
    }

    private static void decodeCheck(ArrayList<String> lines, Packet packet, int version) {
        line(lines, "enabled=", (packet.g1() == 1 ? "yes" : "no"), "yes"); // if_setenabled
        line(lines, "checked=", (packet.g1() == 1 ? "yes" : "no"), "no"); // todo
        line(lines, "alignment=", packet.g1(), 0); // if_check_setalignment
        line(lines, "buttonsize=", packet.g1(), 0); // if_check_setbuttonsize
        line(lines, "trans=", packet.g1(), 0); // if_settrans
        line(lines, "colour=", Unpacker.formatColour(packet.g4s()), "0xffffffff"); // if_setcolour
        decodeSpritePart("sprite.", lines, packet, version, "0xffffff", "no"); // todo
        decodeTextPart("text.", lines, packet, version, 0, 0, "yes"); // todo
    }

    private static void decodeInput(ArrayList<String> lines, Packet packet, int version) {
        line(lines, "enabled=", (packet.g1() == 1 ? "yes" : "no")); // if_setenabled
        line(lines, "filtermode=", packet.g1()); // if_input_setup
        line(lines, "visibilitymode=", packet.g1()); // if_input_setup
        line(lines, "unknown8=", packet.g2()); // if_input_setup

        if (version >= 9) {
            line(lines, "margin=", packet.g1() + "," + packet.g1() + "," + packet.g1() + "," + packet.g1(), "0,0,0,0"); // todo
        }

        if (version >= 7) {
            line(lines, "keyhandlingmode=", packet.g1(), 0);
        }

        line(lines, "trans=", packet.g1(), 0); // if_settrans
        line(lines, "colour=", Integer.toHexString(packet.g4s())); // if_setcolour
        decodeSpritePart("sprite.", lines, packet, version, "0xffffff", "no");
        decodeTextPart("text.", lines, packet, version, 0, 0, "no");
        decodeScrollbarPart("scrollbar.", lines, packet, version);
    }

    private static void decodeSlider(ArrayList<String> lines, Packet packet, int version) {
        throw new UnsupportedOperationException("slider");
    }

    private static void decodeGrid(ArrayList<String> lines, Packet packet, int version) {
        line(lines, "scrollwidth=", packet.g2(), 0); // if_setscrollsize
        line(lines, "scrollheight=", packet.g2(), 0); // if_setscrollsize
        line(lines, "childspacing=", packet.g1()); // if_setchildspacing
        line(lines, "layoutparams_x=", packet.g2()); // if_grid_setlayoutparams
        line(lines, "layoutparams_y=", packet.g2()); // if_grid_setlayoutparams
        line(lines, "layoutparams_mode=", (packet.g1() == 1 ? "yes" : "no")); // if_grid_setlayoutparams
    }

    private static void decodeList(ArrayList<String> lines, Packet packet, int version) {
        line(lines, "enabled=", (packet.g1() == 1 ? "yes" : "no"), "yes");
        line(lines, "dropdownnumentries=", packet.g1()); // if_list_setdropdownnumentries
        line(lines, "selectionlimit=", packet.g1()); // if_list_setselectionlimit
        line(lines, "entryheight=", packet.g1()); // if_list_setentryheight

        if (version >= 9) {
            line(lines, "entryiconscale=", packet.g1()); // if_list_setentryiconscale
        }

        line(lines, "dropdownbuttonparams_size=", packet.g1()); // if_list_setdropdownbuttonparams
        line(lines, "dropdownbuttonparams_offset=", packet.g1()); // if_list_setdropdownbuttonparams

        var count0 = packet.g2();

        for (var i = 0; i < count0; i++) {
            line(lines, "unknown14=", packet.gjstr());
        }

        var count1 = packet.g2();

        for (var i = 0; i < count1; i++) {
            line(lines, "unknown15=", packet.g4s());
        }

        var count2 = packet.g2();

        for (var i = 0; i < count2; i++) {
            line(lines, "unknown16=", packet.g2());
        }

        if (version >= 9) {
            line(lines, "margin1=", packet.g1() + "," + packet.g1() + "," + packet.g1() + "," + packet.g1(), "0,0,0,0");
            line(lines, "margin2=", packet.g1() + "," + packet.g1() + "," + packet.g1() + "," + packet.g1(), "0,0,0,0");
        }

        line(lines, "trans=", packet.g1(), 0); // if_settrans
        line(lines, "colour=", Unpacker.formatColour(packet.g4s()), "0xffffffff"); // if_setcolour
        decodeSpritePart("button.", lines, packet, version, "0xffffff", "no");
        decodeSpritePart("header.", lines, packet, version, "0xffffff", "no");
        decodeSpritePart("body.", lines, packet, version, "0xffffff", "no");
        decodeTextPart("text.", lines, packet, version, 0, 1, "yes");
        decodeScrollbarPart("scrollbar.", lines, packet, version);
    }

    private static void decodeCombo(ArrayList<String> lines, Packet packet, int version) {
        throw new UnsupportedOperationException("combo");
    }

    private static void decodePagedLayer(ArrayList<String> lines, Packet packet, int version) {
        throw new UnsupportedOperationException("pagedlayer");
    }

    private static void decodePagedLayerHeader(ArrayList<String> lines, Packet packet, int version) {
        throw new UnsupportedOperationException("pagedlayerheader");
    }

    private static void decodeCarousel(ArrayList<String> lines, Packet packet, int version) {
        throw new UnsupportedOperationException("carousel");
    }

    private static void decodePagedCarousel(ArrayList<String> lines, Packet packet, int version) {
        throw new UnsupportedOperationException("pagedcarousel");
    }

    private static void decodeRadioGroup(ArrayList<String> lines, Packet packet, int version) {
        throw new UnsupportedOperationException("radiogroup");
    }

    private static void drcodeGroupBox(ArrayList<String> lines, Packet packet, int version) {
        throw new UnsupportedOperationException("groupbox");
    }

    private static void decodeRadialProgressOverlay(ArrayList<String> lines, Packet packet, int version) {
        throw new UnsupportedOperationException("radialprogressoverlay");
    }

    private static void decodeCRMView(ArrayList<String> lines, Packet packet, int version) {
        line(lines, "unknown21=", (packet.g1() == 1 ? "yes" : "no"));
        var count = packet.g2();

        for (var i = 0; i < count; i++) {
            line(lines, "unknown22=", packet.g2());
        }

        line(lines, "unknown23=", packet.gjstr());
        line(lines, "unknown24=", packet.g1());

        var count2 = packet.g2();

        for (var i = 0; i < count2; i++) {
            line(lines, "unknown25=", packet.gjstr());
        }

        var count3 = packet.g2();

        for (var i = 0; i < count3; i++) {
            line(lines, "unknown26=", packet.g4s());
        }
    }

    private static void decodeCutsceneLayer(ArrayList<String> lines, Packet packet, int version) {
        throw new UnsupportedOperationException("cutsceneoverlay");
    }

    private static void decodeModelGroup(ArrayList<String> lines, Packet packet, int version) {
        throw new UnsupportedOperationException("modelgroup");
    }


    private static void decodeTextPart(String prefix, ArrayList<String> lines, Packet packet, int version, int defaultAlignH, int defaultAlignV, String defaultTextShadow) {
        line(lines, prefix + "textfont=", Unpacker.format(Type.FONTMETRICS, Unpack.VERSION <= 600 ? packet.g2null() : packet.gSmart2or4null()), "null"); // if_settextfont

        if (version >= 2) {
            line(lines, prefix + "fontmono=", (packet.g1() == 1 ? "yes" : "no"), "yes"); // if_setfontmono
        }

        line(lines, prefix + "text=", packet.gjstr(), ""); // if_settext
        line(lines, prefix + "textlineheight=", packet.g1(), 0); // todo
        line(lines, prefix + "textalignh=", packet.g1(), defaultAlignH); // if_settextalign
        line(lines, prefix + "textalignv=", packet.g1(), defaultAlignV); // if_settextalign
        line(lines, prefix + "textshadow=", (packet.g1() == 1 ? "yes" : "no"), defaultTextShadow); // if_settextshadow
        line(lines, prefix + "colour=", Unpacker.formatColour(packet.g4s()), "0xffffff"); // if_setcolour

        if (Unpack.VERSION >= 600) {
            line(lines, prefix + "trans=", packet.g1(), 0); // if_settrans
        }

        if (version >= 0) {
            line(lines, prefix + "maxlines=", packet.g1(), 0); // if_setmaxlines
        }
    }

    private static void decodeSpritePart(String prefix, ArrayList<String> lines, Packet packet, int version, String defaultColour, String defaultClickmask) {
        line(lines, prefix + "graphic=", Unpacker.format(Type.GRAPHIC, packet.g4s()), "null"); // if_setgraphic
        line(lines, prefix + "2dangle=", packet.g2(), 0); // if_set2dangle
        var flags = packet.g1();
        line(lines, prefix + "tiling=", ((flags & 1) != 0 ? "yes" : "no"), "no"); // if_settiling
        line(lines, prefix + "alpha=", ((flags & 2) != 0 ? "yes" : "no"), "no"); // if_setalpha
        line(lines, prefix + "trans=", packet.g1(), 0); // if_settrans
        line(lines, prefix + "outline=", packet.g1(), 0); // if_setoutline
        line(lines, prefix + "graphicshadow=", packet.g4s(), 0); // if_setgraphicshadow
        line(lines, prefix + "vflip=", (packet.g1() == 1 ? "yes" : "no"), "no"); // if_setvflip
        line(lines, prefix + "hflip=", (packet.g1() == 1 ? "yes" : "no"), "no"); // if_sethflip
        line(lines, prefix + "colour=", Unpacker.formatColour(packet.g4s()), defaultColour); // if_setcolour

        if (version >= 3) {
            line(lines, prefix + "clickmask=", (packet.g1() == 1 ? "yes" : "no"), defaultClickmask); // if_setclickmask
        }

        if (version >= 6) {
            line(lines, prefix + "edge=", packet.g1() + "," + packet.g1() + "," + packet.g1() + "," + packet.g1(), "0,0,0,0"); // if_graphic_setedge
        }
    }

    private static void decodeScrollbarPart(String prefix, ArrayList<String> lines, Packet packet, int version) {
        line(lines, prefix + "unknown17=", (packet.g1() == 1 ? "yes" : "no"));
        line(lines, prefix + "unknown18=", (packet.g1() == 1 ? "yes" : "no"));
        line(lines, prefix + "unknown19=", packet.g1());
        line(lines, prefix + "unknown20=", packet.g1());
        decodeSpritePart(prefix + "background.", lines, packet, version, "0xffffff", "no");
        decodeSpritePart(prefix + "button.", lines, packet, version, "0xffffff", "no");
        decodeSpritePart(prefix + "handle.", lines, packet, version, "0xffffff", "no");
    }

    public static String decodeSizeMode(int widthmode) {
        return switch (widthmode) {
            case 0 -> "abs";
            case 1 -> "minus";
            case 2 -> "proportion";
            case 3 -> "mode_3";
            case 4 -> "aspect";
            default -> throw new IllegalStateException("Unexpected value: " + widthmode);
        };
    }

    public static String decodeXMode(int widthmode) {
        return switch (widthmode) {
            case 0 -> "abs_left";
            case 1 -> "abs_centre";
            case 2 -> "abs_right";
            case 3 -> "proportion_left";
            case 4 -> "proportion_centre";
            case 5 -> "proportion_right";
            default -> throw new IllegalStateException("Unexpected value: " + widthmode);
        };
    }

    public static String decodeYMode(int widthmode) {
        return switch (widthmode) {
            case 0 -> "abs_top";
            case 1 -> "abs_centre";
            case 2 -> "abs_bottom";
            case 3 -> "proportion_top";
            case 4 -> "proportion_centre";
            case 5 -> "proportion_bottom";
            default -> throw new IllegalStateException("Unexpected value: " + widthmode);
        };
    }

    private static void line(ArrayList<String> lines, String name, Object value) {
        lines.add(name + value);
    }

    private static void line(ArrayList<String> lines, String name, Object value, Object ignore) {
        if (!Objects.equals(value, ignore)) {
            lines.add(name + value);
        }
    }
}
