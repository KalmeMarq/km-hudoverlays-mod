package me.kalmemarq.hudoverlays.condition;

import me.kalmemarq.hudoverlays.HudOverlayContext;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

public class DifficultyCondition implements IHudOverlayCondition {
    @Nullable
    private final Difficulty diff;

    public DifficultyCondition(@Nullable Difficulty difficulty) {
        this.diff = difficulty;
    }

    @Override
    public boolean test(HudOverlayContext context) {
        return context.getPlayer().getWorld().getDifficulty() == this.diff;
    }

    public static final class Serializer implements IHudOverlayConditionSerializer {
        @Override
        public IHudOverlayCondition fromJson(JsonObject obj) {
            String df = JsonHelper.getString(obj, "name");
            return new DifficultyCondition(Difficulty.byName(df));
        }
    } 
}
