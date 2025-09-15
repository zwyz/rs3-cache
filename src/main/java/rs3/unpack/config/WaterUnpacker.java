package rs3.unpack.config;

import rs3.unpack.Type;
import rs3.unpack.Unpacker;
import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class WaterUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var lines = new ArrayList<String>();
        var packet = new Packet(data);
        lines.add("[" + Unpacker.format(Type.WATER, id) + "]");

        while (true) switch (packet.g1()) {
            case 0 -> {
                if (packet.pos != packet.arr.length) {
                    throw new IllegalStateException("end of file not reached");
                }

                return lines;
            }

            // jag::WaterType::DecodeUnusedParams
            case 1 -> lines.add("unknown1=" + packet.g2());
            case 3 -> lines.add("unknown3=" + packet.g2());
            case 6 -> lines.add("unknown6=0x" + Integer.toHexString(packet.g3()));
            case 7 -> lines.add("unknown7=" + packet.g2() + "," + packet.g2());
            case 8 -> lines.add("unknown8=" + packet.g2());
            case 11 -> lines.add("unknown11=" + packet.g2());
            case 13 -> lines.add("unknown13=" + packet.g2());
            case 15 -> lines.add("unknown15=" + packet.g4s());
            case 16 -> lines.add("unknown16=" + packet.g2());
            case 17 -> lines.add("unknown17=" + packet.g2());
            case 18 -> lines.add("unknown18=" + packet.g1());
            case 19 -> lines.add("unknown19=" + packet.g1());
            case 20 -> lines.add("unknown20=" + packet.g2());
            case 21 -> lines.add("unknown21=" + packet.g2());
            case 22 -> lines.add("unknown22=" + packet.g2());
            case 23 -> lines.add("unknown23=" + packet.g2());
            case 24 -> lines.add("unknown24=" + packet.g1());
            case 26 -> lines.add("unknown26=" + packet.g2() + "," + packet.g2() + "," + packet.g2());
            case 28 -> lines.add("unknown28=" + packet.g2());

            // jag::WaterType::DecodeLegacyParam
            case 12 -> lines.add("basergba=0x%08x".formatted(packet.g4s()));

            // jag::WaterType::DecodeFoamParams
            case 9 -> lines.add("water_foam_scale=" + packet.g2());
            case 14 -> lines.add("water_depth_foam=" + packet.g2());

            // jag::WaterType::DecodeLightingReflectionParams
            case 5 -> lines.add("reflection_strength=" + packet.g2());
            case 25 -> lines.add("specular_shininess=" + packet.g2());
            case 27 -> lines.add("specular_factor=" + packet.g2());
            case 83 -> lines.add("fresnel_bias=" + packet.gFloat());
            case 84 -> lines.add("unknown84=" + packet.gFloat());//unused

            // jag::WaterType::DecodeMaterialScaleParams
            case 2 -> lines.add("normal_map_material1_scale=" + packet.g2());
            case 4 -> lines.add("normal_map_material2_scale=" + packet.g2());
            case 10 -> lines.add("foam_material_scale=" + packet.g2());
            case 32 -> lines.add("normal_map_material3_scale=" + packet.g2());
            case 87 -> lines.add("emissive_map_material_scale=" + packet.g2());

            // jag::WaterType::DecodeNormalParams
            case 29 -> lines.add("normal_map_material1=" + Unpacker.format(Type.MATERIAL, packet.g2()));
            case 30 -> lines.add("normal_map_material2=" + Unpacker.format(Type.MATERIAL, packet.g2()));
            case 31 -> lines.add("normal_map_material3=" + Unpacker.format(Type.MATERIAL, packet.g2()));

            case 33 -> decodeNormalMapParams(lines, 0, 0, packet);
            case 34 -> decodeNormalMapParams(lines, 0, 1, packet);
            case 35 -> decodeNormalMapParams(lines, 0, 2, packet);
            case 36 -> decodeNormalMapParams(lines, 0, 3, packet);
            case 37 -> decodeNormalMapParams(lines, 0, 4, packet);
            case 38 -> decodeNormalMapParams(lines, 0, 5, packet);
            case 39 -> decodeNormalMapParams(lines, 0, 6, packet);
            case 40 -> decodeNormalMapParams(lines, 0, 7, packet);

            case 41 -> decodeNormalMapParams(lines, 1, 0, packet);
            case 42 -> decodeNormalMapParams(lines, 1, 1, packet);
            case 43 -> decodeNormalMapParams(lines, 1, 2, packet);
            case 44 -> decodeNormalMapParams(lines, 1, 3, packet);
            case 45 -> decodeNormalMapParams(lines, 1, 4, packet);
            case 46 -> decodeNormalMapParams(lines, 1, 5, packet);
            case 47 -> decodeNormalMapParams(lines, 1, 6, packet);
            case 48 -> decodeNormalMapParams(lines, 1, 7, packet);

            case 49 -> decodeNormalMapParams(lines, 2, 0, packet);
            case 50 -> decodeNormalMapParams(lines, 2, 1, packet);
            case 51 -> decodeNormalMapParams(lines, 2, 2, packet);
            case 52 -> decodeNormalMapParams(lines, 2, 3, packet);
            case 53 -> decodeNormalMapParams(lines, 2, 4, packet);
            case 54 -> decodeNormalMapParams(lines, 2, 5, packet);
            case 55 -> decodeNormalMapParams(lines, 2, 6, packet);
            case 56 -> decodeNormalMapParams(lines, 2, 7, packet);

            case 57 -> decodeNormalMapParams(lines, 3, 0, packet);
            case 58 -> decodeNormalMapParams(lines, 3, 1, packet);
            case 59 -> decodeNormalMapParams(lines, 3, 2, packet);
            case 60 -> decodeNormalMapParams(lines, 3, 3, packet);
            case 61 -> decodeNormalMapParams(lines, 3, 4, packet);
            case 62 -> decodeNormalMapParams(lines, 3, 5, packet);
            case 63 -> decodeNormalMapParams(lines, 3, 6, packet);
            case 64 -> decodeNormalMapParams(lines, 3, 7, packet);

            case 65 -> decodeNormalMapParams(lines, 4, 0, packet);
            case 66 -> decodeNormalMapParams(lines, 4, 1, packet);
            case 67 -> decodeNormalMapParams(lines, 4, 2, packet);
            case 68 -> decodeNormalMapParams(lines, 4, 3, packet);
            case 69 -> decodeNormalMapParams(lines, 4, 4, packet);
            case 70 -> decodeNormalMapParams(lines, 4, 5, packet);
            case 71 -> decodeNormalMapParams(lines, 4, 6, packet);
            case 72 -> decodeNormalMapParams(lines, 4, 7, packet);

            case 73 -> decodeNormalMapParams(lines, 5, 0, packet);
            case 74 -> decodeNormalMapParams(lines, 5, 1, packet);
            case 75 -> decodeNormalMapParams(lines, 5, 2, packet);
            case 76 -> decodeNormalMapParams(lines, 5, 3, packet);
            case 77 -> decodeNormalMapParams(lines, 5, 4, packet);
            case 78 -> decodeNormalMapParams(lines, 5, 5, packet);
            case 79 -> decodeNormalMapParams(lines, 5, 6, packet);
            case 80 -> decodeNormalMapParams(lines, 5, 7, packet);

            // jag::WaterType::DecodeEmissiveParams
            case 86 -> lines.add("emisive_map_material=" + packet.g2());
            case 88 -> lines.add("emissive_uv_scale=" + packet.gFloat() + "," + packet.gFloat());
            case 89 -> lines.add("emissive_rgb=" + packet.g4s());
            case 90 -> lines.add("emissive_scale=" + packet.gFloat());
            case 91 -> lines.add("emissive_map_refraction_depth=" + packet.gFloat());
            case 92 -> lines.add("emissive_map_mode=" + packet.g1());
            case 93 -> lines.add("emissive_source=" + packet.gFloat());
            case 94 -> lines.add("emissive_flow_speed=" + packet.gFloat());
            case 95 -> lines.add("emissive_flow_rotation_degrees=" + packet.gFloat());
            case 96 -> lines.add("emissive_uv_mode=" + packet.g1());
            case 108 -> lines.add("emissive_blend=" + packet.gFloat());

            // jag::WaterType::DecodeExtinctionParams
            case 97 -> lines.add("extinction_rgb_depth_metres=" + packet.gFloat() + "," + packet.gFloat() + "," + packet.gFloat());
            case 98 -> lines.add("extinction_opaque_water_colour=" + packet.g4s());
            case 99 -> lines.add("extinction_visibility_metres=" + packet.gFloat());

            // jag::WaterType::DecodeCausticsParams
            case 100 -> lines.add("caustics_scale=" + packet.gFloat());
            case 101 -> lines.add("caustics_refraction_scale=" + packet.gFloat());
            case 102 -> lines.add("caustics_depth_fade_cutoff=" + packet.gFloat());
            case 103 -> lines.add("caustics_depth_fade_scale=" + packet.gFloat());
            case 104 -> lines.add("caustics_edge_fade_start=" + packet.gFloat());
            case 105 -> lines.add("caustics_edge_fade_end=" + packet.gFloat());
            case 106 -> lines.add("caustics_over_water_fade_start=" + packet.gFloat());
            case 107 -> lines.add("caustics_over_water_fade_end=" + packet.gFloat());

            // jag::WaterType::Decode
            case 81 -> lines.add("still_water_normal_strength=" + packet.gFloat());
            case 82 -> lines.add("flow_noise=" + packet.gFloat());
            case 85 -> lines.add("override_default_water_type=" + Unpacker.formatBoolean(packet.g1()));

            default -> throw new IllegalStateException("unknown opcode");
        }
    }

    private static void decodeNormalMapParams(List<String> lines, int target, int op, Packet packet) {
        switch (op) {
            case 0 -> lines.add("normal_map_params" + (target + 1) + "_unknown33=" + Unpacker.formatBoolean(packet.g1()));
            case 1 -> lines.add("normal_map_params" + (target + 1) + "_unknown34 =" + packet.gFloat());
            case 2 -> lines.add("normal_map_params" + (target + 1) + "_unknown35=" + packet.gFloat());
            case 3 -> lines.add("normal_map_params" + (target + 1) + "_unknown36=" + packet.gFloat());
            case 4 -> lines.add("normal_map_params" + (target + 1) + "_unknown37=" + packet.gFloat() + "," + packet.gFloat());
            case 5 -> lines.add("normal_map_params" + (target + 1) + "_unknown38=" + packet.gFloat() + "," + packet.gFloat());
            case 6 -> lines.add("normal_map_params" + (target + 1) + "_unknown39=" + packet.gFloat());
            case 7 -> lines.add("normal_map_params" + (target + 1) + "_unknown40=" + packet.gFloat());
            default -> throw new IllegalStateException("unknown normal map target");
        }
    }
}
