package com.github.lunatrius.ingameinfo.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class InfoItem extends Info {
	private final static RenderItem renderItem = new RenderItem();
	private final ItemStack item;
	private final FontRenderer fontRenderer;

	public InfoItem(FontRenderer fontRenderer, ItemStack item) {
		this(fontRenderer, item, 0, 0);
	}

	public InfoItem(FontRenderer fontRenderer, ItemStack item, int x, int y) {
		super(x, y);
		this.fontRenderer = fontRenderer;
		this.item = item;
	}

	@Override
	public void drawInfo() {
		if (this.item != null) {
			RenderHelper.enableStandardItemLighting();
			RenderHelper.enableGUIStandardItemLighting();

			GL11.glTranslatef(getX(), getY(), 0);
			GL11.glScalef(0.5f, 0.5f, 0.5f);

			renderItem.renderItemAndEffectIntoGUI(this.fontRenderer, Minecraft.getMinecraft().getTextureManager(), this.item, 0, 0);

			GL11.glScalef(2.0f, 2.0f, 2.0f);
			GL11.glTranslatef(-getX(), -getY(), 0);

			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_BLEND);
		}
	}

	@Override
	public int getWidth() {
		return this.item != null ? 8 : 0;
	}

	@Override
	public int getHeight() {
		return this.item != null ? 8 : 0;
	}

	@Override
	public String toString() {
		return String.format("InfoItem{item: %s, x: %d, y: %d, offsetX: %d, offsetY: %d, children: %s}", this.item, this.x, this.y, this.offsetX, this.offsetY, this.children);
	}

	static {
		renderItem.zLevel = 300;
	}
}
