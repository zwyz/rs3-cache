package rs3.unpack.interfaces;

import rs3.util.Packet;

public class ListComponent extends Component {
    public boolean enabled;
    public int dropdownnumentries;
    public int selectionlimit;
    public int entryheight;
    public int entryiconscale;
    public int dropdownbuttonparams_size;
    public int dropdownbuttonparams_offset;
    public String[] unknown14 = new String[0];
    public int[] unknown15 = new int[0];
    public int[] unknown16 = new int[0];
    public int[] margin1 = {0, 0, 0, 0};
    public int[] margin2 = {0, 0, 0, 0};
    public int trans;
    public int colour;
    public SpritePart button = new SpritePart();
    public SpritePart header = new SpritePart();
    public SpritePart body = new SpritePart();
    public TextPart text = new TextPart();
    public ScrollbarPart scrollbar = new ScrollbarPart();

    @Override
    protected void decodeSpecific(Packet packet) {
        enabled = packet.g1() == 1;
        dropdownnumentries = packet.g1();
        selectionlimit = packet.g1();
        entryheight = packet.g1();

        if (version >= 9) {
            entryiconscale = packet.g1();
        }

        dropdownbuttonparams_size = packet.g1();
        dropdownbuttonparams_offset = packet.g1();

        unknown14 = new String[packet.g2()];

        for (var i = 0; i < unknown14.length; i++) {
            unknown14[i] = packet.gjstr();
        }

        unknown15 = new int[packet.g2()];

        for (var i = 0; i < unknown15.length; i++) {
            unknown15[i] = packet.g4s();
        }

        unknown16 = new int[packet.g2()];

        for (var i = 0; i < unknown16.length; i++) {
            unknown16[i] = packet.g2();
        }

        if (version >= 9) {
            margin1 = new int[]{packet.g1(), packet.g1(), packet.g1(), packet.g1()};
            margin2 = new int[]{packet.g1(), packet.g1(), packet.g1(), packet.g1()};
        }

        trans = packet.g1();
        colour = packet.g4s();
        button.decode(packet, version);
        header.decode(packet, version);
        body.decode(packet, version);
        text.decode(packet, version);
        scrollbar.decode(packet, version);
    }
}
