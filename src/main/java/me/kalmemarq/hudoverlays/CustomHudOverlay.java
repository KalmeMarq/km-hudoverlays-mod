package me.kalmemarq.hudoverlays;

import me.kalmemarq.hudoverlays.condition.IHudOverlayCondition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CustomHudOverlay {
    private final List<HudOverlay> overlays;
    private final List<IHudOverlayCondition> conditions;

    public CustomHudOverlay() {
        this.overlays = new ArrayList<>();
        this.conditions = new ArrayList<>();
    }

    public void addOverlay(HudOverlay hudOverlay) {
        this.overlays.add(hudOverlay);
        this.overlays.sort(Comparator.comparingInt(HudOverlay::getLayer));
    }

    public void addCondition(IHudOverlayCondition condition) {
        this.conditions.add(condition);
    }

    public List<HudOverlay> getOverlays() {
        return this.overlays;
    }

    public boolean canDisplay(HudOverlayContext context) {
        for (IHudOverlayCondition cond : conditions) {
            if (!cond.test(context)) {
                return false;
            }
        }

        return true;
    }
}
