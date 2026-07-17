package com.koteuka404.thaumicforever.network;

import java.util.ArrayList;
import java.util.List;

import com.koteuka404.thaumicforever.client.ClientFutureDropCache;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketFutureDrops implements IMessage {

    private int entityId;
    private List<ItemStack> drops = new ArrayList<>();

    public PacketFutureDrops() {}

    public PacketFutureDrops(int entityId, List<ItemStack> drops) {
        this.entityId = entityId;

        if (drops != null) {
            this.drops = drops;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();

        int size = buf.readInt();
        this.drops = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            ItemStack stack = ByteBufUtils.readItemStack(buf);

            if (!stack.isEmpty()) {
                this.drops.add(stack);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);

        int size = Math.min(this.drops.size(), 8);
        buf.writeInt(size);

        for (int i = 0; i < size; i++) {
            ByteBufUtils.writeItemStack(buf, this.drops.get(i));
        }
    }

    public static class Handler implements IMessageHandler<PacketFutureDrops, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketFutureDrops message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                ClientFutureDropCache.put(message.entityId, message.drops);
            });

            return null;
        }
    }
}