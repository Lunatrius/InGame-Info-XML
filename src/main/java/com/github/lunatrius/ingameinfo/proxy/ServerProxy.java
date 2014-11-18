package com.github.lunatrius.ingameinfo.proxy;

import com.github.lunatrius.ingameinfo.handler.PlayerHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class ServerProxy extends CommonProxy {
    @Override
    public void registerEvents() {
        FMLCommonHandler.instance().bus().register(new PlayerHandler());
    }
}
