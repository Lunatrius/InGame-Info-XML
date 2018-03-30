package com.github.lunatrius.ingameinfo.client.gui.overlay;

import com.github.lunatrius.ingameinfo.handler.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;

public class InfoItem extends Info {
    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    private final ItemStack itemStack;
    private final boolean large;
    private final int size;

    public InfoItem(final ItemStack itemStack) {
        this(itemStack, false);
    }

    public InfoItem(final ItemStack itemStack, final boolean large) {
        this(itemStack, large, 0, 0);
    }

    public InfoItem(final ItemStack itemStack, final boolean large, final int x, final int y) {
        super(x, y);
        this.itemStack = itemStack;
        this.large = large;
        this.size = large ? 16 : 8;
        if (large) {
            this.y = -4;
        }
    }

    @Override
    public void drawInfo() {
        if (!this.itemStack.isEmpty()) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();

            GlStateManager.translate(getX(), getY(), 0);
            if (!this.large) {
                GlStateManager.scale(0.5f, 0.5f, 0.5f);
            }

            final RenderItem renderItem = MINECRAFT.getRenderItem();
            final float zLevel = renderItem.zLevel;
            renderItem.zLevel = 300;
            renderItem.renderItemAndEffectIntoGUI(this.itemStack, 0, 0);

            if (ConfigurationHandler.showOverlayItemIcons) {
                renderItem.renderItemOverlayIntoGUI(MINECRAFT.fontRenderer, this.itemStack, 0, 0, "");
            }

            renderItem.zLevel = zLevel;

            if (!this.large) {
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
            }
            GlStateManager.translate(-getX(), -getY(), 0);

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    @Override
    public int getWidth() {
        return !this.itemStack.isEmpty() ? this.size : 0;
    }

    @Override
    public int getHeight() {
        return !this.itemStack.isEmpty() ? this.size : 0;
    }

    @Override
    public String toString() {
        return String.format("InfoItem{itemStack: %s, x: %d, y: %d, offsetX: %d, offsetY: %d, children: %s}", this.itemStack, this.x, this.y, this.offsetX, this.offsetY, this.children);
    }
}
