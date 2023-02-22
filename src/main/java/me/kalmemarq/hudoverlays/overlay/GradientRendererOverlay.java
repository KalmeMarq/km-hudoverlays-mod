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

public class GradientRendererOverlay extends Overlay {
    public final Orientation orientation;
    public final int colorStart;
    public final int colorEnd;

    public GradientRendererOverlay(int colorStart, int colorEnd, Orientation orientation, UIExpression x, UIExpression y, UIExpression w, UIExpression h, @Nullable UIExpression maxW, @Nullable UIExpression maxH, @Nullable UIExpression minW, @Nullable UIExpression minH, Anchor anchorFrom, Anchor anchorTo, int layer, float alpha, List<IOverlayCondition> conditions) {
        super(Type.GRADIENT, x, y, w, h, maxW, maxH, minW, minH, anchorFrom, anchorTo, layer, alpha, conditions);
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;
        this.orientation = orientation;
    }

    public void render(MatrixStack matrices, double[] dimensions) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float sa = (float)(colorStart >> 24 & 255) / 255.0F;
        float sr = (float)(colorStart >> 16 & 255) / 255.0F;
        float sg = (float)(colorStart >> 8 & 255) / 255.0F;
        float sb = (float)(colorStart & 255) / 255.0F;

        float ea = (float)(colorEnd >> 24 & 255) / 255.0F;
        float er = (float)(colorEnd >> 16 & 255) / 255.0F;
        float eg = (float)(colorEnd >> 8 & 255) / 255.0F;
        float eb = (float)(colorEnd & 255) / 255.0F;

        double startX = dimensions[0];
        double startY = dimensions[1];
        double endX = startX + dimensions[2];
        double endY = startY + dimensions[3];

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        if (orientation == Orientation.VERTICAL) {
            builder.vertex(matrix, (float)endX, (float)startY, (float)layer).color(sr, sg, sb, sa).next();
            builder.vertex(matrix, (float)startX, (float)startY, (float)layer).color(sr, sg, sb, sa).next();
            builder.vertex(matrix, (float)startX, (float)endY, (float)layer).color(er, eg, eb, ea).next();
            builder.vertex(matrix, (float)endX, (float)endY, (float)layer).color(er, eg, eb, ea).next();
        } else {
            builder.vertex(matrix, (float)endX, (float)startY, (float)layer).color(er, eg, eb, ea).next();
            builder.vertex(matrix, (float)startX, (float)startY, (float)layer).color(sr, sg, sb, sa).next();
            builder.vertex(matrix, (float)startX, (float)endY, (float)layer).color(sr, sg, sb, sa).next();
            builder.vertex(matrix, (float)endX, (float)endY, (float)layer).color(er, eg, eb, ea).next();
        }

        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public enum Orientation {
        VERTICAL,
        HORIZONTAL;

        public static Orientation deserialize(String str) {
            if (str.equals("horizontal")) {
                return Orientation.HORIZONTAL;
            } else {
                return Orientation.VERTICAL;
            }
        }
    }
}
