package com.koteuka404.thaumicforever.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import com.koteuka404.thaumicforever.client.fx.FXBleedingHit;

public class PacketBleedingFX implements IMessage {
    public int entityId;
    public int level;

    public PacketBleedingFX() {
    }

    public PacketBleedingFX(int entityId, int level) {
        this.entityId = entityId;
        this.level = level;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        level = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(level);
    }

    public static class Handler implements IMessageHandler<PacketBleedingFX, IMessage> {
        @Override
        public IMessage onMessage(PacketBleedingFX msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                if (world == null) {
                    return;
                }
                Entity entity = world.getEntityByID(msg.entityId);
                if (!(entity instanceof EntityLivingBase)) {
                    return;
                }

                EntityLivingBase target = (EntityLivingBase) entity;
                int count = 3 + Math.min(4, Math.max(1, msg.level));

                for (int i = 0; i < count; i++) {
                    double x = target.posX + (world.rand.nextDouble() - 0.5D) * target.width * 1.1D;
                    double y = target.posY + target.height * (0.25D + world.rand.nextDouble() * 0.55D);
                    double z = target.posZ + (world.rand.nextDouble() - 0.5D) * target.width * 1.1D;

                    double mx = (world.rand.nextDouble() - 0.5D) * 0.03D;
                    double my = 0.01D + world.rand.nextDouble() * 0.02D;
                    double mz = (world.rand.nextDouble() - 0.5D) * 0.03D;

                    Minecraft.getMinecraft().effectRenderer.addEffect(
                        new FXBleedingHit(world, x, y, z, mx, my, mz)
                    );
                }
            });
            return null;
        }
    }
}
