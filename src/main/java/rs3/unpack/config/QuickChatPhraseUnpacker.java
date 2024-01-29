package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.unpack.VarDomain;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class QuickChatPhraseUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.CHATPHRASE, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("template=" + packet.gjstr());

            case 2 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("autoresponse=" + Unpacker.format(Type.CHATPHRASE, packet.g2()));
                }
            }

            case 3 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    var command = packet.g2();

                    lines.add("dynamiccommand=" + switch (command) {
                        case 0 -> "listdialog," + Unpacker.format(Type.ENUM, packet.g2()); // nxt name
                        case 1 -> "objdialog"; // nxt name
                        case 2 -> "countdialog";
                        case 4 -> "stat_base," + Unpacker.format(Type.STAT, packet.g2());
                        case 6 -> "enum_string," + Unpacker.format(Type.ENUM, packet.g2()) + "," + Unpacker.formatVar(VarDomain.PLAYER, packet.g2()); // nxt name
                        case 7 -> "enum_string_clan," + Unpacker.format(Type.ENUM, packet.g2()); // nxt name
                        case 8 -> "var_player_int," + Unpacker.formatVar(VarDomain.PLAYER, packet.g2());
                        case 9 -> "var_player_bit," + Unpacker.formatVarBit(packet.g2());
                        case 10 -> "objtradedialog"; // nxt name
                        case 11 -> "enum_string_statbase," + Unpacker.format(Type.ENUM, packet.g2()) + "," + Unpacker.format(Type.STAT, packet.g2()); // nxt name
                        case 12 -> "unknown_12";
                        case 13 -> "unknown_13";
                        case 14 -> "var_world_int," + Unpacker.formatVar(VarDomain.WORLD, packet.g2());
                        case 15 -> "combat_level";
                        case 16 -> "enum_string_var_player_bit," + Unpacker.format(Type.ENUM, packet.g2()) + "," + Unpacker.formatVarBit(packet.g2());
                        default -> throw new IllegalStateException("invalid dynamiccommand " + command);
                    });
                }
            }

            case 4 -> lines.add("unknown4=no");

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
