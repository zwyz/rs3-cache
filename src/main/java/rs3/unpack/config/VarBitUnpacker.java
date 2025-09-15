package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.unpack.VarDomain;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class VarBitUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);

        while (true) {
            switch (packet.g1()) {
                case 0 -> {
                    if (packet.pos != packet.arr.length) {
                        throw new IllegalStateException("end of file not reached");
                    }

                    lines.addFirst("[" + Unpacker.format(Type.VARBIT, id) + "]");
                    return lines;
                }

                case 1 -> {
                    var domain = packet.g1();
                    Unpacker.setVarBitDomain(id, VarDomain.byID(domain));
                    lines.add("domain=" + Unpacker.formatVarDomain(domain));
                    lines.add("basevar=" + Unpacker.formatVar(VarDomain.byID(domain), packet.gSmart2or4null()));
                }

                case 2 -> {
                    lines.add("startbit=" + packet.g1());
                    lines.add("endbit=" + packet.g1());
                }

                case 16 -> lines.add("wikisync=yes");

                default -> throw new IllegalStateException("unknown opcode");
            }
        }
    }
}
