package rs3.unpack.config;

import rs3.Unpack;
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

                lines.addFirst("[" + Unpacker.format(domain.type, id) + "]");
                return lines;
            }

            case 3 -> {
                var type = Type.byID(packet.g1());
                Unpacker.setVarType(domain, id, type);
                lines.add("type=" + type);
            }

            case 4 -> {
                if (Unpack.VERSION < 762) {
                    lines.add("lifetime=perm");
                } else {
                    lines.add("lifetime=" + switch (packet.g1()) {
                        case 0 -> "temp"; // https://twitter.com/JagexAsh/status/654366476674183168
                        case 1 -> "perm"; // https://twitter.com/JagexAsh/status/654366476674183168
                        case 2 -> "serverperm";
                        default -> throw new IllegalStateException("invalid lifetime");
                    });
                }
            }

            case 5 -> lines.add("transmitlevel=" + switch (packet.g1()) {
                case 0 -> "never";
                case 1 -> "on_set_different";
                case 2 -> "on_set_always";
                default -> throw new IllegalStateException("invalid transmitlevel");
            });

            case 110 -> lines.add("clientcode=" + packet.g2());
            case 7 -> lines.add("domaindefault=no");
            case 8 -> lines.add("wikisync=yes");

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
