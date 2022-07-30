package me.kalmemarq.hudoverlays.condition;

import me.kalmemarq.hudoverlays.HudOverlayContext;

public class UnderwaterCondition implements IHudOverlayCondition {
    private final boolean value;

    public UnderwaterCondition(boolean value) {
        this.value = value;
    }

    @Override
    public boolean test(HudOverlayContext context) {
        return context.getPlayer().isSubmergedInWater() == value;
    }
}
