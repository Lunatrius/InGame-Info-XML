package com.github.lunatrius.ingameinfo.handler;

import com.github.lunatrius.ingameinfo.InGameInfoCore;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

import static cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import static cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class Ticker {
	public static boolean enabled = true;

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

	private boolean isRunning() {
		if (enabled) {
			if (this.client.mcProfiler.profilingEnabled) {
				return true;
			}

			if (this.client.gameSettings != null && !this.client.gameSettings.showDebugInfo) {
				if (!ConfigurationHandler.showOnPlayerList && this.client.gameSettings.keyBindPlayerList.getIsKeyPressed()) {
					return false;
				}

				if (this.client.currentScreen == null) {
					return true;
				}

				if (ConfigurationHandler.showInChat && this.client.currentScreen instanceof GuiChat) {
					return true;
				}
			}
		}

		return false;
	}

	private void onTick(TickEvent event) {
		if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.END) {
			this.client.mcProfiler.startSection("ingameinfo");
			if (isRunning()) {
				if (event.type == TickEvent.Type.CLIENT) {
					this.core.onTickClient();
				} else if (event.type == TickEvent.Type.RENDER) {
					this.core.onTickRender();
				}
			}

			if ((!enabled || this.client.gameSettings == null) && event.type == TickEvent.Type.CLIENT) {
				this.core.reset();
			}
			this.client.mcProfiler.endSection();
		}
	}
}
