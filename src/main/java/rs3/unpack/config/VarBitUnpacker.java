package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.unpack.VarDomain;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

                    lines.addFirst("[" + Unpacker.format(Unpacker.getVarBitDomain(id).bittype, id) + "]");
                    return lines;
                }

                case 1 -> {
                    var domain = VarDomain.byID(packet.g1());
                    Unpacker.setVarBitDomain(id, domain);
                    lines.add("domain=" + domain.name().toLowerCase(Locale.ROOT));
                    lines.add("basevar=" + Unpacker.format(domain.type, packet.gSmart2or4null()));
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
