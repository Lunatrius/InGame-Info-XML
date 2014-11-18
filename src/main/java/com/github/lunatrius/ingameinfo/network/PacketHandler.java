package com.github.lunatrius.ingameinfo.network;

import com.github.lunatrius.ingameinfo.network.message.MessageSeed;
import com.github.lunatrius.ingameinfo.reference.Reference;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID.toLowerCase());

    public static void init() {
        INSTANCE.registerMessage(MessageSeed.class, MessageSeed.class, 0, Side.CLIENT);
    }
}
