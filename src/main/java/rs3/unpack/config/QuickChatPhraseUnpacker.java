package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
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
                        case 6 -> "enum_string," + Unpacker.format(Type.ENUM, packet.g2()) + "," + Unpacker.format(Type.VAR_PLAYER, packet.g2()); // nxt name
                        case 7 -> "enum_string_clan," + Unpacker.format(Type.ENUM, packet.g2()); // nxt name
                        case 8 -> "var_player_int," + Unpacker.format(Type.VAR_PLAYER, packet.g2()); // 216 TOSTRING_VARP
                        case 9 -> "var_player_bit," + Unpacker.format(Type.VAR_PLAYER_BIT, packet.g2()); // 216 TOSTRING_VARBIT
                        case 10 -> "objtradedialog"; // nxt name
                        case 11 -> "enum_string_statbase," + Unpacker.format(Type.ENUM, packet.g2()) + "," + Unpacker.format(Type.STAT, packet.g2()); // nxt name
                        case 12 -> "acc_getcount_world"; // 216 ACC_GETCOUNT_WORLD
                        case 13 -> "acc_getmeancombatlevel"; // 216 ACC_GETMEANCOMBATLEVEL
                        case 14 -> "var_world_int," + Unpacker.format(Type.VAR_WORLD, packet.g2()); // 216 TOSTRING_SHARED
                        case 15 -> "combat_level"; // 216 ACTIVECOMBATLEVEL
                        case 16 -> "enum_string_var_player_bit," + Unpacker.format(Type.ENUM, packet.g2()) + "," + Unpacker.format(Type.VAR_PLAYER_BIT, packet.g2()); // 216 ENUM_STRING_VARBIT
                        default -> throw new IllegalStateException("invalid dynamiccommand " + command);
                    });
                }
            }

            case 4 -> lines.add("searchable=no"); // 216 IsSearchable

            case 5 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    var command = packet.g2();

                    lines.add("dynamiccommand=" + switch (command) {
                        case 0 -> "listdialog," + Unpacker.format(Type.ENUM, packet.gvarint2()); // nxt name
                        case 1 -> "objdialog"; // nxt name
                        case 2 -> "countdialog";
                        case 4 -> "stat_base," + Unpacker.format(Type.STAT, packet.gvarint2());
                        case 6 -> "enum_string," + Unpacker.format(Type.ENUM, packet.gvarint2()) + "," + Unpacker.format(Type.VAR_PLAYER, packet.gvarint2()); // nxt name
                        case 7 -> "enum_string_clan," + Unpacker.format(Type.ENUM, packet.gvarint2()); // nxt name
                        case 8 -> "var_player_int," + Unpacker.format(Type.VAR_PLAYER, packet.gvarint2()); // 216 TOSTRING_VARP
                        case 9 -> "var_player_bit," + Unpacker.format(Type.VAR_PLAYER_BIT, packet.gvarint2()); // 216 TOSTRING_VARBIT
                        case 10 -> "objtradedialog"; // nxt name
                        case 11 -> "enum_string_statbase," + Unpacker.format(Type.ENUM, packet.gvarint2()) + "," + Unpacker.format(Type.STAT, packet.gvarint2()); // nxt name
                        case 12 -> "acc_getcount_world"; // 216 ACC_GETCOUNT_WORLD
                        case 13 -> "acc_getmeancombatlevel"; // 216 ACC_GETMEANCOMBATLEVEL
                        case 14 -> "var_world_int," + Unpacker.format(Type.VAR_WORLD, packet.gvarint2()); // 216 TOSTRING_SHARED
                        case 15 -> "combat_level"; // 216 ACTIVECOMBATLEVEL
                        case 16 -> "enum_string_var_player_bit," + Unpacker.format(Type.ENUM, packet.gvarint2()) + "," + Unpacker.format(Type.VAR_PLAYER_BIT, packet.gvarint2()); // 216 ENUM_STRING_VARBIT
                        default -> throw new IllegalStateException("invalid dynamiccommand " + command);
                    });
                }
            }
            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
