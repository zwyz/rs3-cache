package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DBTableUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.DBTABLE, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> {
                var columnCount = packet.g1();

                for (var value = packet.g1(); value != 255; value = packet.g1()) {
                    var column = value & 127;
                    var hasdefault = (value & 128) != 0;
                    var length = packet.g1();
                    var types = new ArrayList<Type>(length);

                    for (var i = 0; i < length; ++i) {
                        types.add(Type.byID(packet.gSmart1or2()));
                    }

                    Unpacker.setDBColumnType(id, column, types);
                    lines.add("column=col" + column + "," + types.stream().map(t -> t.name).collect(Collectors.joining(",")));

                    if (hasdefault) {
                        var defaultCount = packet.gSmart1or2();

                        for (var entry = 0; entry < defaultCount; entry++) {
                            var sb = new StringBuilder("default=col" + column);

                            for (var type : types) {
                                sb.append(",").append(switch (type.baseType) {
                                    case INTEGER -> Unpacker.format(type, packet.g4s());
                                    case LONG -> Unpacker.format(type, packet.g8s());
                                    case STRING -> Unpacker.format(type, packet.gjstr());
                                    default -> throw new IllegalStateException();
                                });
                            }

                            lines.add(sb.toString());
                        }
                    }
                }
            }

            case 2 -> {
                var size = packet.g4s();
                var start = packet.pos;
                var columnCount = packet.g1();
                var columnType = new Type[columnCount][];

                for (var col = packet.g1(); col != 255; col = packet.g1()) {
                    for (var op = packet.g1(); op != 0; op = packet.g1()) {
                        if (op == 1) {
                            var tupleLength = packet.g1();
                            columnType[col] = new Type[tupleLength];
                            var sb = new StringBuilder("column=col" + col);

                            for (int tup = 0; tup < tupleLength; tup++) {
                                columnType[col][tup] = Type.byID(packet.gSmart1or2());
                                sb.append(",").append(columnType[col][tup].name);
                            }

                            Unpacker.setDBColumnType(id, col, List.of(columnType[col]));
                            lines.add(sb.toString());
                        } else if (op == 2) {
                            var defaultCount = packet.gSmart1or2();

                            for (int def = 0; def < defaultCount; def++) {
                                var tupleLength = columnType[col].length;
                                var sb = new StringBuilder("default=col" + col);

                                for (int tup = 0; tup < tupleLength; tup++) {
                                    var type = columnType[col][def];

                                    sb.append(",").append(switch (type.baseType) {
                                        case INTEGER -> Unpacker.format(type, packet.g4s());
                                        case LONG -> Unpacker.format(type, packet.g8s());
                                        case STRING -> Unpacker.format(type, packet.gjstr());
                                        default -> throw new IllegalStateException();
                                    });
                                }

                                lines.add(sb.toString());
                            }
                        } else {
                            throw new IllegalStateException("invalid column op " + op);
                        }
                    }
                }

                if (packet.pos - start != size) {
                    throw new AssertionError("invalid size");
                }
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
