package me.kalmemarq.hudoverlays.condition;

import me.kalmemarq.hudoverlays.HudOverlayContext;

public interface IHudOverlayCondition {
    boolean test(HudOverlayContext context);
}
