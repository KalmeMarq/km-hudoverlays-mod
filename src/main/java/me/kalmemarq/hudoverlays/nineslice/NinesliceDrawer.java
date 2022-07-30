package me.kalmemarq.hudoverlays.nineslice;

import net.minecraft.client.render.BufferBuilder;

public class NinesliceDrawer {
    public static void renderTexture(BufferBuilder bufferBuilder, int x, int y, int z, int width, int height, NinesliceInfo nsInfo) {
        int u = 0;
        int v = 0;
        int regionWidth = nsInfo.baseWidth;
        int regionHeight = nsInfo.baseHeight;

        // Top Left
        renderTextureRegion(bufferBuilder, x, y, z, nsInfo.u0, nsInfo.v0, u, v, nsInfo.u0, nsInfo.v0, nsInfo);
        // Top Middle
        renderTextureRegion(bufferBuilder, x + nsInfo.u0, y, z, width - nsInfo.u0 - nsInfo.u1, nsInfo.v0, u + nsInfo.u0, v, regionWidth - nsInfo.u0 - nsInfo.u1, nsInfo.v0, nsInfo);
        // Top Right
        renderTextureRegion(bufferBuilder, x + width - nsInfo.u1, y, z, nsInfo.u1, nsInfo.v0, u + regionWidth - nsInfo.u1, v, nsInfo.u1, nsInfo.v0, nsInfo);
        // Left Middle
        renderTextureRegion(bufferBuilder, x, y + nsInfo.v0, z, nsInfo.u0, height - nsInfo.v0 - nsInfo.v1, u, v + nsInfo.v0, nsInfo.u0, regionHeight - nsInfo.v0 - nsInfo.v1, nsInfo);
        // Center
        renderTextureRegion(bufferBuilder, x + nsInfo.u0, y + nsInfo.v0, z, width - nsInfo.u0 - nsInfo.u1, height - nsInfo.v0 - nsInfo.v1, u + nsInfo.u0, v + nsInfo.v0, regionWidth - nsInfo.u0 - nsInfo.u1, regionHeight - nsInfo.v0 - nsInfo.v1, nsInfo);
        // Right Middle
        renderTextureRegion(bufferBuilder, x + width - nsInfo.u1, y + nsInfo.v0, z, nsInfo.u0,height - nsInfo.v0 - nsInfo.v1, u + regionWidth - nsInfo.u1, v + nsInfo.v0, nsInfo.u0, regionHeight - nsInfo.v0 - nsInfo.v1, nsInfo);
        // Bottom Left
        renderTextureRegion(bufferBuilder, x, y + height - nsInfo.v1, z, nsInfo.u0, nsInfo.v1, u, v + regionHeight - nsInfo.v1, nsInfo.u0, nsInfo.v1, nsInfo);
        // Bottom Middle
        renderTextureRegion(bufferBuilder, x + nsInfo.u0, y + height - nsInfo.v1, z,width - nsInfo.u0 - nsInfo.u1, nsInfo.v1, u + nsInfo.u0, v + regionHeight - nsInfo.v1, regionWidth - nsInfo.u0 - nsInfo.u1, nsInfo.v1, nsInfo);
        // Bottom Right
        renderTextureRegion(bufferBuilder, x + width - nsInfo.u1, y + height - nsInfo.v1, z, nsInfo.u0, nsInfo.v1, u + regionWidth - nsInfo.u1, v + regionHeight - nsInfo.v1, nsInfo.u0, nsInfo.v1, nsInfo);
    }

    public static void renderTextureRegion(BufferBuilder bufferBuilder, int x, int y, int z, int width, int height, int u, int v, int regionWidth, int regionHeight, NinesliceInfo nsInfo) {
        double x0 = x;
        double x1 = x + width;
        double y0 = y;
        double y1 = y + height;

        float u0 = (u + 0.0f) / nsInfo.baseWidth;
        float v0 = (v + 0.0f) / nsInfo.baseHeight;
        float u1 = (u + (float)regionWidth) / nsInfo.baseWidth;
        float v1 = (v + (float)regionHeight) / nsInfo.baseHeight;

        bufferBuilder.vertex(x0, y1, z).texture(u0, v1).next();
        bufferBuilder.vertex(x1, y1, z).texture(u1, v1).next();
        bufferBuilder.vertex(x1, y0, z).texture(u1, v0).next();
        bufferBuilder.vertex(x0, y0, z).texture(u0, v0).next();
    }
}
