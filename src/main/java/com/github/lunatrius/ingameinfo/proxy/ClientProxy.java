package com.github.lunatrius.ingameinfo.proxy;

import com.github.lunatrius.ingameinfo.InGameInfoCore;
import com.github.lunatrius.ingameinfo.command.InGameInfoCommand;
import com.github.lunatrius.ingameinfo.handler.ConfigurationHandler;
import com.github.lunatrius.ingameinfo.handler.Ticker;
import com.github.lunatrius.ingameinfo.tag.Tag;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import com.github.lunatrius.ingameinfo.value.registry.ValueRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

public class ClientProxy extends CommonProxy {
    private final InGameInfoCore core = InGameInfoCore.INSTANCE;

    @Override
    public void registerValues() {
        ValueRegistry.INSTANCE.init();
    }

    @Override
    public void setupConfig(File file) {
        this.core.setConfigDirectory(file);
        this.core.setConfigFile(ConfigurationHandler.configName);
        this.core.reloadConfig();
    }

    @Override
    public void registerTags() {
        TagRegistry.INSTANCE.init();
    }

    @Override
    public void registerEvents() {
        MinecraftForge.EVENT_BUS.register(new Ticker());
        FMLCommonHandler.instance().bus().register(new Ticker());
        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
    }

    @Override
    public void registerCommands() {
        ClientCommandHandler.instance.registerCommand(new InGameInfoCommand());
    }

    @Override
    public void setServer(MinecraftServer server) {
        Tag.setServer(server);
    }
}
