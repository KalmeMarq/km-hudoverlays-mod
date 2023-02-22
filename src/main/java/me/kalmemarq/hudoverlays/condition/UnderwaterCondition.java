package me.kalmemarq.hudoverlays.condition;

import com.google.gson.JsonObject;

import me.kalmemarq.hudoverlays.OverlayContext;
import net.minecraft.util.JsonHelper;

public class UnderwaterCondition implements IOverlayCondition {
    private final boolean value;

    public UnderwaterCondition(boolean value) {
        this.value = value;
    }

    @Override
    public boolean test(OverlayContext context) {
        return context.getPlayer().isSubmergedInWater() == value;
    }

    public static final class Serializer implements IOverlayConditionSerializer<UnderwaterCondition> {
        @Override
        public UnderwaterCondition fromJson(JsonObject obj) {
            boolean value = JsonHelper.getBoolean(obj, "value", true);
            return new UnderwaterCondition(value);
        }
    }
}
