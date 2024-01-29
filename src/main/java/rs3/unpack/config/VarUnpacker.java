package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.unpack.VarDomain;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class VarUnpacker {
    public static List<String> unpack(VarDomain domain, int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                lines.addFirst("[" + Unpacker.formatVar(domain, id) + "]");
                return lines;
            }

            case 3 -> {
                var type = packet.g1();
                Unpacker.setVarType(domain, id, Type.byID(type));
                lines.add("type=" + Unpacker.format(Type.TYPE, type));
            }

            case 4 -> lines.add("lifetime=" + Unpacker.formatVarLifetime(packet.g1()));
            case 5 -> lines.add("transmitlevel=" + Unpacker.formatTransmitLevel(packet.g1()));
            case 110 -> lines.add("clientcode=" + packet.g2());
            case 7 -> lines.add("domaindefault=no");

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
