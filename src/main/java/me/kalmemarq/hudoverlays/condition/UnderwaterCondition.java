package me.kalmemarq.hudoverlays.condition;

import com.google.gson.JsonObject;

import me.kalmemarq.hudoverlays.HudOverlayContext;
import net.minecraft.util.JsonHelper;

public class UnderwaterCondition implements IHudOverlayCondition {
    private final boolean value;

    public UnderwaterCondition(boolean value) {
        this.value = value;
    }

    @Override
    public boolean test(HudOverlayContext context) {
        return context.getPlayer().isSubmergedInWater() == value;
    }

    public static final class Serializer implements IHudOverlayConditionSerializer {
        @Override
        public IHudOverlayCondition fromJson(JsonObject obj) {
            boolean value = JsonHelper.getBoolean(obj, "value", true);
            return new UnderwaterCondition(value);
        }
    }
}
