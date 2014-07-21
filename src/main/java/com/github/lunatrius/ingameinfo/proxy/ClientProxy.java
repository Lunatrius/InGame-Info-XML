package com.github.lunatrius.ingameinfo.proxy;

import com.github.lunatrius.ingameinfo.InGameInfoCore;
import com.github.lunatrius.ingameinfo.command.InGameInfoCommand;
import com.github.lunatrius.ingameinfo.handler.ConfigurationHandler;
import com.github.lunatrius.ingameinfo.handler.Ticker;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

public class ClientProxy extends CommonProxy {
	private final InGameInfoCore core = InGameInfoCore.instance;

	@Override
	public void setupConfig(File file) {
		this.core.setConfigDirectory(file);
		this.core.copyDefaultConfig();
		this.core.setConfigFile(ConfigurationHandler.configName);
		this.core.reloadConfig();
	}

	@Override
	public void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new Ticker(this.core));
		FMLCommonHandler.instance().bus().register(new Ticker(this.core));
		FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
	}

	@Override
	public void registerCommands() {
		ClientCommandHandler.instance.registerCommand(new InGameInfoCommand(this.core));
	}

	@Override
	public void setServer(MinecraftServer server) {
		this.core.setServer(server);
	}
}
