package com.github.lunatrius.ingameinfo.handler;

import com.github.lunatrius.ingameinfo.network.PacketHandler;
import com.github.lunatrius.ingameinfo.network.message.MessageSeed;
import com.github.lunatrius.ingameinfo.reference.Reference;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerHandler {
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            try {
                PacketHandler.INSTANCE.sendTo(new MessageSeed(event.player.worldObj.getSeed()), (EntityPlayerMP) event.player);
            } catch (Exception ex) {
                Reference.logger.error("Failed to send the seed!", ex);
            }
        }
    }
}
