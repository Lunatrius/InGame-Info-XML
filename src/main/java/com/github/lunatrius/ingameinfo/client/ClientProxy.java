package com.github.lunatrius.ingameinfo.client;

import com.github.lunatrius.ingameinfo.CommonProxy;
import com.github.lunatrius.ingameinfo.InGameInfoCore;
import com.github.lunatrius.ingameinfo.Ticker;
import com.github.lunatrius.ingameinfo.command.InGameInfoCommand;
import com.github.lunatrius.ingameinfo.lib.Reference;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.ClientCommandHandler;

import java.io.File;

public class ClientProxy extends CommonProxy {
	private final InGameInfoCore core = InGameInfoCore.instance;

	@Override
	public void initializeVariables() {
		Ticker.showInChat = Reference.config.getShowInChat();
		Ticker.showOnPlayerList = Reference.config.getShowOnPlayerList();
	}

	@Override
	public void setupConfig(File file) {
		this.core.setConfigDirectory(file);
		this.core.copyDefaultConfig();
		this.core.setConfigFile(Reference.config.getConfigName());
		this.core.reloadConfig();
	}

	@Override
	public void registerEvents() {
		FMLCommonHandler.instance().bus().register(new Ticker(this.core));
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
