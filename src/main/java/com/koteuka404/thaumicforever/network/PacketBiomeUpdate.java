package com.koteuka404.thaumicforever.network;

import com.koteuka404.thaumicforever.storage.BiomeSpreadWorldData;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBiomeUpdate implements IMessage {
    private int x;
    private int z;
    private int biomeId;

    public PacketBiomeUpdate() {}

    public PacketBiomeUpdate(int x, int z, int biomeId) {
        this.x = x;
        this.z = z;
        this.biomeId = biomeId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        z = buf.readInt();
        biomeId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(z);
        buf.writeInt(biomeId);
    }

    public static class Handler implements IMessageHandler<PacketBiomeUpdate, IMessage> {
        @Override
        public IMessage onMessage(PacketBiomeUpdate msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                Biome biome = Biome.getBiome(msg.biomeId);
                if (world != null && biome != null) {
                    BiomeSpreadWorldData.setBiomeAt(world, new BlockPos(msg.x, 0, msg.z), biome);
                }
            });
            return null;
        }
    }
}
