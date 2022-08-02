package me.kalmemarq.hudoverlays.condition;

import com.google.gson.JsonObject;
import me.kalmemarq.hudoverlays.HudOverlayContext;
import net.minecraft.util.JsonHelper;

public class IsUsingSpyglass implements IHudOverlayCondition {
    private final boolean value;

    public IsUsingSpyglass(boolean value) {
        this.value = value;
    }

    @Override
    public boolean test(HudOverlayContext context) {
        return context.getPlayer().isUsingSpyglass() == value;
    }

    public static final class Serializer implements IHudOverlayConditionSerializer<IsUsingSpyglass> {
        @Override
        public IsUsingSpyglass fromJson(JsonObject obj) {
            boolean value = JsonHelper.getBoolean(obj, "value", true);
            return new IsUsingSpyglass(value);
        }
    }
}
