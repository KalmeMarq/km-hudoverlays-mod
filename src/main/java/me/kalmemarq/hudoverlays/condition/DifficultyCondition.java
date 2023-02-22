package me.kalmemarq.hudoverlays.condition;

import me.kalmemarq.hudoverlays.OverlayContext;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

public class DifficultyCondition implements IOverlayCondition {
    private final Difficulty diff;

    public DifficultyCondition(@Nullable Difficulty difficulty) {
        this.diff = difficulty == null ? Difficulty.PEACEFUL : difficulty;
    }

    @Override
    public boolean test(OverlayContext context) {
        return context.getPlayer().getWorld().getDifficulty() == this.diff;
    }

    public static final class Serializer implements IOverlayConditionSerializer<DifficultyCondition> {
        @Override
        public DifficultyCondition fromJson(JsonObject obj) {
            String df = JsonHelper.getString(obj, "name");
            return new DifficultyCondition(Difficulty.byName(df));
        }
    } 
}
