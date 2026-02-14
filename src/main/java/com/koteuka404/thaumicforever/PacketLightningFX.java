package com.koteuka404.thaumicforever;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketLightningFX implements IMessage {
    public double x1, y1, z1, x2, y2, z2;
    public boolean hasBend;
    public double bx, by, bz;

    public boolean white;

    public PacketLightningFX() {}

    public PacketLightningFX(double x1, double y1, double z1, double x2, double y2, double z2) {
        this(x1, y1, z1, x2, y2, z2, false, 0, 0, 0, false);
    }

    public PacketLightningFX(double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             boolean hasBend, double bx, double by, double bz) {
        this(x1, y1, z1, x2, y2, z2, hasBend, bx, by, bz, false);
    }

    public PacketLightningFX(double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             boolean white) {
        this(x1, y1, z1, x2, y2, z2, false, 0, 0, 0, white);
    }

    public PacketLightningFX(double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             boolean hasBend, double bx, double by, double bz,
                             boolean white) {
        this.x1 = x1; this.y1 = y1; this.z1 = z1;
        this.x2 = x2; this.y2 = y2; this.z2 = z2;
        this.hasBend = hasBend;
        this.bx = bx; this.by = by; this.bz = bz;
        this.white = white;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x1 = buf.readDouble(); y1 = buf.readDouble(); z1 = buf.readDouble();
        x2 = buf.readDouble(); y2 = buf.readDouble(); z2 = buf.readDouble();
        hasBend = buf.readBoolean();
        bx = buf.readDouble(); by = buf.readDouble(); bz = buf.readDouble();
        white = buf.readBoolean(); // NEW
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x1); buf.writeDouble(y1); buf.writeDouble(z1);
        buf.writeDouble(x2); buf.writeDouble(y2); buf.writeDouble(z2);
        buf.writeBoolean(hasBend);
        buf.writeDouble(bx); buf.writeDouble(by); buf.writeDouble(bz);
        buf.writeBoolean(white); // NEW
    }

    public static class Handler implements IMessageHandler<PacketLightningFX, IMessage> {
        @Override
        public IMessage onMessage(PacketLightningFX msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World w = Minecraft.getMinecraft().world;
                if (msg.hasBend) {
                    Minecraft.getMinecraft().effectRenderer.addEffect(
                        new FXLightningBolt(w, msg.x1, msg.y1, msg.z1, msg.bx, msg.by, msg.bz, msg.white)
                    );
                    Minecraft.getMinecraft().effectRenderer.addEffect(
                        new FXLightningBolt(w, msg.bx, msg.by, msg.bz, msg.x2, msg.y2, msg.z2, msg.white)
                    );
                } else {
                    Minecraft.getMinecraft().effectRenderer.addEffect(
                        new FXLightningBolt(w, msg.x1, msg.y1, msg.z1, msg.x2, msg.y2, msg.z2, msg.white)
                    );
                }
            });
            return null;
        }
    }

    public static void sendWhiteWith25Bend(World world,
                                           double x1, double y1, double z1,
                                           double x2, double y2, double z2) {
        boolean bend = world.rand.nextFloat() < 0.25f;
        PacketLightningFX pkt;
        if (bend) {
            double midX = (x1 + x2) * 0.5;
            double midY = (y1 + y2) * 0.5;
            double midZ = (z1 + z2) * 0.5;
            double off = 0.5 + world.rand.nextDouble() * 0.5;
            midX += (world.rand.nextDouble() - 0.5) * off;
            midY += (world.rand.nextDouble() - 0.5) * off;
            midZ += (world.rand.nextDouble() - 0.5) * off;
            pkt = new PacketLightningFX(x1, y1, z1, x2, y2, z2, true, midX, midY, midZ, true);
        } else {
            pkt = new PacketLightningFX(x1, y1, z1, x2, y2, z2, true /*white*/);
        }

        double cx = (x1 + x2) * 0.5;
        double cy = (y1 + y2) * 0.5;
        double cz = (z1 + z2) * 0.5;
        ThaumicForever.network.sendToAllAround(
            pkt,
            new NetworkRegistry.TargetPoint(world.provider.getDimension(), cx, cy, cz, 48)
        );
    }
}
