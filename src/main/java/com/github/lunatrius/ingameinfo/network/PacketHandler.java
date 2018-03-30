package com.github.lunatrius.ingameinfo.network;

import com.github.lunatrius.ingameinfo.network.message.MessageSeed;
import com.github.lunatrius.ingameinfo.reference.Reference;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);

    public static void init() {
        INSTANCE.registerMessage(MessageSeed.class, MessageSeed.class, 0, Side.CLIENT);
    }
}
