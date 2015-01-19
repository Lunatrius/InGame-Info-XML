package com.github.lunatrius.ingameinfo.client.gui;

import com.github.lunatrius.core.util.vector.Vector2f;
import com.github.lunatrius.ingameinfo.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class InfoIcon extends Info {
    private final static TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    private final ResourceLocation resourceLocation;
    private final Vector2f xy0 = new Vector2f();
    private final Vector2f xy1 = new Vector2f();
    private final Vector2f uv0 = new Vector2f();
    private final Vector2f uv1 = new Vector2f();
    private final double zLevel = 300;
    private int displayWidth;
    private int displayHeight;

    public InfoIcon(String location) {
        this(location, 0, 0, 8, 8, 0, 0, 8, 8, 8, 8, 0, 0);
    }

    public InfoIcon(String location, int displayX, int displayY, int displayWidth, int displayHeight, int iconX, int iconY, int iconWidth, int iconHeight, int textureWidth, int textureHeight, int x, int y) {
        super(x, y);
        this.resourceLocation = new ResourceLocation(location);
        setDisplayDimensions(displayX, displayY, displayWidth, displayHeight);
        setTextureData(iconX, iconY, iconWidth, iconHeight, textureWidth, textureHeight);
    }

    public void setDisplayDimensions(int displayX, int displayY, int displayWidth, int displayHeight) {
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;

        this.xy0.set(displayX, displayY);
        this.xy1.set(displayX + displayWidth, displayY + displayHeight);
    }

    public void setTextureData(int iconX, int iconY, int iconWidth, int iconHeight, int textureWidth, int textureHeight) {
        this.uv0.set((float) iconX / textureWidth, (float) iconY / textureHeight);
        this.uv1.set((float) (iconX + iconWidth) / textureWidth, (float) (iconY + iconHeight) / textureHeight);
    }

    @Override
    public void drawInfo() {
        try {
            textureManager.bindTexture(this.resourceLocation);

            GL11.glTranslatef(getX(), getY(), 0);

            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.startDrawingQuads();
            worldRenderer.addVertexWithUV(this.xy0.x, this.xy1.y, this.zLevel, this.uv0.x, this.uv1.y);
            worldRenderer.addVertexWithUV(this.xy1.x, this.xy1.y, this.zLevel, this.uv1.x, this.uv1.y);
            worldRenderer.addVertexWithUV(this.xy1.x, this.xy0.y, this.zLevel, this.uv1.x, this.uv0.y);
            worldRenderer.addVertexWithUV(this.xy0.x, this.xy0.y, this.zLevel, this.uv0.x, this.uv0.y);
            tessellator.draw();

            GL11.glTranslatef(-getX(), -getY(), 0);
        } catch (Exception e) {
            Reference.logger.debug(e);
        }
    }

    @Override
    public int getWidth() {
        return this.displayWidth;
    }

    @Override
    public int getHeight() {
        return this.displayHeight;
    }

    @Override
    public String toString() {
        return String.format("InfoIcon{resource: %s, x: %d, y: %d, offsetX: %d, offsetY: %d, children: %s}", this.resourceLocation, this.x, this.y, this.offsetX, this.offsetY, this.children);
    }
}
