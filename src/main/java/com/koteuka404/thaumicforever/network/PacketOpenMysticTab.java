package com.koteuka404.thaumicforever.network;

import com.koteuka404.thaumicforever.registry.ModGuiHandler;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import com.koteuka404.thaumicforever.compat.BaublesCompat;
import com.koteuka404.thaumicforever.config.ModConfig;
import com.koteuka404.thaumicforever.research.MysticBaubleSlots;
import com.koteuka404.thaumicforever.ThaumicForever;

public class PacketOpenMysticTab implements IMessage {
    public PacketOpenMysticTab() {}
    @Override public void toBytes(ByteBuf buf) {}
    @Override public void fromBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<PacketOpenMysticTab,IMessage> {
        @Override
        public IMessage onMessage(PacketOpenMysticTab msg, MessageContext ctx) {
            if (!ModConfig.enableMysticGuiButton) return null;
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                if (!ModConfig.enableMysticGuiButton) return;
                player.openGui(
                    ThaumicForever.instance,
                    ModGuiHandler.GUI_BAUBLES,
                    player.getServerWorld(), 0,0,0
                );

                IBaublesItemHandler ba = BaublesApi.getBaublesHandler(player);
                for (MysticBaubleSlots.BoundSlot bs : MysticBaubleSlots.getGuiBoundSlots(player, ba)) {
                    int phys = bs.physicalIndex;
                    BaublesCompat.sendSlotSync(player, phys, ba.getStackInSlot(phys), player);
                }
            });
            return null;
        }
    }
}
