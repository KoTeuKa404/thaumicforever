package com.koteuka404.thaumicforever.network;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import com.koteuka404.thaumicforever.compat.BaublesCompat;

public class PacketOpenNormalInventory implements IMessage {
    public PacketOpenNormalInventory() {}
    @Override public void toBytes(ByteBuf buf) {}
    @Override public void fromBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<PacketOpenNormalInventory, IMessage> {
        @Override
        public IMessage onMessage(PacketOpenNormalInventory msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                player.closeContainer();
                IBaublesItemHandler ba = BaublesApi.getBaublesHandler(player);
                int originalSlots = ba.getSlots();

                for (int i = 0; i < originalSlots; i++) {
                    BaublesCompat.sendSlotSync(player, i, ba.getStackInSlot(i), player);
                }
            });
            return null;
        }
    }
}
