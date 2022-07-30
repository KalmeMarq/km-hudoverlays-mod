package me.kalmemarq.hudoverlays.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.kalmemarq.hudoverlays.*;
import me.kalmemarq.hudoverlays.nineslice.NinesliceDrawer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Shadow private int lastHealthValue;

    @Inject(at = @At("TAIL"), method = "render")
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
       PlayerEntity player = MinecraftClient.getInstance().player;

       if (player != null && !MinecraftClient.getInstance().options.hudHidden) {
           HudOverlayContext context = new HudOverlayContext(player);

           for (CustomHudOverlay cov : HudOverlayManager.getCustomHudOverlays()) {
               if (cov.canDisplay(context)) {
                   for (HudOverlay ov : cov.getOverlays()) {
                       if (ov.canDisplay(context)) {
                           renderOverlayReplacer(ov.getTexture(), ov.getLayer(), 1.0f, ov);
                       }
                   }
               }
           }
       }
    }

    @Inject(at = @At("HEAD"), method = "renderOverlay", cancellable = true)
    private void injectRenderOverlay(Identifier texture, float opacity, CallbackInfo ci) {
       if (texture.equals(HudOverlayManager.PUMPKIN_BLUR)) {
           if (HudOverlayManager.getPumpkinOverlays().size() > 0) {

               HudOverlayContext context = new HudOverlayContext(MinecraftClient.getInstance().player);

               for (HudOverlay ov : HudOverlayManager.getPumpkinOverlays()) {
                   if (ov.canDisplay(context)) renderOverlayReplacer(ov.getTexture(), ov.getLayer(), opacity, ov);
               }

               ci.cancel();
           }
       } else if (texture.equals(HudOverlayManager.POWDER_SNOW_OUTLINE)) {
           if (HudOverlayManager.getPowderSnowOverlays().size() > 0) {
               HudOverlayContext context = new HudOverlayContext(MinecraftClient.getInstance().player);

               for (HudOverlay ov : HudOverlayManager.getPowderSnowOverlays()) {
                   if (ov.canDisplay(context)) renderOverlayReplacer(ov.getTexture(), ov.getLayer(), opacity, ov);
               }

               ci.cancel();
           }
       }
    }

    private void renderOverlayReplacer(Identifier texture, int layer, float opacity, HudOverlay ov) {
        RenderSystem.enableDepthTest();
        // RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ov.getAlpha() < 1.0f ? GameRenderer::getPositionColorTexShader : GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
        RenderSystem.setShaderTexture(0, texture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, ov.getAlpha() < 1.0f ? VertexFormats.POSITION_COLOR_TEXTURE : VertexFormats.POSITION_TEXTURE);

        if (ov.ninesliceInfo == null) {
            if (ov.getAlpha() < 1.0f) {
                bufferBuilder.vertex(0.0f, (double)this.scaledHeight, (float)layer).color(1.0f, 1.0f, 1.0f, ov.getAlpha()).texture(0.0F, 1.0F).next();
                bufferBuilder.vertex( (double)this.scaledWidth, (double)this.scaledHeight, (float)layer).color(1.0f, 1.0f, 1.0f, ov.getAlpha()).texture(1.0F, 1.0F).next();
                bufferBuilder.vertex( (double)this.scaledWidth, 0.0f, (float)layer).color(1.0f, 1.0f, 1.0f, ov.getAlpha()).texture(1.0F, 0.0F).next();
                bufferBuilder.vertex( 0.0f, 0.0f, (double)layer).color(1.0f, 1.0f, 1.0f, ov.getAlpha()).texture(0.0F, 0.0F).next();
            } else {
                bufferBuilder.vertex(0.0f, (double)this.scaledHeight, (float)layer).texture(0.0F, 1.0F).next();
                bufferBuilder.vertex( (double)this.scaledWidth, (double)this.scaledHeight, (float)layer).texture(1.0F, 1.0F).next();
                bufferBuilder.vertex( (double)this.scaledWidth, 0.0f, (float)layer).texture(1.0F, 0.0F).next();
                bufferBuilder.vertex( 0.0f, 0.0f, (double)layer).texture(0.0F, 0.0F).next();
            }
        } else {
            NinesliceDrawer.renderTexture(bufferBuilder, 0, 0, layer, scaledWidth, scaledHeight, ov.ninesliceInfo);
        }

        tessellator.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}