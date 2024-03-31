package rs3.avatarviewer;

import rs3.util.Packet;

import java.util.Arrays;
import java.util.Base64;

public class PlayerAppearance {
    public static final int[] HIDDEN = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0};

    public int gender;
    public final int[] worn = new int[19];
    public final int[] kit = new int[19];
    public int transmog = -1;
    public int team = 0;
    public int[] recol = new int[10];
    public int[] retex = new int[10];
    public int bas;

    public static void main(String[] args) {
        var data = Base64.getDecoder().decode("AOQvnGtZuafTph2n1wCmGQCmDaYOAAAAAAAAAAAzLy1YlJQAAAAAAAAAAAAAAAAAADQ");
        var appearance = new PlayerAppearance(data);
        new PlayerAppearance(appearance.encode());
        System.out.println();
    }

    public PlayerAppearance() {

    }

    public PlayerAppearance(byte[] data) {
        var packet = new Packet(data);
        this.gender = packet.g1s();

        for (var slot = 0; slot < worn.length; slot++) {
            if (HIDDEN[slot] == 1) {
                worn[slot] = -1;
                kit[slot] = -1;
                continue;
            }

            var a = packet.g1();

            if (a == 0) {
                worn[slot] = -1;
            } else {
                var b = packet.g1();
                var obj = (a << 8) + b;

                if (slot == 0 && obj == 65535) {
                    transmog = packet.gSmart2or4null();
                    team = packet.g1();
                    break;
                }

                if (obj >= 2048) {
                    worn[slot] = obj - 2048;
                    kit[slot] = -1;
                } else {
                    worn[slot] = -1;
                    kit[slot] = obj - 256;
                }
            }
        }

        if (transmog == -1) {
            var customizations = packet.g2();

            if (customizations != 0) {
                throw new IllegalStateException("not supported");
            }
        }

        for (var i = 0; i < 10; ++i) {
            recol[i] = packet.g1();
        }

        for (var i = 0; i < 10; ++i) {
            retex[i] = packet.g1();
        }

        bas = packet.g2null();

        if (packet.pos != packet.arr.length) {
            throw new IllegalStateException("end not reached");
        }
    }

    public byte[] encode() {
        var packet = new Packet(100);
        packet.p1(gender);

        if (transmog != -1) {
            packet.pSmart2or4null(transmog);
            packet.p1(team);
        } else {
            for (var slot = 0; slot < worn.length; slot++) {
                if (HIDDEN[slot] == 1) {
                    continue;
                }

                if (worn[slot] != -1) {
                    packet.p2(2048 + worn[slot]);
                } else if (kit[slot] != -1) {
                    packet.p2(256 + kit[slot]);
                } else {
                    packet.p1(0);
                }
            }

            packet.p2(0); // customisations
        }

        for (var i = 0; i < 10; ++i) {
            packet.p1(recol[i]);
        }

        for (var i = 0; i < 10; ++i) {
            packet.p1(retex[i]);
        }

        packet.p2(bas);
        return Arrays.copyOfRange(packet.arr, 0, packet.pos);
    }
}
