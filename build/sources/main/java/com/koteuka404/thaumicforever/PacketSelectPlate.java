package com.koteuka404.thaumicforever;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSelectPlate implements IMessage {
    private ItemStack selectedPlate;
    private BlockPos pos; // Додайте поле для позиції

    // Порожній конструктор для десеріалізації
    public PacketSelectPlate() {}

    // Конструктор для ініціалізації з параметрами
    public PacketSelectPlate(ItemStack selectedPlate, BlockPos pos) {
        this.selectedPlate = selectedPlate;
        this.pos = pos; // Ініціалізуйте позицію
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.selectedPlate = new ItemStack(ByteBufUtils.readTag(buf));
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()); // Читаємо координати
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.selectedPlate.writeToNBT(new NBTTagCompound()));
        buf.writeInt(pos.getX()); // Записуємо X координату
        buf.writeInt(pos.getY()); // Записуємо Y координату
        buf.writeInt(pos.getZ()); // Записуємо Z координату
    }

    public static class Handler implements IMessageHandler<PacketSelectPlate, IMessage> {
        @Override
        public IMessage onMessage(PacketSelectPlate message, MessageContext ctx) {
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                TileEntityCompressor tile = (TileEntityCompressor) ctx.getServerHandler().player.world.getTileEntity(message.pos);
                if (tile != null) {
                    tile.setSelectedPlate(message.selectedPlate);
                }
            });
            return null;
        }
    }
}
