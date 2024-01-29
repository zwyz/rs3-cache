package rs3.unpack.map;

import rs3.unpack.Vector3;
import rs3.util.Packet;

public class Environment {
    public final EnvironmentA unknown1;
    public final EnvironmentB unknown2;
    public final EnvironmentC unknown3;
    public final EnvironmentD unknown4;
    public final EnvironmentE unknown5;
    public final EnvironmentF unknown6;
    public final int unknown7;
    public final int unknown8;
    public final boolean unknown9;
    public final EnvironmentG unknown10;
    public final float unknown11;
    public final Vector3 unknown12;

    public Environment(Packet packet) {
        this.unknown1 = new EnvironmentA(packet);
        this.unknown2 = new EnvironmentB(packet);
        this.unknown3 = new EnvironmentC(packet);
        this.unknown4 = new EnvironmentD(packet);
        this.unknown5 = new EnvironmentE(packet);
        this.unknown6 = new EnvironmentF(packet);
        this.unknown7 = packet.g2();
        this.unknown8 = packet.g2();
        this.unknown9 = packet.g1() == 1;
        this.unknown10 = new EnvironmentG(packet);
        this.unknown11 = packet.gFloat();
        this.unknown12 = new Vector3(packet);
    }
}
