package rs3.unpack.vfx;

import com.google.gson.*;
import rs3.util.Packet;

import java.util.Locale;

public class VFXUnpacker {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(VFXAttribute.class, (JsonSerializer<VFXAttribute>) (src, typeOfSrc, context) -> {
                var result = new JsonObject(); // new object to make type first field
                result.addProperty("type", src.getType().name().toLowerCase(Locale.ROOT));
                var data = (JsonObject) context.serialize(src);

                for (var e : data.entrySet()) {
                    result.add(e.getKey(), e.getValue());
                }

                return result;
            })
            .registerTypeAdapter(VFXAttribute.class, (JsonDeserializer<VFXAttribute>) (json, typeOfT, context) -> {
                var type = VFXAttributeType.valueOf(((JsonObject) json).get("type").getAsString().toUpperCase(Locale.ROOT));

                return switch (type) {
                    case ATTRIBUTE_0 -> context.deserialize(json, VFXAttribute0.class);
                    case ATTRIBUTE_1 -> context.deserialize(json, VFXAttribute1.class);
                    case ATTRIBUTE_2 -> context.deserialize(json, VFXAttribute2.class);
                    case ATTRIBUTE_3 -> context.deserialize(json, VFXAttribute3.class);
                    case ATTRIBUTE_4 -> context.deserialize(json, VFXAttribute4.class);
                    case ATTRIBUTE_5 -> context.deserialize(json, VFXAttribute5.class);
                    case ATTRIBUTE_6 -> context.deserialize(json, VFXAttribute6.class);
                    case ATTRIBUTE_7 -> context.deserialize(json, VFXAttribute7.class);
                    case ATTRIBUTE_8 -> context.deserialize(json, VFXAttribute8.class);
                    case ATTRIBUTE_9 -> context.deserialize(json, VFXAttribute9.class);
                    case ATTRIBUTE_10 -> context.deserialize(json, VFXAttribute10.class);
                };
            })
            .setPrettyPrinting()
            .create();

    public static String unpack(byte[] b) {
        return GSON.toJson(new VFX(new Packet(b)));
    }
}
