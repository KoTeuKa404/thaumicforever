package com.koteuka404.thaumicforever.inventory;

import com.koteuka404.thaumicforever.storage.VoidChestWorldData;
import com.koteuka404.thaumicforever.tile.TileVoidChest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryVoidChest implements IInventory {
    private final TileVoidChest tile;
    private final VoidChestWorldData data;
    private final NonNullList<ItemStack> stacks;

    public InventoryVoidChest(TileVoidChest tile) {
        this.tile = tile;
        this.data = VoidChestWorldData.get(tile.getWorld());
        this.stacks = this.data.getInventory(tile.getNetworkId());
    }

    @Override
    public int getSizeInventory() {
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.stacks.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStackHelper.getAndSplit(this.stacks, index, count);
        if (!stack.isEmpty()) this.markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = ItemStackHelper.getAndRemove(this.stacks, index);
        if (!stack.isEmpty()) this.markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.stacks.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        this.data.markDirty();
        this.tile.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.tile.getWorld().getTileEntity(this.tile.getPos()) == this.tile
            && player.getDistanceSq(this.tile.getPos().getX() + 0.5D, this.tile.getPos().getY() + 0.5D, this.tile.getPos().getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.stacks.size(); i++) {
            this.stacks.set(i, ItemStack.EMPTY);
        }
        this.markDirty();
    }

    @Override
    public String getName() {
        return "container.thaumicforever.void_chest";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(this.getName());
    }
}
