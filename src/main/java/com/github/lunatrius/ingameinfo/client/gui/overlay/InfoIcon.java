package com.github.lunatrius.ingameinfo.client.gui.overlay;

import com.github.lunatrius.core.client.gui.GuiHelper;
import com.github.lunatrius.core.util.vector.Vector2f;
import com.github.lunatrius.ingameinfo.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class InfoIcon extends Info {
    private final static TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    private final ResourceLocation resourceLocation;
    private final Vector2f xy0 = new Vector2f();
    private final Vector2f xy1 = new Vector2f();
    private final Vector2f uv0 = new Vector2f();
    private final Vector2f uv1 = new Vector2f();
    private int displayWidth;
    private int displayHeight;

    public InfoIcon(final String location) {
        this(new ResourceLocation(location));
    }

    public InfoIcon(final String location, final int displayX, final int displayY, final int displayWidth, final int displayHeight, final int iconX, final int iconY, final int iconWidth, final int iconHeight, final int textureWidth, final int textureHeight, final int x, final int y) {
        this(new ResourceLocation(location), displayX, displayY, displayWidth, displayHeight, iconX, iconY, iconWidth, iconHeight, textureWidth, textureHeight, x, y);
    }

    public InfoIcon(final ResourceLocation location) {
        this(location, 0, 0, 8, 8, 0, 0, 8, 8, 8, 8, 0, 0);
    }

    public InfoIcon(final ResourceLocation location, final int displayX, final int displayY, final int displayWidth, final int displayHeight, final int iconX, final int iconY, final int iconWidth, final int iconHeight, final int textureWidth, final int textureHeight, final int x, final int y) {
        super(x, y);
        this.resourceLocation = location;
        setDisplayDimensions(displayX, displayY, displayWidth, displayHeight);
        setTextureData(iconX, iconY, iconWidth, iconHeight, textureWidth, textureHeight);
    }

    public void setDisplayDimensions(final int displayX, final int displayY, final int displayWidth, final int displayHeight) {
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;

        this.xy0.set(displayX, displayY);
        this.xy1.set(displayX + displayWidth, displayY + displayHeight);
    }

    public void setTextureData(final int iconX, final int iconY, final int iconWidth, final int iconHeight, final int textureWidth, final int textureHeight) {
        this.uv0.set((float) iconX / textureWidth, (float) iconY / textureHeight);
        this.uv1.set((float) (iconX + iconWidth) / textureWidth, (float) (iconY + iconHeight) / textureHeight);
    }

    @Override
    public void drawInfo() {
        try {
            textureManager.bindTexture(this.resourceLocation);

            GlStateManager.translate(getX(), getY(), 0);

            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder buffer = tessellator.getBuffer();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            double zLevel = 300;
            GuiHelper.drawTexturedRectangle(buffer, this.xy0.x, this.xy0.y, this.xy1.x, this.xy1.y, zLevel, this.uv0.x, this.uv0.y, this.uv1.x, this.uv1.y);
            tessellator.draw();

            GlStateManager.translate(-getX(), -getY(), 0);
        } catch (final Exception e) {
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
