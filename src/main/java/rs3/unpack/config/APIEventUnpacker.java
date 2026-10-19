package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class APIEventUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        var types = new ArrayList<Type>();
        lines.add("[" + Unpacker.format(Type.API_EVENT, id) + "]");

        while (true) {
            var opcode = packet.g1();
            switch (opcode) {
                case 0 -> {
                    if (packet.pos != packet.arr.length) {
                        throw new IllegalStateException("end of file not reached");
                    }

                    Unpacker.setAPIEventType(id, types);
                    return lines;
                }

                case 1 -> lines.add("name=" + packet.gjstr());
                case 2 -> lines.add("permission=" + packet.gjstr());
                case 3 -> {
                    var name = packet.gjstr();
                    var type = Type.byID(packet.gSmart1or2());
                    types.add(type);
                    lines.add("property=" + name + "," + type);
                }

                default -> throw new IllegalStateException("unknown opcode: " + opcode);
            }
        }
    }
}
