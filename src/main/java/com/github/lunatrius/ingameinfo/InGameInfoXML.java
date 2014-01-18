package com.github.lunatrius.ingameinfo;

import com.github.lunatrius.ingameinfo.command.InGameInfoCommand;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.ClientCommandHandler;

import java.util.EnumSet;
import java.util.logging.Logger;

@Mod(modid = "InGameInfoXML")
public class InGameInfoXML {
	@Instance("InGameInfoXML")
	public static InGameInfoXML instance;
	public static final Logger LOGGER = FMLCommonHandler.instance().getFMLLogger();

	private final InGameInfoCore core = InGameInfoCore.instance;
	private Minecraft minecraftClient = null;
	private boolean showInChat;

	public Config config = null;
	public boolean enabled = true;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.config = new Config(event.getSuggestedConfigurationFile());
		this.config.load();
		this.showInChat = this.config.getShowInChat();
		this.config.save();

		this.core.setClient(this.minecraftClient = Minecraft.getMinecraft());
		this.core.setConfigDirectory(event.getModConfigurationDirectory());
		this.core.copyDefaultConfig();
		this.core.setConfigFile(this.config.getConfigName());
		this.core.reloadConfig();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		TickRegistry.registerTickHandler(new Ticker(EnumSet.of(TickType.CLIENT, TickType.RENDER)), Side.CLIENT);

		ClientCommandHandler.instance.registerCommand(new InGameInfoCommand(this.core));
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		this.core.setServer(event.getServer());
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		this.core.setServer(null);
	}

	public boolean onTick(TickType tick, boolean start) {
		if (start) {
			return true;
		}

		if (this.enabled && this.minecraftClient != null && this.minecraftClient.gameSettings != null && !this.minecraftClient.gameSettings.showDebugInfo) {
			if (this.minecraftClient.currentScreen == null || this.showInChat && this.minecraftClient.currentScreen instanceof GuiChat) {
				if (tick == TickType.CLIENT) {
					this.core.onTickClient();
				} else if (tick == TickType.RENDER) {
					this.core.onTickRender();
				}
			}
		}

		return true;
	}
}
