package com.koteuka404.thaumicforever.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import com.koteuka404.thaumicforever.client.render.SkyBeamClientRenderer;

public class PacketSkyBeamFX implements IMessage {
    public double x1, y1, z1, x2, y2, z2;
    public int lifeTicks;
    public int color;
    public float width;

    public PacketSkyBeamFX() {}

    public PacketSkyBeamFX(double x1, double y1, double z1, double x2, double y2, double z2, int lifeTicks, int color, float width) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.lifeTicks = lifeTicks;
        this.color = color;
        this.width = width;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x1 = buf.readDouble();
        y1 = buf.readDouble();
        z1 = buf.readDouble();
        x2 = buf.readDouble();
        y2 = buf.readDouble();
        z2 = buf.readDouble();
        lifeTicks = buf.readInt();
        color = buf.readInt();
        width = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x1);
        buf.writeDouble(y1);
        buf.writeDouble(z1);
        buf.writeDouble(x2);
        buf.writeDouble(y2);
        buf.writeDouble(z2);
        buf.writeInt(lifeTicks);
        buf.writeInt(color);
        buf.writeFloat(width);
    }

    public static class Handler implements IMessageHandler<PacketSkyBeamFX, IMessage> {
        @Override
        public IMessage onMessage(PacketSkyBeamFX msg, MessageContext ctx) {
            SkyBeamClientRenderer.addBeam(msg);
            return null;
        }
    }
}

