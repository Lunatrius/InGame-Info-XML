package com.github.lunatrius.ingameinfo;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

import static cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import static cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class Ticker {
	public static boolean enabled = true;
	public static boolean showInChat = true;

	private final Minecraft client;
	private final InGameInfoCore core;

	public Ticker(InGameInfoCore core) {
		this.client = Minecraft.getMinecraft();
		this.core = core;
	}

	@SubscribeEvent
	public void tick(ClientTickEvent event) {
		onTick(event);
	}

	@SubscribeEvent
	public void tick(RenderTickEvent event) {
		onTick(event);
	}

	private void onTick(TickEvent event) {
		if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.END) {
			if (enabled && this.client != null && this.client.gameSettings != null && !this.client.gameSettings.showDebugInfo) {
				if (this.client.currentScreen == null || showInChat && this.client.currentScreen instanceof GuiChat) {
					if (event.type == TickEvent.Type.CLIENT) {
						this.core.onTickClient();
					} else if (event.type == TickEvent.Type.RENDER) {
						this.core.onTickRender();
					}
				}
			} else {
				this.core.reset();
			}
		}
	}
}
