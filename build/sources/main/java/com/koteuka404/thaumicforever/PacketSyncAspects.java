package com.koteuka404.thaumicforever;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class PacketSyncAspects implements IMessage {
    private BlockPos pos;
    private AspectList aspects;

    public PacketSyncAspects() {}

    public PacketSyncAspects(BlockPos pos, AspectList aspects) {
        this.pos = pos;
        this.aspects = aspects;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.aspects = new AspectList();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            String aspectTag = ByteBufUtils.readUTF8String(buf);
            int amount = buf.readInt();
            Aspect aspect = Aspect.getAspect(aspectTag);
            if (aspect != null) {
                this.aspects.add(aspect, amount);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(aspects.size());
        for (Aspect aspect : aspects.getAspects()) {
            ByteBufUtils.writeUTF8String(buf, aspect.getTag());
            buf.writeInt(aspects.getAmount(aspect));
        }
    }

    public static class Handler implements IMessageHandler<PacketSyncAspects, IMessage> {
        @Override
        public IMessage onMessage(PacketSyncAspects message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                TileEntity tileEntity = Minecraft.getMinecraft().world.getTileEntity(message.pos);
                if (tileEntity instanceof DoubleTableTileEntity) {
                    ((DoubleTableTileEntity) tileEntity).setStoredAspects(message.aspects);
                }
            });
            return null;
        }
    }
}
