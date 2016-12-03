package com.github.lunatrius.ingameinfo.handler;

import com.github.lunatrius.ingameinfo.network.PacketHandler;
import com.github.lunatrius.ingameinfo.network.message.MessageSeed;
import com.github.lunatrius.ingameinfo.reference.Reference;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerHandler {
    @SubscribeEvent
    public void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            try {
                PacketHandler.INSTANCE.sendTo(new MessageSeed(event.player.world.getSeed()), (EntityPlayerMP) event.player);
            } catch (final Exception ex) {
                Reference.logger.error("Failed to send the seed!", ex);
            }
        }
    }
}
