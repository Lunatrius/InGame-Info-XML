package com.github.lunatrius.ingameinfo.proxy;

import com.github.lunatrius.ingameinfo.handler.PlayerHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ServerProxy extends CommonProxy {
    @Override
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new PlayerHandler());
    }
}
