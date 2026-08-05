package rs3.unpack.config;

import rs3.Unpack;
import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class MapElementUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.MAPELEMENT, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            case 1 -> lines.add("sprite=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null())); // 216 GetSprite
            case 2 -> lines.add("mouseovergraphic=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null()));
            case 3 -> lines.add("text=" + packet.gjstr()); // 216 GetText, html5 unobfuscated
            case 4 -> lines.add("textcolour=" + Unpacker.formatColour(packet.g3())); // 216 GetTextRGBA
            case 5 -> lines.add("textmouseovercolour=" + Unpacker.formatColour(packet.g3())); // 216 GetTextMouseOverColour
            case 6 -> lines.add("textsize=" + packet.g1()); // 216 GetTextSize

            case 7 -> lines.add("show=" + switch (packet.g1()) {  // 216 GetShowOnWorldMap, GetShowOnMiniMap
                case 0 -> "none";
                case 1 -> "map";
                case 2 -> "minimap";
                case 3 -> "both";
                default -> throw new IllegalStateException();
            });

            case 8 -> lines.add("mapfunction=" + Unpacker.formatYesNo(packet.g1()));

            case 9 -> {
                var varbit = packet.g2null();
                var var = packet.g2null();

                if (var != -1) {
                    lines.add("condition=" + Unpacker.format(Type.VAR_PLAYER, var) + "," + packet.g4s() + "," + packet.g4s());
                } else {
                    lines.add("condition=" + Unpacker.format(Type.VAR_PLAYER_BIT, varbit) + "," + packet.g4s() + "," + packet.g4s());
                }
            }

            case 10 -> lines.add("op1=" + packet.gjstr());
            case 11 -> lines.add("op2=" + packet.gjstr());
            case 12 -> lines.add("op3=" + packet.gjstr());
            case 13 -> lines.add("op4=" + packet.gjstr());
            case 14 -> lines.add("op5=" + packet.gjstr());

            case 15 -> { // 216 GetPolygon
                var points = packet.g1();

                for (var i = 0; i < points; ++i) {
                    lines.add("polygonpoint" + i + "=" + packet.g2s() + "," + packet.g2s());
                }

                lines.add("polygonfill=" + Unpacker.formatColour(packet.g4s()));

                if (Unpack.VERSION < 629) {
                    lines.add("polygonoutline=" + Unpacker.formatColour(packet.g4s()));
                } else {
                    var palette = new int[packet.g1()];

                    for (var i = 0; i < palette.length; ++i) {
                        palette[i] = packet.g4s();
                    }

                    if (palette.length == 1) {
                        lines.add("polygonoutline=" + Unpacker.formatColour(palette[0]));

                        for (var i = 0; i < points; ++i) {
                            packet.g1s();
                        }
                    } else {
                        for (var i = 0; i < points; ++i) {
                            lines.add("polygonoutline" + i + "=" + Unpacker.formatColour(palette[packet.g1s()]));
                        }
                    }
                }
            }

            case 16 -> lines.add("listable=no"); // 216 GetListable
            case 17 -> lines.add("opbase=" + packet.gjstr());
            case 18 -> lines.add("worldmaparrow=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null())); // 216 GetWorldmapArrow
            case 19 -> lines.add("category=" + Unpacker.format(Type.CATEGORY, packet.g2())); // 216 GetCategory

            case 20 -> {
                var varbit = packet.g2null();
                var var = packet.g2null();

                if (var != -1) {
                    lines.add("condition2=" + Unpacker.format(Type.VAR_PLAYER, var) + "," + packet.g4s() + "," + packet.g4s());
                } else {
                    lines.add("condition2=" + Unpacker.format(Type.VAR_PLAYER_BIT, varbit) + "," + packet.g4s() + "," + packet.g4s());
                }
            }

            case 21 -> lines.add("textbackgroundoutline=" + Unpacker.formatColour(packet.g4s())); // 216 GetTextBackgroundOutlineRGBA
            case 22 -> lines.add("textbackgroundfill=" + Unpacker.formatColour(packet.g4s())); // 216 GetTextBackgroundFillRGBA
            case 23 -> lines.add("polygonoutlinedash=" + packet.g1() + "," + packet.g1() + "," + packet.g1()); // length, gap, phase
            case 24 -> lines.add("textoffset=" + packet.g2s() + "," + packet.g2s());
            case 25 -> lines.add("flashsprite=" + Unpacker.format(Type.GRAPHIC, packet.gSmart2or4null())); // 216 GetFlashSpriteID

            case 26 -> { // 216 GetMultiME
                var multivarbit = packet.g2null();

                if (multivarbit != -1) {
                    lines.add("multivar=" + Unpacker.format(Type.VAR_PLAYER_BIT, multivarbit));
                }

                var multivarp = packet.g2null();

                if (multivarp != -1) {
                    lines.add("multivar=" + Unpacker.format(Type.VAR_PLAYER, multivarp));
                }

                var count = packet.g1();

                for (var i = 0; i <= count; ++i) {
                    var multi = packet.g2null();

                    if (multi != -1) {
                        lines.add("multimel=" + i + "," + Unpacker.format(Type.MAPELEMENT, multi));
                    }
                }
            }

            case 27 -> {
                var multivarbit = packet.g2null();

                if (multivarbit != -1) {
                    lines.add("multivar=" + Unpacker.format(Type.VAR_PLAYER_BIT, multivarbit));
                }

                var multivarp = packet.g2null();

                if (multivarp != -1) {
                    lines.add("multivar=" + Unpacker.format(Type.VAR_PLAYER, multivarp));
                }

                var multidefault = packet.g2null();

                if (multidefault != -1) {
                    lines.add("multimel=default," + Unpacker.format(Type.HITMARK, multidefault));
                }

                var count = packet.g1();

                for (var i = 0; i <= count; ++i) {
                    var multi = packet.g2null();

                    if (multi != -1) {
                        lines.add("multimel=" + i + "," + Unpacker.format(Type.HITMARK, multi));
                    }
                }
            }

            case 28 -> lines.add("minimapiconscale=" + packet.g1()); // 216 GetMinimapIconScale

            case 29 -> lines.add("halign=" + switch (packet.g1()) { // 216 GetHAlign
                case 0 -> "left";
                case 1 -> "centre";
                case 2 -> "right";
                default -> throw new IllegalStateException();
            });

            case 30 -> lines.add("valign=" + switch (packet.g1()) { // 216 GetVAlign
                case 0 -> "top";
                case 1 -> "centre";
                case 2 -> "bottom";
                default -> throw new IllegalStateException();
            });

            case 249 -> {
                var count = packet.g1();

                for (var i = 0; i < count; i++) {
                    if (packet.g1() == 1) {
                        lines.add("param=" + Unpacker.format(Type.PARAM, packet.g3()) + "," + packet.gjstr());
                    } else {
                        var param = packet.g3();
                        lines.add("param=" + Unpacker.format(Type.PARAM, param) + "," + Unpacker.format(Unpacker.getParamType(param), packet.g4s()));
                    }
                }
            }

            default -> throw new IllegalStateException("unknown opcode");
        }
    }
}
