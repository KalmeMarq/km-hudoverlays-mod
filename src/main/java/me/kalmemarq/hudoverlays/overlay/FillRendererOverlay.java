package me.kalmemarq.hudoverlays.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import me.kalmemarq.hudoverlays.Anchor;
import me.kalmemarq.hudoverlays.UIExpression;
import me.kalmemarq.hudoverlays.condition.IOverlayCondition;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;

public class FillRendererOverlay extends Overlay {
    public final int color;

    public FillRendererOverlay(int color, UIExpression x, UIExpression y, UIExpression w, UIExpression h, @Nullable UIExpression maxW, @Nullable UIExpression maxH, @Nullable UIExpression minW, @Nullable UIExpression minH, Anchor anchorFrom, Anchor anchorTo, int layer, float alpha, List<IOverlayCondition> conditions) {
        super(Type.FILL, x, y, w, h, maxW, maxH, minW, minH, anchorFrom, anchorTo, layer, alpha, conditions);
        this.color = color;
    }

    public void render(MatrixStack matrices, double[] dimensions) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        double i;
        double x1 = dimensions[0];
        double y1 = dimensions[1];
        double x2 = x1 + dimensions[2];
        double y2 = y1 + dimensions[3];

        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float)x1, (float)y2, 0.0F).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, (float)x2, (float)y2, 0.0F).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, (float)x2, (float)y1, 0.0F).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0.0F).color(r, g, b, a).next();
        bufferBuilder.end();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
