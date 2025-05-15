package com.koteuka404.thaumicforever;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.BaubleType;               
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketSync;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.*;

import java.util.List;

public class PacketOpenMysticTab implements IMessage {
    public PacketOpenMysticTab() {}
    @Override public void toBytes(ByteBuf buf) {}
    @Override public void fromBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<PacketOpenMysticTab, IMessage> {
        @Override
        public IMessage onMessage(PacketOpenMysticTab msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                player.openGui(
                    ThaumicForever.instance,
                    ModGuiHandler.GUI_BAUBLES,
                    player.getServerWorld(), 0, 0, 0
                );

                IBaublesItemHandler ba = BaublesApi.getBaublesHandler(player);
                int offset = ba.getSlots();
                List<BaubleType> types = MysticBaubleSlots.getForPlayer(player);

                for (int i = 0; i < types.size(); i++) {
                    PacketSync sync = new PacketSync(
                        player,
                        offset + i,
                        ba.getStackInSlot(offset + i)
                    );
                    PacketHandler.INSTANCE.sendTo(sync, player);
                }
            });
            return null;
        }
    }
}
