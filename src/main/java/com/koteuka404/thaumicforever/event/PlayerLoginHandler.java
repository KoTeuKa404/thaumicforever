package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.network.PacketServerConfigSync;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class PlayerLoginHandler {
    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            ThaumicForever.network.sendTo(PacketServerConfigSync.fromServerConfig(), player);
            ThaumcraftCapabilities.getKnowledge(event.player).sync(player);
        }
    }
}
