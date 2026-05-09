package com.koteuka404.thaumicforever.network;

import com.example.coremod.ThaumcraftLogisticsRequestLifetimePatch;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCancelLogisticsRequest implements IMessage {
    private ItemStack stack = ItemStack.EMPTY;

    public PacketCancelLogisticsRequest() {}

    public PacketCancelLogisticsRequest(ItemStack stack) {
        this.stack = stack == null ? ItemStack.EMPTY : stack.copy();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.stack.isEmpty() ? new NBTTagCompound() : this.stack.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        this.stack = tag == null || tag.hasNoTags() ? ItemStack.EMPTY : new ItemStack(tag);
    }

    public static class Handler implements IMessageHandler<PacketCancelLogisticsRequest, IMessage> {
        @Override
        public IMessage onMessage(PacketCancelLogisticsRequest msg, MessageContext ctx) {
            if (msg == null || ctx == null || ctx.getServerHandler() == null) {
                return null;
            }

            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    ThaumcraftLogisticsRequestLifetimePatch.cancelForPlayer(player, msg.stack);
                }
            });
            return null;
        }
    }
}
