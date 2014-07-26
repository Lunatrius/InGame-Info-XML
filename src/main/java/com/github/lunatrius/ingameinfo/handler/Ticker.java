package com.github.lunatrius.ingameinfo.handler;

import com.github.lunatrius.ingameinfo.InGameInfoCore;
import com.github.lunatrius.ingameinfo.tag.Tag;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import static cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import static cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class Ticker {
	public static boolean enabled = true;

	private final Minecraft client = Minecraft.getMinecraft();
	private final InGameInfoCore core = InGameInfoCore.INSTANCE;

	@SubscribeEvent
	public void onRenderGameOverlayEventPre(RenderGameOverlayEvent.Pre event) {
		if (ConfigurationHandler.replaceDebug && event.type == RenderGameOverlayEvent.ElementType.DEBUG) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		onTick(event);
	}

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		onTick(event);
	}

	private boolean isRunning() {
		if (enabled) {
			if (this.client.mcProfiler.profilingEnabled) {
				return true;
			}

			// a && b || !a && !b  -->  a == b
			if (this.client.gameSettings != null && ConfigurationHandler.replaceDebug == this.client.gameSettings.showDebugInfo) {
				if (!ConfigurationHandler.showOnPlayerList && this.client.gameSettings.keyBindPlayerList.getIsKeyPressed()) {
					return false;
				}

				if (this.client.gameSettings.hideGUI) {
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
				Tag.setServer(null);
				Tag.releaseResources();
			}
			this.client.mcProfiler.endSection();
		}
	}
}
