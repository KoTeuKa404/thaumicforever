package com.koteuka404.thaumicforever.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileCustomFlowerPot extends TileEntity {
    private ItemStack plant = ItemStack.EMPTY;

    public ItemStack getPlant() {
        return plant;
    }

    public boolean isEmpty() {
        return plant.isEmpty();
    }

    public void setPlant(ItemStack stack) {
        if (stack.isEmpty()) {
            plant = ItemStack.EMPTY;
        } else {
            plant = stack.copy();
            plant.setCount(1);
        }
        markDirty();
        sync();
    }

    public ItemStack removePlant() {
        ItemStack out = plant;
        plant = ItemStack.EMPTY;
        markDirty();
        sync();
        return out;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!plant.isEmpty()) {
            compound.setTag("Plant", plant.writeToNBT(new NBTTagCompound()));
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Plant", 10)) {
            plant = new ItemStack(compound.getCompoundTag("Plant"));
        } else {
            plant = ItemStack.EMPTY;
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(super.getUpdateTag());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        if (world != null) {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    public void sync() {
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }
}
