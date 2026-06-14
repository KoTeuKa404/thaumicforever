package com.koteuka404.thaumicforever.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketPrimalAuraConverterFX implements IMessage {
    public double x;
    public double y;
    public double z;
    public int color;

    public PacketPrimalAuraConverterFX() {}

    public PacketPrimalAuraConverterFX(double x, double y, double z, int color) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        color = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(color);
    }

    public static class Handler implements IMessageHandler<PacketPrimalAuraConverterFX, IMessage> {
        @Override
        public IMessage onMessage(PacketPrimalAuraConverterFX msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                if (world == null) return;

                for (int i = 0; i < 3; i++) {
                    double ox = (world.rand.nextDouble() - 0.5D) * 0.22D;
                    double oz = (world.rand.nextDouble() - 0.5D) * 0.22D;
                    double mx = (world.rand.nextDouble() - 0.5D) * 0.01D;
                    double my = 0.018D + world.rand.nextDouble() * 0.018D;
                    double mz = (world.rand.nextDouble() - 0.5D) * 0.01D;
                    com.koteuka404.thaumicforever.client.fx.FXDispatcher.INSTANCE.drawFocusCloudParticle(
                            msg.x + ox, msg.y + world.rand.nextDouble() * 0.18D, msg.z + oz,
                            mx, my, mz, msg.color);
                }
            });
            return null;
        }
    }
}
