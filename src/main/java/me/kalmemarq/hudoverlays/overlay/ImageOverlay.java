package me.kalmemarq.hudoverlays.overlay;

import me.kalmemarq.hudoverlays.Anchor;
import me.kalmemarq.hudoverlays.UIExpression;
import me.kalmemarq.hudoverlays.condition.IOverlayCondition;
import me.kalmemarq.hudoverlays.nineslice.NinesliceInfo;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ImageOverlay extends Overlay {
    public final Identifier texture;
    @Nullable
    public final NinesliceInfo ninesliceInfo;
    public final float u0;
    public final float v0;
    public final float u1;
    public final float v1;
    public final int u;
    public final int v;
    public final int rW;
    public final int rH;

    public ImageOverlay(Identifier texture, int u, int v, int regionW, int regionH, int textureWidth, int textureHeight, @Nullable NinesliceInfo ninesliceInfo, UIExpression x, UIExpression y, UIExpression w, UIExpression h, @Nullable UIExpression maxW, @Nullable UIExpression maxH, @Nullable UIExpression minW, @Nullable UIExpression minH, Anchor anchorFrom, Anchor anchorTo, int layer, float alpha, List<IOverlayCondition> conditions) {
        super(Type.IMAGE, x, y, w, h, maxW, maxH, minW, minH, anchorFrom, anchorTo, layer, alpha, conditions);
        this.texture = texture;
        this.ninesliceInfo = ninesliceInfo;

        this.u = u;
        this.v = v;
        this.rW = regionW;
        this.rH = regionH;

        this.u0 = u / (float)textureWidth;
        this.v0 = v / (float)textureHeight;
        this.u1 = (u + regionW) / (float)textureWidth;
        this.v1 = (v + regionH) / (float)textureHeight;
    }
}
