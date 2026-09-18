package rs3.unpack.interfaces;

import rs3.Unpack;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Component {
    public int id;
    public int version;
    public int type;
    public int buttontype;
    public int clientcode;
    public String name;

    public int x;
    public int y;
    public int width;
    public int height;
    public int widthmode;
    public int heightmode;
    public int xmode;
    public int ymode;
    public int aspectwidth = 1;
    public int aspectheight = 1;
    public int layer = -1;
    public boolean hide;
    public boolean noclickthrough;
    public int stylesheet = -1;
    public int unknown10;

    // if1 only
    public int legacytrans;
    public int mouseoverlayer = -1;
    public int[] scriptComparison = new int[0];
    public int[] scriptComparisonValue = new int[0];
    public int[][] scriptInstructions = new int[0][];
    public String targetbase = "";
    public String buttontext = "";

    // ops
    public String opbase = "";
    public String pausetext = "";
    public String targetverb = "";
    public String[] ops = new String[0];
    public List<OpKeyEntry> opkeys = new ArrayList<>();
    public int[] opcursors = new int[0];
    public int mouseovercursor = -1;
    public int targetcursor1 = -1;
    public int targetcursor2 = -1;
    public int targetcursor3 = -1;
    public int dragrenderbehaviour;
    public int dragdeadzone;
    public int dragdeadtime;
    public int events;
    public List<ParamEntry> params = new ArrayList<>();
    public Object[] onload;
    public Object[] onmouseover;
    public Object[] onmouseleave;
    public Object[] ontargetleave;
    public Object[] ontargetenter;
    public Object[] onvartransmit;
    public Object[] oninvtransmit;
    public Object[] onstattransmit;
    public Object[] ontimer;
    public Object[] onop;
    public Object[] onopt;
    public Object[] onmouserepeat;
    public Object[] onclick;
    public Object[] onclickrepeat;
    public Object[] onrelease;
    public Object[] onhold;
    public Object[] ondrag;
    public Object[] ondragcomplete;
    public Object[] ondragcancel;
    public Object[] onscrollwheel;
    public Object[] onvarctransmit;
    public Object[] onvarcstrtransmit;
    public Object[] onbuttonpressed;
    public Object[] oncontentchanged;
    public Object[] onselectionchanged;
    public Object[] oncrmviewupdated;
    public int[] onvartransmitlist;
    public int[] oninvtransmitlist;
    public int[] onstattransmitlist;
    public int[] onvarctransmitlist;
    public int[] onvarcstrtransmitlist;

    public static Component create(int type) {
        return switch (type) {
            case 0 -> new LayerComponent();
            case 1 -> new InputBoxComponent();
            case 2 -> new InvComponent();
            case 3 -> new RectangleComponent();
            case 4 -> new TextComponent();
            case 5 -> new GraphicComponent();
            case 6 -> new ModelComponent();
            case 7 -> new InvTextComponent();
            case 8 -> new TooltipComponent();
            case 9 -> new LineComponent();
            case 10 -> new ButtonComponent();
            case 11 -> new PanelComponent();
            case 12 -> new CheckComponent();
            case 13 -> new InputComponent();
            case 14 -> new SliderComponent();
            case 15 -> new GridComponent();
            case 16 -> new ListComponent();
            case 17 -> new ComboComponent();
            case 18 -> new PagedLayerComponent();
            case 19 -> new PagedLayerHeaderComponent();
            case 20 -> new CarouselComponent();
            case 21 -> new PagedCarouselComponent();
            case 22 -> new RadioGroupComponent();
            case 23 -> new GroupBoxComponent();
            case 24 -> new RadialProgressOverlayComponent();
            case 26 -> new CRMViewComponent();
            case 27 -> new CutsceneLayerComponent();
            case 28 -> new ModelGroupComponent();
            default -> throw new AssertionError("invalid type " + type);
        };
    }

    public static Component decode(int id, Packet packet) {
        Component component;

        if (Unpack.VERSION < 566 && (packet.arr[packet.pos] & 0xff) != 0xff) {
            var type = packet.g1();
            var buttontype = packet.g1();
            var clientcode = packet.g2();

            component = create(type);
            component.id = id;
            component.version = -2;
            component.type = type;
            component.buttontype = buttontype;
            component.clientcode = clientcode;
            component.decodeOld(packet);
        } else {
            var version = packet.g1();
            var value = packet.g1();
            var type = value & 127;
            var name = (value & 128) != 0 ? packet.gjstr() : null;

            component = create(type);
            component.id = id;
            component.version = version == 255 ? -1 : version;
            component.type = type;
            component.name = name;
            component.clientcode = packet.g2();
            component.decodeNew(packet);
        }

        return component;
    }

    public void decodeOld(Packet packet) {
        if (Unpack.VERSION >= 400) {
            x = packet.g2s();
            y = packet.g2s();
        }

        width = packet.g2();
        height = packet.g2();
        legacytrans = packet.g1();

        if (Unpack.VERSION >= 400) {
            layer = packet.g2null();
            mouseoverlayer = packet.g2null();
        } else {
            mouseoverlayer = packet.g2special();
        }

        var comparisoncount = packet.g1();
        scriptComparison = new int[comparisoncount];
        scriptComparisonValue = new int[comparisoncount];

        for (var i = 0; i < comparisoncount; i++) {
            scriptComparison[i] = packet.g1();
            scriptComparisonValue[i] = packet.g2();
        }

        var instructioncount = packet.g1();
        scriptInstructions = new int[instructioncount][];

        for (var i = 0; i < instructioncount; i++) {
            scriptInstructions[i] = new int[packet.g2()];

            for (var j = 0; j < scriptInstructions[i].length; j++) {
                scriptInstructions[i][j] = packet.g2null();
            }
        }

        decodeSpecific(packet);

        if (buttontype == 2 || type == 2) {
            targetverb = packet.gjstr();
            targetbase = packet.gjstr();
            events |= (packet.g2() & 63) << 11;
        }

        if (buttontype == 1 || buttontype == 4 || buttontype == 5 || buttontype == 6) {
            buttontext = packet.gjstr();
        }
    }

    public void decodeNew(Packet packet) {
        x = packet.g2s();
        y = packet.g2s();
        width = packet.g2();
        height = packet.g2();

        if (Unpack.VERSION >= 493) {
            widthmode = packet.g1s();
            heightmode = packet.g1s();
            xmode = packet.g1s();
            ymode = packet.g1s();
        }

        if (widthmode == 4 || heightmode == 4) {
            aspectwidth = packet.g2();
            aspectheight = packet.g2();
        }

        layer = packet.g2null();

        var flags = packet.g1();
        hide = (flags & 1) != 0;

        if (version >= 0) {
            noclickthrough = (flags & 2) != 0;
        }

        decodeSpecific(packet);

        if (version >= 6) {
            stylesheet = packet.g4s();
        }

        if (version >= 9) {
            unknown10 = packet.g1();
        }

        if (version < 6) {
            events = packet.g3();
        } else {
            events = packet.g4s();
        }

        if (Unpack.VERSION < 499) {
            // nothing
        } else if (Unpack.VERSION < 530) {
            var count = packet.g1();
            var keys = new int[count];
            var mods = new int[count];

            for (var index = 0; index < count; index++) {
                keys[index] = packet.g1s();
            }

            if (count > 0 && Unpack.VERSION >= 509) {
                var modcount = packet.g1();

                for (var index = 0; index < modcount; index++) {
                    mods[index] = packet.g1s();
                }
            }

            for (var i = 0; i < count; i++) {
                opkeys.add(new OpKeyEntry(i + 1, keys[i], mods[i], -1));
            }
        } else {
            var value = packet.g1();

            while (value != 0) {
                var op = (value >> 4);
                var rate = (value << 8 | packet.g1()) & 4095;
                if (rate == 4095) rate = -1;
                var key = packet.g1s();
                var mods = packet.g1s();

                opkeys.add(new OpKeyEntry(op, key, mods, rate));
                value = packet.g1();
            }
        }

        opbase = packet.gjstr();
        var var14 = packet.g1();
        var opcount = var14 & 15;
        var opcursorcount = var14 >> 4;
        ops = new String[opcount];
        opcursors = new int[opcount];
        Arrays.fill(opcursors, -1);

        for (var i = 0; i < opcount; ++i) {
            ops[i] = packet.gjstr();
        }

        for (var i = 0; i < opcursorcount; i++) {
            opcursors[packet.g1()] = packet.g2();
        }

        if (Unpack.VERSION >= 537) {
            pausetext = packet.gjstr();
        }

        dragdeadzone = packet.g1();
        dragdeadtime = packet.g1();
        dragrenderbehaviour = packet.g1();
        targetverb = packet.gjstr();

        if (Unpack.VERSION >= 530 && ((events >>> 11) & 0b1111111) != 0) {
            targetcursor1 = packet.g2null();
            targetcursor2 = packet.g2null();
            targetcursor3 = packet.g2null();
        }

        if (version >= 0) {
            mouseovercursor = packet.g2null();
        }

        if (version >= 0) {
            var intparamcount = packet.g1();

            for (var i = 0; i < intparamcount; ++i) {
                params.add(new ParamEntry(packet.g3(), packet.g4s()));
            }

            var stringparamcount = packet.g1();

            for (var i = 0; i < stringparamcount; ++i) {
                params.add(new ParamEntry(packet.g3(), packet.gjstr2()));
            }
        }

        onload = decodeHook(packet);
        onmouseover = decodeHook(packet);
        onmouseleave = decodeHook(packet);
        ontargetleave = decodeHook(packet);
        ontargetenter = decodeHook(packet);
        onvartransmit = decodeHook(packet);
        oninvtransmit = decodeHook(packet);
        onstattransmit = decodeHook(packet);
        ontimer = decodeHook(packet);
        onop = decodeHook(packet);

        if (version >= 0) {
            onopt = decodeHook(packet);
        }

        onmouserepeat = decodeHook(packet);
        onclick = decodeHook(packet);
        onclickrepeat = decodeHook(packet);
        onrelease = decodeHook(packet);
        onhold = decodeHook(packet);
        ondrag = decodeHook(packet);
        ondragcomplete = decodeHook(packet);

        if (Unpack.VERSION < 459) {
            ondragcancel = decodeHook(packet);
        }

        onscrollwheel = decodeHook(packet);

        if (Unpack.VERSION >= 506) {
            onvarctransmit = decodeHook(packet);
            onvarcstrtransmit = decodeHook(packet);
        }

        if (version >= 6) {
            onbuttonpressed = decodeHook(packet);
            oncontentchanged = decodeHook(packet);
            onselectionchanged = decodeHook(packet);
        }

        if (version >= 8) {
            oncrmviewupdated = decodeHook(packet);
        }

        if (Unpack.VERSION >= 459) {
            onvartransmitlist = decodeHookTransmitList(packet);
            oninvtransmitlist = decodeHookTransmitList(packet);
            onstattransmitlist = decodeHookTransmitList(packet);
        }

        if (Unpack.VERSION >= 506) {
            onvarctransmitlist = decodeHookTransmitList(packet);
            onvarcstrtransmitlist = decodeHookTransmitList(packet);
        }
    }

    protected abstract void decodeSpecific(Packet packet);

    private static Object[] decodeHook(Packet packet) {
        var count = packet.g1();

        if (count == 0) {
            return null;
        }

        packet.g1();
        var hook = new Object[count];
        hook[0] = packet.g4s();

        for (var i = 0; i < count - 1; ++i) {
            var type = packet.g1();

            hook[i + 1] = switch (type) {
                case 0 -> packet.g4s();
                case 1 -> packet.gjstr();
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };
        }

        return hook;
    }

    private static int[] decodeHookTransmitList(Packet packet) {
        var count = packet.g1();

        if (count == 0) {
            return null;
        }

        var list = new int[count];

        for (var i = 0; i < count; ++i) {
            list[i] = packet.g4s();
        }

        return list;
    }

    public record OpKeyEntry(int op, int key, int mods, int rate) {

    }

    public record ParamEntry(int param, Object value) {}
}
