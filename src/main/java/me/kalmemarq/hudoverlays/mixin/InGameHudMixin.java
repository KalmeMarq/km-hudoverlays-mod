package me.kalmemarq.hudoverlays.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.kalmemarq.hudoverlays.*;
import me.kalmemarq.hudoverlays.nineslice.NinesliceDrawer;
import me.kalmemarq.hudoverlays.overlay.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow @Final
    private MinecraftClient client;
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Inject(method = "renderVignetteOverlay", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V"), cancellable = true)
    private void renderVignetteOverlay(Entity entity, CallbackInfo ci) {
        if (OverlayManager.hasCustomVignette()) {
            OverlayContext context = new OverlayContext(this.client.player);

            if (OverlayManager.VIGNETTE_OC.canDisplay(context)) {
                MatrixStack stack = new MatrixStack();
                for (Overlay ov : OverlayManager.VIGNETTE_OC.getOverlays()) {
                    if (ov.canDisplay(context)) renderHudOverlay(ov, stack);
                }

                ci.cancel();
            }
        }
    }

     @Inject(method = "renderPortalOverlay", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableDepthTest()V"), cancellable = true)
     private void renderPortalOverlay(float nauseaStrength, CallbackInfo ci) {
         if (OverlayManager.hasCustomVignette()) {
             OverlayContext context = new OverlayContext(this.client.player);

             if (OverlayManager.VIGNETTE_OC.canDisplay(context)) {
                 MatrixStack stack = new MatrixStack();
                 for (Overlay ov : OverlayManager.VIGNETTE_OC.getOverlays()) {
                     if (ov.canDisplay(context)) renderHudOverlay(ov, stack);
                 }

                 ci.cancel();
             }
         }
     }

    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = true)
    private void renderSpyglassOverlay(float scale, CallbackInfo ci) {
        if (OverlayManager.hasCustomSpyglass()) {
            OverlayContext context = new OverlayContext(this.client.player);

            if (OverlayManager.SPYGLASS_OC.canDisplay(context)) {
                MatrixStack stack = new MatrixStack();
                for (Overlay ov : OverlayManager.SPYGLASS_OC.getOverlays()) {
                    if (ov.canDisplay(context)) renderHudOverlay(ov, stack);
                }

                ci.cancel();
            }
        }
    }

    private void renderHudOverlay(Overlay overlay, MatrixStack matrices) {
        if (overlay.type == Overlay.Type.IMAGE) {
            ImageOverlay ov = (ImageOverlay) overlay;
            TextRenderer textRenderer = getTextRenderer();
            double[] dim = ov.calcDimensions(textRenderer, scaledWidth, scaledHeight, this.client.options.getGuiScale().getValue());

            RenderSystem.enableDepthTest();
            // RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, ov.texture);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

            int layer = ov.getLayer();

            if (ov.ninesliceInfo == null) {
                double x1 = dim[0] + dim[2];
                double y1 = dim[1] + dim[3];

                builder.vertex(dim[0], y1, layer).texture(ov.u0, ov.v1).next();
                builder.vertex(x1, y1, layer).texture(ov.u1, ov.v1).next();
                builder.vertex(x1, dim[1], layer).texture(ov.u1, ov.v0).next();
                builder.vertex(dim[0], dim[1], layer).texture(ov.u0, ov.v0).next();
            } else {
                NinesliceDrawer.renderTexture(builder, (int)dim[0], (int)dim[1], layer, ov.u, ov.v, ov.rW, ov.rH, (int)dim[2], (int)dim[3], ov.ninesliceInfo, 1.0f);
            }

            tessellator.draw();

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else if (overlay.type == Overlay.Type.TEXT) {
            TextOverlay ov = (TextOverlay) overlay;

            RenderSystem.enableDepthTest();
            // RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            TextRenderer textRenderer = getTextRenderer();
            double[] dim = ov.calcDimensions(textRenderer, scaledWidth, scaledHeight, this.client.options.getGuiScale().getValue());

            if (ov.shadow) {
                textRenderer.drawWithShadow(matrices, ov.text, (int)dim[0], (int)dim[1], ov.color);
            } else {
                textRenderer.draw(matrices, ov.text, (int)dim[0], (int)dim[1], ov.color);
            }

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else if (overlay.type == Overlay.Type.FILL) {
            RenderSystem.enableDepthTest();
            // RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            FillRendererOverlay ov = (FillRendererOverlay) overlay;
            TextRenderer textRenderer = getTextRenderer();
            double[] dim = ov.calcDimensions(textRenderer, scaledWidth, scaledHeight, this.client.options.getGuiScale().getValue());

            ov.render(matrices, dim);

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            RenderSystem.enableDepthTest();
            // RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            GradientRendererOverlay ov = (GradientRendererOverlay) overlay;
            TextRenderer textRenderer = getTextRenderer();
            double[] dim = ov.calcDimensions(textRenderer, scaledWidth, scaledHeight, this.client.options.getGuiScale().getValue());

            ov.render(matrices, dim);

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }


    @Inject(at = @At("TAIL"), method = "render")
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        PlayerEntity player = this.client.player;

        if (player != null && !this.client.options.hudHidden) {
            OverlayContext context = new OverlayContext(player);

            for (OverlayContainer cov : OverlayManager.getCustomOverlayContainers()) {
                if (cov.canDisplay(context)) {
                    for (Overlay ov : cov.getOverlays()) {
                        if (ov.canDisplay(context)) {
                            renderHudOverlay(ov, matrices);
                        }
                    }
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "renderOverlay", cancellable = true)
    private void injectRenderOverlay(Identifier texture, float opacity, CallbackInfo ci) {
        if (texture.equals(OverlayManager.PUMPKIN_BLUR)) {
            if (OverlayManager.PUMPKIN_OC.getOverlays().size() > 0) {

                OverlayContext context = new OverlayContext(this.client.player);

                MatrixStack stack = new MatrixStack();
                for (Overlay ov : OverlayManager.PUMPKIN_OC.getOverlays()) {
                    if (ov.canDisplay(context)) renderHudOverlay(ov, stack);
                }

                ci.cancel();
            }
        } else if (texture.equals(OverlayManager.POWDER_SNOW_OUTLINE)) {
            if (OverlayManager.POWDER_SNOW_OC.getOverlays().size() > 0) {
                OverlayContext context = new OverlayContext(this.client.player);

                MatrixStack stack = new MatrixStack();
                for (Overlay ov : OverlayManager.POWDER_SNOW_OC.getOverlays()) {
                    if (ov.canDisplay(context)) renderHudOverlay(ov, stack);
                }

                ci.cancel();
            }
        }
    }

//    private void renderOverlayReplacer(Identifier texture, int layer, float opacity, HudOverlay ov) {
//        RenderSystem.enableDepthTest();
//        // RenderSystem.depthMask(false);
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.setShader(ov.getAlpha() < 1.0f ? GameRenderer::getPositionColorTexProgram : GameRenderer::getPositionTexProgram);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
//        RenderSystem.setShaderTexture(0, texture);
//        Tessellator tessellator = Tessellator.getInstance();
//        BufferBuilder bufferBuilder = tessellator.getBuffer();
//        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, ov.getAlpha() < 1.0f ? VertexFormats.POSITION_COLOR_TEXTURE : VertexFormats.POSITION_TEXTURE);
//
//        if (ov.ninesliceInfo == null) {
//            if (ov.getAlpha() < 1.0f) {
//                bufferBuilder.vertex(0.0f, (double)this.scaledHeight, (float)layer).color(1.0f, 1.0f, 1.0f, ov.getAlpha()).texture(0.0F, 1.0F).next();
//                bufferBuilder.vertex( (double)this.scaledWidth, (double)this.scaledHeight, (float)layer).color(1.0f, 1.0f, 1.0f, ov.getAlpha()).texture(1.0F, 1.0F).next();
//                bufferBuilder.vertex( (double)this.scaledWidth, 0.0f, (float)layer).color(1.0f, 1.0f, 1.0f, ov.getAlpha()).texture(1.0F, 0.0F).next();
//                bufferBuilder.vertex( 0.0f, 0.0f, (double)layer).color(1.0f, 1.0f, 1.0f, ov.getAlpha()).texture(0.0F, 0.0F).next();
//            } else {
//                bufferBuilder.vertex(0.0f, (double)this.scaledHeight, (float)layer).texture(0.0F, 1.0F).next();
//                bufferBuilder.vertex( (double)this.scaledWidth, (double)this.scaledHeight, (float)layer).texture(1.0F, 1.0F).next();
//                bufferBuilder.vertex( (double)this.scaledWidth, 0.0f, (float)layer).texture(1.0F, 0.0F).next();
//                bufferBuilder.vertex( 0.0f, 0.0f, (double)layer).texture(0.0F, 0.0F).next();
//            }
//        } else {
//            NinesliceDrawer.renderTexture(bufferBuilder, 0, 0, layer, scaledWidth, scaledHeight, ov.ninesliceInfo, ov.getAlpha());
//        }
//
//        tessellator.draw();
//        RenderSystem.depthMask(true);
//        RenderSystem.enableDepthTest();
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//    }
}
