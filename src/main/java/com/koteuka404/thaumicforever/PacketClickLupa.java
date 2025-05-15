package com.koteuka404.thaumicforever;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.aspects.AspectList;

public class PacketClickLupa implements IMessage {
    private BlockPos pos;

    public PacketClickLupa() {}

    public PacketClickLupa(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

   public static class Handler implements IMessageHandler<PacketClickLupa, IMessage> {
    @Override
public IMessage onMessage(PacketClickLupa message, MessageContext ctx) {
    ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
        TileEntity tileEntity = ctx.getServerHandler().player.world.getTileEntity(message.pos);
        if (tileEntity instanceof DoubleTableTileEntity) {
            DoubleTableTileEntity table = (DoubleTableTileEntity) tileEntity;

            table.updateAspectsFromInventory();

            AspectList updatedAspects = table.getStoredAspects();
            ThaumicForever.network.sendTo(
                new PacketSyncAspects(message.pos, updatedAspects),
                ctx.getServerHandler().player
            );
        }
    });
    return null;
}

    
}

}
