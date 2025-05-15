package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class PlayerLoginHandler {
    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            ThaumcraftCapabilities.getKnowledge(event.player).sync((EntityPlayerMP) event.player);
        }
    }
}
