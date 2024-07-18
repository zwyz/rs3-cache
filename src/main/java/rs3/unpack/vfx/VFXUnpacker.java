package rs3.unpack.vfx;

import com.google.gson.*;
import rs3.util.Packet;

import java.util.Locale;

public class VFXUnpacker {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Module.class, (JsonSerializer<Module>) (src, typeOfSrc, context) -> {
                var result = new JsonObject(); // new object to make type first field
                result.addProperty("type", src.getType().name().toLowerCase(Locale.ROOT));
                var data = (JsonObject) context.serialize(src);

                for (var e : data.entrySet()) {
                    result.add(e.getKey(), e.getValue());
                }

                return result;
            })
            .registerTypeAdapter(Module.class, (JsonDeserializer<Module>) (json, typeOfT, context) -> {
                var type = ModuleType.valueOf(((JsonObject) json).get("type").getAsString().toUpperCase(Locale.ROOT));

                return switch (type) {
                    case FLOW -> context.deserialize(json, Flow.class);
                    case COLOUR -> context.deserialize(json, Colour.class);
                    case GRAVITY -> context.deserialize(json, Gravity.class);
                    case TRIGGER_PLANE -> context.deserialize(json, TriggerPlane.class);
                    case ATTRACTOR -> context.deserialize(json, Attractor.class);
                    case ROTATION -> context.deserialize(json, Rotation.class);
                    case SCALE -> context.deserialize(json, Scale.class);
                    case NOISE -> context.deserialize(json, Noise.class);
                    case FLIPBOOK -> context.deserialize(json, Flipbook.class);
                    case ACCELERATION -> context.deserialize(json, Acceleration.class);
                    case LIGHTING -> context.deserialize(json, Lighting.class);
                };
            })
            .setPrettyPrinting()
            .create();

    public static String unpack(byte[] b) {
        return GSON.toJson(new VFX(new Packet(b)));
    }
}
