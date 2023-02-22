package me.kalmemarq.hudoverlays.condition;

import me.kalmemarq.hudoverlays.OverlayContext;

public interface IOverlayCondition {
    boolean test(OverlayContext context);
}
