package me.kalmemarq.hudoverlays.overlay;

import me.kalmemarq.hudoverlays.Anchor;
import me.kalmemarq.hudoverlays.UIExpression;
import me.kalmemarq.hudoverlays.condition.IOverlayCondition;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TextOverlay extends Overlay {
    public final Text text;
    public final int color;
    public final boolean shadow;

    public TextOverlay(Text text, int color, boolean shadow, UIExpression x, UIExpression y, UIExpression w, UIExpression h, @Nullable UIExpression maxW, @Nullable UIExpression maxH, @Nullable UIExpression minW, @Nullable UIExpression minH, Anchor anchorFrom, Anchor anchorTo, int layer, float alpha, List<IOverlayCondition> conditions) {
        super(Type.TEXT, x, y, w, h, maxW, maxH, minW, minH, anchorFrom, anchorTo, layer, alpha, conditions);
        this.text = text;
        this.color = color;
        this.shadow = shadow;
    }

    @Override
    public int getDefaultWidth(TextRenderer textRenderer, int screenWidth, int screenHeight) {
        return textRenderer.getWidth(this.text);
    }

    @Override
    public int getDefaultHeight(TextRenderer textRenderer, int screenWidth, int screenHeight) {
        return textRenderer.fontHeight;
    }
}
