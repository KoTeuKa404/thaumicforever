package com.koteuka404.thaumicforever.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ParticleEngine;
import com.koteuka404.thaumicforever.client.fx.FXExplosionWisp;

public class PacketKatanaSlashFX implements IMessage {
    private static final int SLASH_GRID = 64;
    private static final int SLASH_START_INDEX = 22 * 64 + 44; // x=704, y=352 on 16px tiles in a 1024 texture.
    private static final int SLASH_FRAMES = 7;

    public double x;
    public double y;
    public double z;
    public double dx;
    public double dy;
    public double dz;

    public PacketKatanaSlashFX() {
    }

    public PacketKatanaSlashFX(double x, double y, double z, double dx, double dy, double dz) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        dx = buf.readDouble();
        dy = buf.readDouble();
        dz = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(dx);
        buf.writeDouble(dy);
        buf.writeDouble(dz);
    }

    public static class Handler implements IMessageHandler<PacketKatanaSlashFX, IMessage> {
        @Override
        public IMessage onMessage(PacketKatanaSlashFX msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() ->
                spawnSlashClient(Minecraft.getMinecraft().world, msg.x, msg.y, msg.z, msg.dx, msg.dy, msg.dz)
            );
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void spawnSlashClient(World world, double x, double y, double z, double dx, double dy, double dz) {
        Minecraft mc = Minecraft.getMinecraft();
        if (world == null || mc == null || mc.gameSettings == null) {
            return;
        }

        int particleSetting = mc.gameSettings.particleSetting;
        if (particleSetting >= 2) {
            return;
        }

        Vec3d forward = new Vec3d(dx, dy, dz);
        if (forward.lengthSquared() < 1.0E-6D) {
            forward = new Vec3d(0.0D, 0.0D, 1.0D);
        } else {
            forward = forward.normalize();
        }

        Vec3d base = new Vec3d(x, y, z).add(forward.scale(0.8D));

        // 7 separate frames from 4particles, played in sequence upward on Y.
        for (int i = 0; i < SLASH_FRAMES; i++) {
            double yLift = i * 0.07D;
            double px = base.x + (world.rand.nextDouble() - 0.5D) * 0.01D;
            double py = base.y + yLift;
            double pz = base.z + (world.rand.nextDouble() - 0.5D) * 0.01D;

            FXExplosionWisp fx = new FXExplosionWisp(
                world, px, py, pz, 0.0D, 0.0D, 0.0D, SLASH_GRID, SLASH_START_INDEX + i, 1
            ).tune(1.45F, 0.35F, 0.72F, 1.0F, 0.95F, 6);
            ParticleEngine.addEffectWithDelay(world, fx, i * 5);
        }
    }
}
