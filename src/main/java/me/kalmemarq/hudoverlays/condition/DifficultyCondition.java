package me.kalmemarq.hudoverlays.condition;

import me.kalmemarq.hudoverlays.HudOverlayContext;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.Nullable;

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
}
