package me.kalmemarq.hudoverlays.condition;

import com.google.gson.JsonObject;
import me.kalmemarq.hudoverlays.OverlayContext;
import net.minecraft.util.JsonHelper;

public class IsUsingSpyglass implements IOverlayCondition {
    private final boolean value;

    public IsUsingSpyglass(boolean value) {
        this.value = value;
    }

    @Override
    public boolean test(OverlayContext context) {
        return context.getPlayer().isUsingSpyglass() == value;
    }

    public static final class Serializer implements IOverlayConditionSerializer<IsUsingSpyglass> {
        @Override
        public IsUsingSpyglass fromJson(JsonObject obj) {
            boolean value = JsonHelper.getBoolean(obj, "value", true);
            return new IsUsingSpyglass(value);
        }
    }
}
