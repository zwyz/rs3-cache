package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.unpack.VarDomain;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class VarClanSettingUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                lines.addFirst("[" + Unpacker.format(Type.VAR_CLAN_SETTING, id) + "]");
                return lines;
            }

            case 1 -> {
                var type = packet.g1();

                if (type == 1) {
                    lines.add("type=bit");
                } else {
                    Unpacker.setVarType(VarDomain.CLAN_SETTING, id, Type.byChar(type));
                    lines.add("type=" + Unpacker.format(Type.TYPE, type));
                }
            }

            case 2 -> {
                lines.add("basevar=" + Unpacker.format(Type.VAR_CLAN_SETTING, packet.g2()));
                lines.add("startbit=" + packet.g1());
                lines.add("endbit=" + packet.g1());
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
