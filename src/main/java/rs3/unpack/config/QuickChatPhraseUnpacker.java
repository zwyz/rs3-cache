package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class QuickChatPhraseUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        String template = null;
        var commands = new ArrayList<String>();
        lines.add("[" + Unpacker.format(Type.CHATPHRASE, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                if (template != null) {
                    lines.add(1, "phrase=" + formatPhrase(template, commands));
                } else if (!commands.isEmpty()) {
                    throw new IllegalStateException("dynamic commands without phrase text");
                }

                return lines;
            }

            case 1 -> template = packet.gjstr();

            case 2 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    lines.add("autoresponse=" + Unpacker.format(Type.CHATPHRASE, packet.g2()));
                }
            }

            case 3 -> unpackCommands(packet, false, commands);
            case 4 -> lines.add("searchable=no"); // 216 IsSearchable
            case 5 -> unpackCommands(packet, true, commands);

            default -> throw new IllegalStateException("unknown opcode");
        }
    }

    private static void unpackCommands(Packet packet, boolean wide, List<String> commands) {
        var count = packet.g1();
        commands.clear();
        for (var i = 0; i < count; i++) {
            commands.add(unpackCommand(packet, wide));
        }
    }

    private static String unpackCommand(Packet packet, boolean wide) {
        var command = packet.g2();
        IntSupplier parameter = wide ? packet::gvarint2 : packet::g2;

        return switch (command) {
            case 0 -> "listdialog(" + formatParameter(Type.ENUM, parameter) + ")"; // 216 LISTDIALOG
            case 1 -> "objdialog()"; // 216 OBJDIALOG
            case 2 -> "countdialog()"; // 216 COUNTDIALOG
            case 4 -> "stat_base(" + formatParameter(Type.STAT, parameter) + ")"; // 216 STAT_BASE
            case 6 -> "enum_string(" + formatParameter(Type.ENUM, parameter) + "," + formatParameter(Type.VAR_PLAYER, parameter) + ")"; // 216 ENUM_STRING
            case 7 -> "enum_string_clan(" + formatParameter(Type.ENUM, parameter) + ")"; // 216 ENUM_STRING_CLAN
            case 8 -> "tostring_varp(" + formatParameter(Type.VAR_PLAYER, parameter) + ")"; // 216 TOSTRING_VARP
            case 9 -> "tostring_varbit(" + formatParameter(Type.VAR_PLAYER_BIT, parameter) + ")"; // 216 TOSTRING_VARBIT
            case 10 -> "objtradedialog()"; // 216 OBJTRADEDIALOG
            case 11 -> "enum_string_statbase(" + formatParameter(Type.ENUM, parameter) + "," + formatParameter(Type.STAT, parameter) + ")"; // 216 ENUM_STRING_STATBASE
            case 12 -> "acc_getcount_world()"; // 216 ACC_GETCOUNT_WORLD
            case 13 -> "acc_getmeancombatlevel()"; // 216 ACC_GETMEANCOMBATLEVEL
            case 14 -> "tostring_shared(" + formatParameter(Type.VAR_WORLD, parameter) + ")"; // 216 TOSTRING_SHARED
            case 15 -> "activecombatlevel()"; // 216 ACTIVECOMBATLEVEL
            case 16 -> "enum_string_varbit(" + formatParameter(Type.ENUM, parameter) + "," + formatParameter(Type.VAR_PLAYER_BIT, parameter) + ")"; // 216 ENUM_STRING_VARBIT
            default -> throw new IllegalStateException("invalid dynamiccommand " + command);
        };
    }

    private static String formatParameter(Type type, IntSupplier parameter) {
        var name = Unpacker.format(type, parameter.getAsInt());
        //noinspection ConstantValue
        if (false) {
            return type == Type.VAR_PLAYER || type == Type.VAR_PLAYER_BIT || type == Type.VAR_WORLD ? "%" + name : name;
        }
        return name;
        
    }

    private static String formatPhrase(String template, List<String> commands) {
        // The cache stores one '<' separator per dynamic command, without a closing '>'.
        var parts = template.split("<", -1);
        if (parts.length != commands.size() + 1) {
            throw new IllegalStateException("phrase has " + (parts.length - 1) + " command slots but " + commands.size() + " dynamic commands");
        }

        var phrase = new StringBuilder(parts[0]);
        for (var i = 0; i < commands.size(); i++) {
            phrase.append('<').append(commands.get(i)).append('>').append(parts[i + 1]);
        }
        return phrase.toString();
    }
}
