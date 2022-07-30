package me.kalmemarq.hudoverlays;

import me.kalmemarq.hudoverlays.condition.IHudOverlayCondition;
import me.kalmemarq.hudoverlays.nineslice.NinesliceInfo;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HudOverlay {
    private final Identifier texture;
    private final boolean fitToScreen;
    private final int layer;
    private final float alpha;
    private final List<IHudOverlayCondition> conditions;
    @Nullable
    public NinesliceInfo ninesliceInfo;

    public HudOverlay(Identifier texture, boolean fitToScreen, int layer, float alpha, @Nullable NinesliceInfo ninesliceInfo) {
        this.texture = texture;
        this.fitToScreen = fitToScreen;
        this.layer = layer;
        this.alpha = alpha;
        this.conditions = new ArrayList<>();
        this.ninesliceInfo = ninesliceInfo;
    }

    public void addCondition(IHudOverlayCondition condition) {
        this.conditions.add(condition);
    }

    public Identifier getTexture() {
        return this.texture;
    }

    public boolean fitsToScreen() {
        return this.fitToScreen;
    }

    public int getLayer() {
        return this.layer;
    }

    public float getAlpha() {
        return this.alpha;
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
