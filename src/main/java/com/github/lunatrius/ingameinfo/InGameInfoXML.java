package com.github.lunatrius.ingameinfo;

import com.github.lunatrius.core.version.VersionChecker;
import com.github.lunatrius.ingameinfo.handler.ConfigurationHandler;
import com.github.lunatrius.ingameinfo.network.PacketHandler;
import com.github.lunatrius.ingameinfo.proxy.CommonProxy;
import com.github.lunatrius.ingameinfo.reference.Reference;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY)
public class InGameInfoXML {
    @Instance(Reference.MODID)
    public static InGameInfoXML instance;

    @SidedProxy(serverSide = Reference.PROXY_SERVER, clientSide = Reference.PROXY_CLIENT)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Reference.logger = event.getModLog();
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        proxy.registerValues();
        proxy.setupConfig(event.getModConfigurationDirectory());

        VersionChecker.registerMod(event.getModMetadata(), Reference.FORGE);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        PacketHandler.init();
        proxy.registerEvents();
        proxy.registerCommands();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.registerTags();
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
