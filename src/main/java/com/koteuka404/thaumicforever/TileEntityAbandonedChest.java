package com.koteuka404.thaumicforever;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityAbandonedChest extends TileEntityLockableLoot {

    private NonNullList<ItemStack> chestContents = NonNullList.withSize(27, ItemStack.EMPTY); 
    private String lootTableName = null;  
    public void setLootTable(String lootTableName) {
        this.lootTableName = lootTableName;
    }

    @Override
    public int getSizeInventory() {
        return this.chestContents.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.chestContents) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.chestContents.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.chestContents, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.chestContents, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.chestContents.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.chestContents);

        if (compound.hasKey("LootTableName", 8)) { 
            this.lootTableName = compound.getString("LootTableName");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, this.chestContents);

        if (this.lootTableName != null) {
            compound.setString("LootTableName", this.lootTableName);
        }
        return compound;
    }


    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this &&
               player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public void clear() {
        this.chestContents.clear();
    }

    @Override
    public String getName() {
        return "container.abandoned_chest";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(this.getName());
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

   @Nullable
    @Override
    public ResourceLocation getLootTable() {
        return new ResourceLocation("thaumicforever", "chests/hilltop_stones");
    }


    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerAbandonedChest(playerInventory, this);
    }

    @Override
    public String getGuiID() {
        return "thaumicforever:abandoned_chest";
    }
}
