package com.github.lunatrius.ingameinfo.proxy;

import com.github.lunatrius.ingameinfo.handler.PlayerHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ServerProxy extends CommonProxy {
    @Override
    public void init(final FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PlayerHandler());
    }
}
