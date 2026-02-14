package com.koteuka404.thaumicforever;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TrailMsg implements IMessage {
    public double fx, fy, fz, tx, ty, tz;

    public TrailMsg() {}
    public TrailMsg(Vec3d from, Vec3d to) {
        this.fx = from.x; this.fy = from.y; this.fz = from.z;
        this.tx = to.x;   this.ty = to.y;   this.tz = to.z;
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeDouble(fx); buf.writeDouble(fy); buf.writeDouble(fz);
        buf.writeDouble(tx); buf.writeDouble(ty); buf.writeDouble(tz);
    }
    @Override public void fromBytes(ByteBuf buf) {
        fx = buf.readDouble(); fy = buf.readDouble(); fz = buf.readDouble();
        tx = buf.readDouble(); ty = buf.readDouble(); tz = buf.readDouble();
    }

    public static class Handler implements IMessageHandler<TrailMsg, IMessage> {
        @Override
        public IMessage onMessage(TrailMsg msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                HungryFX.spawnAbsorbTrail(
                    Minecraft.getMinecraft().world,
                    new Vec3d(msg.fx, msg.fy, msg.fz),
                    new Vec3d(msg.tx, msg.ty, msg.tz)
                );
            });
            return null;
        }
    }
}
