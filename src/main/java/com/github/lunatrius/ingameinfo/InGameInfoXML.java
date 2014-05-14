package com.github.lunatrius.ingameinfo;

import com.github.lunatrius.core.version.VersionChecker;
import com.github.lunatrius.ingameinfo.config.Config;
import com.github.lunatrius.ingameinfo.lib.Reference;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = Reference.MODID, name = Reference.NAME)
public class InGameInfoXML {
	@Instance(Reference.MODID)
	public static InGameInfoXML instance;

	@SidedProxy(serverSide = Reference.PROXY_COMMON, clientSide = Reference.PROXY_CLIENT)
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		VersionChecker.registerMod(event.getModMetadata());

		Reference.logger = event.getModLog();

		Reference.config = new Config(event.getSuggestedConfigurationFile());
		Reference.config.save();

		proxy.initializeVariables();

		proxy.setupConfig(event.getModConfigurationDirectory());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerEvents();
		proxy.registerCommands();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		proxy.setServer(event.getServer());
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		proxy.setServer(null);
	}
}
