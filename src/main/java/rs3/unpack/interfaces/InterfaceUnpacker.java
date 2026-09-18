package rs3.unpack.interfaces;

import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.unpack.script.ScriptUnpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InterfaceUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var packet = new Packet(data);
        var component = Component.decode(id, packet);

        if (packet.pos != packet.arr.length) {
            throw new IllegalStateException("end of file not reached");
        }

        return unpack(component);
    }

    public static List<String> unpack(Component component) {
        var lines = new ArrayList<String>();
        lines.add("[" + Unpacker.formatComponentShort(component.id) + "]");
        lines.add("type=" + formatIfType(component.type));

        if (component.name != null) lines.add("name=" + component.name);
        if (component.clientcode != 0) lines.add("clientcode=" + component.clientcode);
        if (component.x != 0) lines.add("x=" + component.x); // if_getx
        if (component.y != 0) lines.add("y=" + component.y); // if_gety
        if (component.width != 0) lines.add("width=" + component.width); // if_getwidth
        if (component.height != 0) lines.add("height=" + component.height); // if_getheight
        if (component.widthmode != 0) lines.add("widthmode=" + formatSizeMode(component.widthmode));
        if (component.heightmode != 0) lines.add("heightmode=" + formatSizeMode(component.heightmode));
        if (component.xmode != 0) lines.add("xmode=" + formatXMode(component.xmode));
        if (component.ymode != 0) lines.add("ymode=" + formatYMode(component.ymode));
        if (component.aspectwidth != 1) lines.add("aspectwidth=" + component.aspectwidth); // if_setaspect
        if (component.aspectheight != 1) lines.add("aspectheight=" + component.aspectheight); // if_setaspect
        if (component.layer != -1) lines.add("layer=" + Unpacker.formatComponentShort((component.id & 0xffff0000) | component.layer)); // if_getlayer
        if (component.mouseoverlayer != -1) lines.add("mouseoverlayer=" + Unpacker.formatComponentShort((component.id & 0xffff0000) | component.mouseoverlayer)); // if1 only
        if (component.hide) lines.add("hide=yes"); // if_sethide
        if (component.noclickthrough) lines.add("noclickthrough=yes"); // if_setnoclickthrough
        if (component.buttontype != 0) lines.add("buttontype=" + component.buttontype); // if1 only
        if (component.legacytrans != 0) lines.add("trans=" + component.legacytrans); // if1 only

        switch (component) {
            case LayerComponent c -> unpackLayer(lines, c);
            case InputBoxComponent c -> unpackInputBox(lines, c);
            case InvComponent c -> unpackInv(lines, c);
            case RectangleComponent c -> unpackRectangle(lines, c);
            case TextComponent c -> unpackText(lines, c);
            case GraphicComponent c -> unpackGraphic(lines, c);
            case ModelComponent c -> unpackModel(lines, c);
            case InvTextComponent c -> unpackInvText(lines, c);
            case TooltipComponent c -> unpackTooltip(lines, c);
            case LineComponent c -> unpackLine(lines, c);
            case ButtonComponent c -> unpackButton(lines, c);
            case PanelComponent c -> unpackPanel(lines, c);
            case CheckComponent c -> unpackCheck(lines, c);
            case InputComponent c -> unpackInput(lines, c);
            case GridComponent c -> unpackGrid(lines, c);
            case ListComponent c -> unpackList(lines, c);
            case CRMViewComponent c -> unpackCRMView(lines, c);
            default -> throw new AssertionError("invalid type " + component.type);
        }

        if (component.stylesheet != -1) lines.add("stylesheet=" + Unpacker.format(Type.STYLESHEET, component.stylesheet));
        if (component.unknown10 != 0) lines.add("unknown10=" + component.unknown10);

        // if_setevents
        var events = component.events;
        if (((events >>> 0) & 1) != 0) lines.add("pausebutton=yes");

        var transmitop = new ArrayList<String>();
        if (((events >>> 1) & 1) != 0) transmitop.add("1");
        if (((events >>> 2) & 1) != 0) transmitop.add("2");
        if (((events >>> 3) & 1) != 0) transmitop.add("3");
        if (((events >>> 4) & 1) != 0) transmitop.add("4");
        if (((events >>> 5) & 1) != 0) transmitop.add("5");
        if (((events >>> 6) & 1) != 0) transmitop.add("6");
        if (((events >>> 7) & 1) != 0) transmitop.add("7");
        if (((events >>> 8) & 1) != 0) transmitop.add("8");
        if (((events >>> 9) & 1) != 0) transmitop.add("9");
        if (((events >>> 10) & 1) != 0) transmitop.add("10");
        if (!transmitop.isEmpty()) lines.add("transmitop=" + String.join(",", transmitop));

        var targetmask = new ArrayList<String>();
        if ((events & (1 << 11)) != 0) targetmask.add("obj");
        if ((events & (1 << 12)) != 0) targetmask.add("npc");
        if ((events & (1 << 13)) != 0) targetmask.add("loc");
        if ((events & (1 << 14)) != 0) targetmask.add("player");
        if ((events & (1 << 15)) != 0) targetmask.add(Unpack.VERSION < 566 ? "inv" : "self");
        if ((events & (1 << 16)) != 0) targetmask.add("com");
        if ((events & (1 << 17)) != 0) targetmask.add("coord");
        if (!targetmask.isEmpty()) lines.add("targetmask=" + String.join(",", targetmask));

        if (((events >>> 18) & 0b111) != 0) lines.add("dragdepth=" + ((events >>> 18) & 0b111));
        if (((events >>> 21) & 1) != 0) lines.add("candrop=yes");
        if (((events >>> 22) & 1) != 0) lines.add("cantarget=yes");
        if (((events >>> 23) & 1) != 0) lines.add("event23=yes");
        if (((events >>> 24) & 1) != 0) lines.add("event24=yes");
        if (((events >>> 25) & 1) != 0) lines.add("event25=yes");
        if (((events >>> 26) & 1) != 0) lines.add("event26=yes");
        if (((events >>> 27) & 1) != 0) lines.add("event27=yes");
        if (((events >>> 28) & 1) != 0) lines.add("event28=yes");
        if (((events >>> 29) & 1) != 0) lines.add("event29=yes");
        if (((events >>> 30) & 1) != 0) lines.add("event30=yes");
        if (((events >>> 31) & 1) != 0) lines.add("event31=yes");

        if (!component.opbase.isEmpty()) lines.add("opbase=" + component.opbase); // if_setopbase
        if (!component.pausetext.isEmpty()) lines.add("pausetext=" + component.pausetext); // if_setpausetext

        for (var i = 0; i < component.ops.length; i++) { // if_setop
            if (!component.ops[i].isEmpty()) lines.add("op" + (i + 1) + "=" + component.ops[i]);
        }

        for (var i = 0; i < component.ops.length; i++) { // if_setopcursor
            if (component.opcursors[i] != -1) {
                lines.add("opcursor" + i + "=" + Unpacker.format(Type.CURSOR, component.opcursors[i]));
            }
        }

        for (var opkey : component.opkeys) { // if_setopkey
            if (opkey.rate() == -1) {
                lines.add("opkey" + opkey.op() + "=" + opkey.key() + "," + opkey.mods() + "," + opkey.rate());
            } else {
                lines.add("opkey" + opkey.op() + "=" + opkey.key() + "," + opkey.mods());
            }
        }

        if (component.dragdeadzone != 0) lines.add("dragdeadzone=" + component.dragdeadzone); // if_setdragdeadzone
        if (component.dragdeadtime != 0) lines.add("dragdeadtime=" + component.dragdeadtime); // if_setdragdeadtime
        if (component.dragrenderbehaviour != 0) lines.add("dragrenderbehaviour=" + component.dragrenderbehaviour); // if_setdragrenderbehaviour
        if (!component.targetverb.isEmpty()) lines.add("targetverb=" + component.targetverb); // if_settargetverb
        if (!component.targetbase.isEmpty()) lines.add("targetbase=" + component.targetbase); // if1 only
        if (!component.buttontext.isEmpty()) lines.add("buttontext=" + component.buttontext); // if1 only
        if (component.targetcursor1 != -1) lines.add("targetcursor1=" + Unpacker.format(Type.CURSOR, component.targetcursor1));
        if (component.targetcursor2 != -1) lines.add("targetcursor2=" + Unpacker.format(Type.CURSOR, component.targetcursor2));
        if (component.targetcursor3 != -1) lines.add("targetcursor3=" + Unpacker.format(Type.CURSOR, component.targetcursor3));
        if (component.mouseovercursor != -1) lines.add("mouseovercursor=" + Unpacker.format(Type.CURSOR, component.mouseovercursor)); // if_setmouseovercursor

        for (var entry : component.params) { // if_setparam
            var param = entry.param();
            var value = entry.value();
            var type = Unpacker.getParamType(param);

            if (type == Type.STRING) {
                lines.add("param=" + Unpacker.format(Type.PARAM, param) + "," + value);
            } else {
                lines.add("param=" + Unpacker.format(Type.PARAM, param) + "," + Unpacker.format(type, (int) value));
            }
        }

        if (component.scriptInstructions.length != 0) { // if1 only
            for (var i = 0; i < component.scriptInstructions.length; i++) {
                if (i >= component.scriptComparison.length || component.scriptComparison[i] == 0) {
                    lines.add("script" + i + "=" + unpackIfVar(component.scriptInstructions[i]));
                } else if (component.scriptComparison[i] == 1) {
                    lines.add("script=" + unpackIfVar(component.scriptInstructions[i]) + " = " + component.scriptComparisonValue[i]);
                } else if (component.scriptComparison[i] == 2) {
                    lines.add("script=" + unpackIfVar(component.scriptInstructions[i]) + " < " + component.scriptComparisonValue[i]);
                } else if (component.scriptComparison[i] == 3) {
                    lines.add("script=" + unpackIfVar(component.scriptInstructions[i]) + " > " + component.scriptComparisonValue[i]);
                } else if (component.scriptComparison[i] == 4) {
                    lines.add("script=" + unpackIfVar(component.scriptInstructions[i]) + " != " + component.scriptComparisonValue[i]);
                }
            }
        }

        if (component.onload != null) lines.add("onload=" + formatHook(component.onload));
        if (component.onmouseover != null) lines.add("onmouseover=" + formatHook(component.onmouseover)); // if_setonmouseover
        if (component.onmouseleave != null) lines.add("onmouseleave=" + formatHook(component.onmouseleave)); // if_setonmouseleave
        if (component.ontargetleave != null) lines.add("ontargetleave=" + formatHook(component.ontargetleave)); // if_setontargetleave
        if (component.ontargetenter != null) lines.add("ontargetenter=" + formatHook(component.ontargetenter)); // if_setontargetenter
        if (component.onvartransmit != null) lines.add("onvartransmit=" + formatHook(component.onvartransmit)); // if_setonvartransmit
        if (component.oninvtransmit != null) lines.add("oninvtransmit=" + formatHook(component.oninvtransmit)); // if_setoninvtransmit
        if (component.onstattransmit != null) lines.add("onstattransmit=" + formatHook(component.onstattransmit)); // if_setonstattransmit
        if (component.ontimer != null) lines.add("ontimer=" + formatHook(component.ontimer)); // if_setontimer
        if (component.onop != null) lines.add("onop=" + formatHook(component.onop)); // if_setonop
        if (component.onopt != null) lines.add("onopt=" + formatHook(component.onopt)); // if_setonopt
        if (component.onmouserepeat != null) lines.add("onmouserepeat=" + formatHook(component.onmouserepeat)); // if_setonmouserepeat
        if (component.onclick != null) lines.add("onclick=" + formatHook(component.onclick)); // if_setonclick
        if (component.onclickrepeat != null) lines.add("onclickrepeat=" + formatHook(component.onclickrepeat)); // if_setonclickrepeat
        if (component.onrelease != null) lines.add("onrelease=" + formatHook(component.onrelease)); // if_setonrelease
        if (component.onhold != null) lines.add("onhold=" + formatHook(component.onhold)); // if_setonhold
        if (component.ondrag != null) lines.add("ondrag=" + formatHook(component.ondrag)); // if_setondrag
        if (component.ondragcomplete != null) lines.add("ondragcomplete=" + formatHook(component.ondragcomplete)); // if_setondragcomplete
        if (component.ondragcancel != null) lines.add("ondragcancel=" + formatHook(component.ondragcancel));
        if (component.onscrollwheel != null) lines.add("onscrollwheel=" + formatHook(component.onscrollwheel)); // if_setonscrollwheel
        if (component.onvarctransmit != null) lines.add("onvarctransmit=" + formatHook(component.onvarctransmit)); // if_setonvarctransmit
        if (component.onvarcstrtransmit != null) lines.add("onvarcstrtransmit=" + formatHook(component.onvarcstrtransmit)); // if_setonvarcstrtransmit
        if (component.onbuttonpressed != null) lines.add("onbuttonpressed=" + formatHook(component.onbuttonpressed)); // 949 beta enum
        if (component.oncontentchanged != null) lines.add("oncontentchanged=" + formatHook(component.oncontentchanged)); // 949 beta enum
        if (component.onselectionchanged != null) lines.add("onselectionchanged=" + formatHook(component.onselectionchanged)); // 949 beta enum
        if (component.oncrmviewupdated != null) lines.add("oncrmviewupdated=" + formatHook(component.oncrmviewupdated)); // 949 beta enum
        if (component.onvartransmitlist != null) lines.add("onvartransmitlist=" + formatTransmitList(component.onvartransmitlist, Type.VAR_PLAYER));
        if (component.oninvtransmitlist != null) lines.add("oninvtransmitlist=" + formatTransmitList(component.oninvtransmitlist, Type.INV));
        if (component.onstattransmitlist != null) lines.add("onstattransmitlist=" + formatTransmitList(component.onstattransmitlist, Type.STAT));
        if (component.onvarctransmitlist != null) lines.add("onvarctransmitlist=" + formatTransmitList(component.onvarctransmitlist, Type.VAR_CLIENT));
        if (component.onvarcstrtransmitlist != null) lines.add("onvarcstrtransmitlist=" + formatTransmitList(component.onvarcstrtransmitlist, Type.VAR_CLIENT_STRING));

        return lines;
    }

    private static String unpackIfVar(int[] instructions) {
        var i = 0;
        var e = "";

        while (true) {
            var opcode = instructions[i++];

            if (opcode == 0) {
                break;
            } else if (opcode == 15) {
                e += " - ";
                continue;
            } else if (opcode == 16) {
                e += " / ";
                continue;
            } else if (opcode == 17) {
                e += " * ";
                continue;
            } else if (!e.isEmpty()) {
                e += " + ";
            }

            switch (opcode) {
                case 1 -> e += "stat(" + Unpacker.format(Type.STAT, instructions[i++]) + ")";
                case 2 -> e += "stat_base(" + Unpacker.format(Type.STAT, instructions[i++]) + ")";
                case 3 -> e += "stat_xp(" + Unpacker.format(Type.STAT, instructions[i++]) + ")";
                case 6 -> e += "stat_levelxp(" + instructions[i++] + ")";
                case 8 -> e += "stat_combat";
                case 9 -> e += "stat_total";

                case 4 -> e += "inv_total(" + Unpacker.format(Type.COMPONENT, (Unpack.VERSION < 400 ? 0 : instructions[i++]) << 16 | instructions[i++]) + ", " + Unpacker.format(Type.OBJ, instructions[i++]) + ")";
                case 10 -> e += "inv_contains(" + Unpacker.format(Type.COMPONENT, (Unpack.VERSION < 400 ? 0 : instructions[i++]) << 16 | instructions[i++]) + ", " + Unpacker.format(Type.OBJ, instructions[i++]) + ")";

                case 5 -> e += Unpacker.format(Type.VAR_PLAYER, instructions[i++]);
                case 14 -> e += Unpacker.format(Type.VAR_PLAYER_BIT, instructions[i++]);
                case 13 -> e += "testbit(" + Unpacker.format(Type.VAR_PLAYER, instructions[i++]) + ", " + instructions[i++] + ")";
                case 7 -> e += "fatigue(" + Unpacker.format(Type.VAR_PLAYER, instructions[i++]) + ")";

                case 11 -> e += "runenergy_visible";
                case 12 -> e += "runweight_visible";
                case 18 -> e += "coordx";
                case 19 -> e += "coordz";
                case 20 -> e += instructions[i++];

                default -> throw new IllegalStateException("unknown instruction " + opcode);
            }
        }

        return e;
    }

    private static String formatHook(Object[] hook) {
        var script = (Integer) hook[0];
        var arguments = new ArrayList<String>();

        for (var i = 0; i < hook.length - 1; ++i) {
            if (!ScriptUnpacker.SCRIPT_PARAMETERS.isEmpty()) {
                arguments.add(formatHookArgument(hook[i + 1], ScriptUnpacker.SCRIPT_PARAMETERS.get(script).get(i)));
            } else {
                arguments.add(String.valueOf(hook[i + 1])); // missing opcodes mode
            }
        }

        if (arguments.isEmpty()) {
            return Unpacker.format(Type.CLIENTSCRIPT, script);
        } else {
            return Unpacker.format(Type.CLIENTSCRIPT, script) + "(" + String.join(", ", arguments) + ")";
        }
    }

    private static String formatHookArgument(Object value, Type type) {
        type = ScriptUnpacker.chooseDisplayType(type);

        if (Objects.equals(value, "event_opbase")) return "event_opbase";
        if (Objects.equals(value, "event_text")) return "event_text";
        if (Objects.equals(value, Integer.MIN_VALUE + 1)) return "event_mousex";
        if (Objects.equals(value, Integer.MIN_VALUE + 2)) return "event_mousey";
        if (Objects.equals(value, Integer.MIN_VALUE + 3)) return "event_com";
        if (Objects.equals(value, Integer.MIN_VALUE + 4)) return "event_op";
        if (Objects.equals(value, Integer.MIN_VALUE + 5)) return "event_comsubid";
        if (Objects.equals(value, Integer.MIN_VALUE + 6)) return "event_com2";
        if (Objects.equals(value, Integer.MIN_VALUE + 7)) return "event_comsubid2";
        if (Objects.equals(value, Integer.MIN_VALUE + 8)) return "event_keycode";
        if (Objects.equals(value, Integer.MIN_VALUE + 9)) return "event_keychar";
        if (Objects.equals(value, Integer.MIN_VALUE + 10)) return "event_gamepadvalue";
        if (Objects.equals(value, Integer.MIN_VALUE + 11)) return "event_gamepadbutton";

        if (value instanceof Integer i) {
            return Unpacker.format(type, i);
        }

        return "\"" + value + "\"";
    }

    private static String formatTransmitList(int[] list, Type type) {
        var sb = new StringBuilder();

        for (var i = 0; i < list.length; ++i) {
            if (i > 0) {
                sb.append(",");
            }

            sb.append(Unpacker.format(type, list[i]));
        }

        return sb.toString();
    }

    private static void unpackLayer(ArrayList<String> lines, LayerComponent component) {
        if (component.scrollwidth != 0) lines.add("scrollwidth=" + component.scrollwidth); // if_getscrollwidth
        if (component.scrollheight != 0) lines.add("scrollheight=" + component.scrollheight); // if_getscrollheight
        if (component.margin[0] != 0 || component.margin[1] != 0 || component.margin[2] != 0 || component.margin[3] != 0) lines.add("margin=" + component.margin[0] + "," + component.margin[1] + "," + component.margin[2] + "," + component.margin[3]); // if_getmargi
    }

    private static void unpackInputBox(ArrayList<String> lines, InputBoxComponent component) {
        if (component.unknown200 != 0) lines.add("unknown200=" + component.unknown200);
        if (component.unknown201 != 0) lines.add("unknown201=" + component.unknown201);
        if (component.legacyfont != -1) lines.add("textfont=" + formatLegacyFont(component.legacyfont));
        if (component.textfont != -1) lines.add("textfont=" + Unpacker.format(Unpack.VERSION < 751 ? Type.GRAPHIC : Type.FONTMETRICS, component.textfont)); // if_settextfont
        if (component.textlineheight != 0) lines.add("textlineheight=" + component.textlineheight);
        if (component.textalignh != 0) lines.add("textalignh=" + component.textalignh); // if_settextalign
        if (component.textalignv != 0) lines.add("textalignv=" + component.textalignv); // if_settextalign
        if (component.textshadow) lines.add("textshadow=yes"); // if_settextshadow
        lines.add("colour=" + Unpacker.formatColour(component.colour)); // if_setcolour
    }

    private static void unpackInv(ArrayList<String> lines, InvComponent component) {
        if (component.draggable) lines.add("draggable=yes");
        if (component.interactable) lines.add("interactable=yes");
        if (component.usable) lines.add("usable=yes");
        if (component.swappable) lines.add("swappable=yes");
        if (component.paddingx != 0) lines.add("paddingx=" + component.paddingx);
        if (component.paddingy != 0) lines.add("paddingy=" + component.paddingy);

        for (var i = 0; i < component.sloticon.length; i++) {
            if (component.sloticon[i] != -1) {
                lines.add("slot" + (i + 1) + "=" + component.slotoffsetx[i] + "," + component.slotoffsety[i] + "," + Unpacker.format(Type.GRAPHIC, component.sloticon[i]));
            } else if (component.legacysloticon[i] != null) {
                lines.add("slot" + (i + 1) + "=" + component.slotoffsetx[i] + "," + component.slotoffsety[i] + "," + component.legacysloticon[i]);
            }
        }
    }

    private static void unpackGraphic(ArrayList<String> lines, GraphicComponent component) {
        unpackSpritePart("", lines, component.sprite);
    }

    private static void unpackInvText(ArrayList<String> lines, InvTextComponent component) {
        if (component.textalignh != 0) lines.add("textalignh=" + component.textalignh); // if_settextalign
        if (component.legacyfont != -1) lines.add("textfont=" + formatLegacyFont(component.legacyfont));
        if (component.textfont != -1) lines.add("textfont=" + Unpacker.format(Unpack.VERSION < 751 ? Type.GRAPHIC : Type.FONTMETRICS, component.textfont)); // if_settextfont
        if (component.textshadow) lines.add("textshadow=yes"); // if_settextshadow
        lines.add("colour=" + Unpacker.formatColour(component.colour)); // if_setcolour
        if (component.paddingx != 0) lines.add("paddingx=" + component.paddingx);
        if (component.paddingy != 0) lines.add("paddingy=" + component.paddingy);
        if (component.interactable) lines.add("interactable=yes");
    }

    private static void unpackTooltip(ArrayList<String> lines, TooltipComponent component) {
        if (!component.text.isEmpty()) lines.add("text=" + component.text); // if_settext
    }

    private static void unpackModel(ArrayList<String> lines, ModelComponent component) {
        lines.add("model=" + Unpacker.format(Type.MODEL, component.model));
        if (component.modelactive != -1) lines.add("modelactive=" + Unpacker.format(Type.MODEL, component.modelactive)); // if1 only

        if (component.modelorigin_x != 0 || component.modelorigin_y != 0 || component.modelorigin_z != 0) {
            if (!component.hasmodelorigin_z) {
                lines.add("modelorigin=" + component.modelorigin_x + "," + component.modelorigin_y); // if_setmodelorigin
            } else {
                lines.add("modelorigin=" + component.modelorigin_x + "," + component.modelorigin_y + "," + component.modelorigin_z); // if_setmodelorigin
            }
        }

        if (component.modelangle_x != 0 || component.modelangle_y != 0 || component.modelangle_z != 0) {
            lines.add("modelangle=" + component.modelangle_x + "," + component.modelangle_y + "," + component.modelangle_z); // if_setmodelangle
        }

        if (component.modelzoom != 100) lines.add("modelzoom=" + component.modelzoom); // if_setmodelzoom
        if (component.modelanim != -1) lines.add("modelanim=" + Unpacker.format(Type.SEQ, component.modelanim)); // if_setmodelanim
        if (component.modelanimactive != -1) lines.add("modelanimactive=" + Unpacker.format(Type.SEQ, component.modelanimactive)); // if1 only
        if (component.modelorthog) lines.add("modelorthog=yes"); // if_setmodelorthog
        if (component.unknown100 != 0) lines.add("unknown100=" + component.unknown100); // todo: correct default?
        if (component.unknown101 != 0) lines.add("unknown101=" + component.unknown101); // todo: correct default?
        if (component.unknown103) lines.add("unknown103=yes");
        if (component.modelprecisezoom) lines.add("modelprecisezoom=yes"); // todo
        if (component.modelnodepth) lines.add("modelnodepth=yes"); // todo
        if (component.modelobjwidth != 0) lines.add("modelobjwidth=" + component.modelobjwidth); // todo
        if (component.modelobjheight != 0) lines.add("modelobjheight=" + component.modelobjheight); // todo
    }

    private static void unpackText(ArrayList<String> lines, TextComponent component) {
        unpackTextPart("", lines, component.text);
    }

    private static void unpackRectangle(ArrayList<String> lines, RectangleComponent component) {
        lines.add("colour=" + Unpacker.formatColour(component.colour)); // if_setcolour
        if (component.fill) lines.add("fill=yes"); // if_setfill
        if (component.trans != 0) lines.add("trans=" + component.trans); // if_settrans
        if (component.colouractive != 0) lines.add("colouractive=" + Unpacker.formatColour(component.colouractive)); // if1 only
        if (component.mouseovercolour != 0) lines.add("mouseovercolour=" + Unpacker.formatColour(component.mouseovercolour)); // if1 only
        if (component.mouseovercolouractive != 0) lines.add("mouseovercolouractive=" + Unpacker.formatColour(component.mouseovercolouractive)); // if1 only
    }

    private static void unpackLine(ArrayList<String> lines, LineComponent component) {
        if (component.linewid != 1) lines.add("linewid=" + component.linewid); // if_setlinewid
        lines.add("colour=" + Unpacker.formatColour(component.colour)); // if_setcolour
        if (component.linedirection) lines.add("linedirection=yes"); // if_setlinedirection
    }

    private static void unpackButton(ArrayList<String> lines, ButtonComponent component) {
        if (!component.enabled) lines.add("enabled=no"); // if_setenabled
        if (component.cantoggle) lines.add("cantoggle=yes"); // if_button_setcantoggle
        if (component.unknown1 != 1) lines.add("unknown1=" + component.unknown1);
        if (!component.linkobjoption1) lines.add("linkobjoption1=no"); // if_button_setlinkobjoptions
        if (!component.linkobjoption2) lines.add("linkobjoption2=no"); // if_button_setlinkobjoptions
        var offsets = component.textareasizeoffsets; // if_button_settextareasizeoffsets

        if (offsets[0] != 0 || offsets[1] != 0 || offsets[2] != 0 || offsets[3] != 0) {
            lines.add("textareasizeoffsets=" + offsets[0] + "," + offsets[1] + "," + offsets[2] + "," + offsets[3]);
        }

        if (component.trans != 0) lines.add("trans=" + component.trans); // if_settrans
        if (component.colour != 0xffffff) lines.add("colour=" + Unpacker.formatColour(component.colour)); // if_setcolour
        unpackSpritePart("sprite.", lines, component.sprite); // todo
        unpackTextPart("text.", lines, component.text); // todo
    }

    private static void unpackPanel(ArrayList<String> lines, PanelComponent component) {
        if (component.scrollwidth != 0) lines.add("scrollwidth=" + component.scrollwidth); // if_setscrollsize
        if (component.scrollheight != 0) lines.add("scrollheight=" + component.scrollheight); // if_setscrollsize
        lines.add("isvertical=" + (component.isvertical ? "yes" : "no")); // if_panel_setisvertical
        lines.add("childspacing=" + component.childspacing); // if_setchildspacing
    }

    private static void unpackCheck(ArrayList<String> lines, CheckComponent component) {
        if (!component.enabled) lines.add("enabled=no"); // if_setenabled
        if (component.checked) lines.add("checked=yes"); // todo
        if (component.alignment != 0) lines.add("alignment=" + component.alignment); // if_check_setalignment
        if (component.buttonsize != 0) lines.add("buttonsize=" + component.buttonsize); // if_check_setbuttonsize
        if (component.trans != 0) lines.add("trans=" + component.trans); // if_settrans
        if (component.colour != 0xffffffff) lines.add("colour=" + Unpacker.formatColour(component.colour)); // if_setcolour
        unpackSpritePart("sprite.", lines, component.sprite); // todo
        unpackTextPart("text.", lines, component.text); // todo
    }

    private static void unpackInput(ArrayList<String> lines, InputComponent component) {
        lines.add("enabled=" + (component.enabled ? "yes" : "no")); // if_setenabled
        lines.add("filtermode=" + component.filtermode); // if_input_setup
        lines.add("visibilitymode=" + component.visibilitymode); // if_input_setup
        lines.add("unknown8=" + component.unknown8); // if_input_setup
        if (component.margin[0] != 0 || component.margin[1] != 0 || component.margin[2] != 0 || component.margin[3] != 0) lines.add("margin=" + component.margin[0] + "," + component.margin[1] + "," + component.margin[2] + "," + component.margin[3]);
        if (component.keyhandlingmode != 0) lines.add("keyhandlingmode=" + component.keyhandlingmode);
        if (component.trans != 0) lines.add("trans=" + component.trans); // if_settrans
        lines.add("colour=" + Integer.toHexString(component.colour)); // if_setcolour
        unpackSpritePart("sprite.", lines, component.sprite);
        unpackTextPart("text.", lines, component.text);
        unpackScrollbarPart("scrollbar.", lines, component.scrollbar);
    }

    private static void unpackGrid(ArrayList<String> lines, GridComponent component) {
        if (component.scrollwidth != 0) lines.add("scrollwidth=" + component.scrollwidth); // if_setscrollsize
        if (component.scrollheight != 0) lines.add("scrollheight=" + component.scrollheight); // if_setscrollsize
        lines.add("childspacing=" + component.childspacing); // if_setchildspacing
        lines.add("layoutparams_x=" + component.layoutparams_x); // if_grid_setlayoutparams
        lines.add("layoutparams_y=" + component.layoutparams_y); // if_grid_setlayoutparams
        lines.add("layoutparams_mode=" + (component.layoutparams_mode ? "yes" : "no")); // if_grid_setlayoutparams
    }

    private static void unpackList(ArrayList<String> lines, ListComponent component) {
        if (!component.enabled) lines.add("enabled=no");
        lines.add("dropdownnumentries=" + component.dropdownnumentries); // if_list_setdropdownnumentries
        lines.add("selectionlimit=" + component.selectionlimit); // if_list_setselectionlimit
        lines.add("entryheight=" + component.entryheight); // if_list_setentryheight
        if (component.entryiconscale != 100) lines.add("entryiconscale=" + component.entryiconscale); // if_list_setentryiconscale
        lines.add("dropdownbuttonparams=" + component.dropdownbuttonparams_size + "," + component.dropdownbuttonparams_offset); // if_list_setdropdownbuttonparams

        for (var value : component.unknown14) {
            lines.add("unknown14=" + value);
        }

        for (var value : component.unknown15) {
            lines.add("unknown15=" + value);
        }

        for (var value : component.unknown16) {
            lines.add("unknown16=" + value);
        }

        var margin1 = component.margin1;

        if (margin1[0] != 0 || margin1[1] != 0 || margin1[2] != 0 || margin1[3] != 0) {
            lines.add("margin1=" + margin1[0] + "," + margin1[1] + "," + margin1[2] + "," + margin1[3]);
        }

        var margin2 = component.margin2;

        if (margin2[0] != 0 || margin2[1] != 0 || margin2[2] != 0 || margin2[3] != 0) {
            lines.add("margin2=" + margin2[0] + "," + margin2[1] + "," + margin2[2] + "," + margin2[3]);
        }

        if (component.trans != 0) lines.add("trans=" + component.trans); // if_settrans
        if (component.colour != 0xffffffff) lines.add("colour=" + Unpacker.formatColour(component.colour)); // if_setcolour
        unpackSpritePart("button.", lines, component.button);
        unpackSpritePart("header.", lines, component.header);
        unpackSpritePart("body.", lines, component.body);
        unpackTextPart("text.", lines, component.text);
        unpackScrollbarPart("scrollbar.", lines, component.scrollbar);
    }

    private static void unpackCRMView(ArrayList<String> lines, CRMViewComponent component) {
        lines.add("unknown21=" + (component.unknown21 ? "yes" : "no"));

        for (var value : component.unknown22) {
            lines.add("unknown22=" + value);
        }

        lines.add("unknown23=" + component.unknown23);
        lines.add("unknown24=" + component.unknown24);

        for (var value : component.unknown25) {
            lines.add("unknown25=" + value);
        }

        for (var value : component.unknown26) {
            lines.add("unknown26=" + value);
        }
    }

    private static void unpackTextPart(String prefix, ArrayList<String> lines, TextPart text) {
        if (text.legacyfont != -1) lines.add(prefix + "textfont=" + formatLegacyFont(text.legacyfont));
        if (text.textfont != -1) lines.add(prefix + "textfont=" + Unpacker.format(Unpack.VERSION < 751 ? Type.GRAPHIC : Type.FONTMETRICS, text.textfont)); // if_settextfont
        if (!text.fontmono) lines.add(prefix + "fontmono=no"); // if_setfontmono
        if (!text.text.isEmpty()) lines.add(prefix + "text=" + text.text); // if_settext
        if (text.textactive != null && !text.textactive.isEmpty()) lines.add(prefix + "textactive=" + text.textactive); // if1 only
        if (text.textlineheight != 0) lines.add(prefix + "textlineheight=" + text.textlineheight); // todo
        if (text.textalignh != 0) lines.add(prefix + "textalignh=" + text.textalignh); // if_settextalign
        if (text.textalignv != 0) lines.add(prefix + "textalignv=" + text.textalignv); // if_settextalign
        if (text.textshadow) lines.add(prefix + "textshadow=yes"); // if_settextshadow
        if (text.colour != 0xffffff) lines.add(prefix + "colour=" + Unpacker.formatColour(text.colour)); // if_setcolour
        if (text.colouractive != 0) lines.add(prefix + "colouractive=" + Unpacker.formatColour(text.colouractive)); // if1 only
        if (text.mouseovercolour != 0) lines.add(prefix + "mouseovercolour=" + Unpacker.formatColour(text.mouseovercolour)); // if1 only
        if (text.mouseovercolouractive != 0) lines.add(prefix + "mouseovercolouractive=" + Unpacker.formatColour(text.mouseovercolouractive)); // if1 only
        if (text.trans != 0) lines.add(prefix + "trans=" + text.trans); // if_settrans
        if (text.maxlines != 0) lines.add(prefix + "maxlines=" + text.maxlines); // if_setmaxlines
    }

    private static String formatLegacyFont(int legacyfont) {
        return switch (legacyfont) {
            case 0 -> "p11_full";
            case 1 -> "p12_full";
            case 2 -> "b12_full";
            case 3 -> "q8_full";
            default -> throw new IllegalStateException("Unexpected value: " + legacyfont);
        };
    }

    private static void unpackSpritePart(String prefix, ArrayList<String> lines, SpritePart sprite) {
        if (sprite.graphic != -1) lines.add(prefix + "graphic=" + Unpacker.format(Type.GRAPHIC, sprite.graphic)); // if_setgraphic
        if (!sprite.legacygraphic.isEmpty()) lines.add(prefix + "graphic=" + sprite.legacygraphic); // if1 only
        if (sprite.graphicactive != -1) lines.add(prefix + "graphicactive=" + Unpacker.format(Type.GRAPHIC, sprite.graphicactive)); // if1 only
        if (!sprite.legacygraphicactive.isEmpty()) lines.add(prefix + "graphicactive=" + sprite.legacygraphicactive); // if1 only
        if (sprite.angle2d != 0) lines.add(prefix + "2dangle=" + sprite.angle2d); // if_set2dangle
        if (sprite.tiling) lines.add(prefix + "tiling=yes"); // if_settiling
        if (sprite.alpha) lines.add(prefix + "alpha=yes"); // if_setalpha
        if (sprite.trans != 0) lines.add(prefix + "trans=" + sprite.trans); // if_settrans
        if (sprite.outline != 0) lines.add(prefix + "outline=" + sprite.outline); // if_setoutline
        if (sprite.graphicshadow != 0) lines.add(prefix + "graphicshadow=" + sprite.graphicshadow); // if_setgraphicshadow
        if (sprite.vflip) lines.add(prefix + "vflip=yes"); // if_setvflip
        if (sprite.hflip) lines.add(prefix + "hflip=yes"); // if_sethflip
        if (sprite.colour != 0xffffff) lines.add(prefix + "colour=" + Unpacker.formatColour(sprite.colour)); // if_setcolour
        if (sprite.clickmask) lines.add(prefix + "clickmask=" + (sprite.clickmask ? "yes" : "no")); // if_setclickmask

        var edge = sprite.edge; // if_graphic_setedge

        if (edge[0] != 0 || edge[1] != 0 || edge[2] != 0 || edge[3] != 0) {
            lines.add(prefix + "edge=" + edge[0] + "," + edge[1] + "," + edge[2] + "," + edge[3]);
        }
    }

    private static void unpackScrollbarPart(String prefix, ArrayList<String> lines, ScrollbarPart scrollbar) {
        lines.add(prefix + "unknown17=" + (scrollbar.unknown17 ? "yes" : "no"));
        lines.add(prefix + "unknown18=" + (scrollbar.unknown18 ? "yes" : "no"));
        lines.add(prefix + "unknown19=" + scrollbar.unknown19);
        lines.add(prefix + "unknown20=" + scrollbar.unknown20);
        unpackSpritePart(prefix + "background.", lines, scrollbar.background);
        unpackSpritePart(prefix + "button.", lines, scrollbar.button);
        unpackSpritePart(prefix + "handle.", lines, scrollbar.handle);
    }

    public static String formatSizeMode(int widthmode) {
        return switch (widthmode) {
            case 0 -> "abs";
            case 1 -> "minus";
            case 2 -> "proportion";
            case 3 -> "mode_3";
            case 4 -> "aspect";
            default -> throw new IllegalStateException("Unexpected value: " + widthmode);
        };
    }

    public static String formatXMode(int widthmode) {
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

    public static String formatYMode(int widthmode) {
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

    public static String formatIfType(int type) {
        return switch (type) {
            case 0 -> "layer";
            case 1 -> "inputbox";
            case 2 -> "inv";
            case 3 -> "rectangle";
            case 4 -> "text";
            case 5 -> "graphic";
            case 6 -> "model";
            case 7 -> "invtext";
            case 8 -> "tooltip";
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
}
