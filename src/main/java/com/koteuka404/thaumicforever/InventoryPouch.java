package com.koteuka404.thaumicforever;

import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class InventoryPouch extends InventoryBasic {

    public static final int SIZE = 27;
    private static final String NBT_ITEMS = "Items";

    private final ItemStack pouchStack;

    public InventoryPouch(ItemStack stack) {
        super("Pouch", false, SIZE);
        this.pouchStack = Objects.requireNonNull(stack, "pouchStack");
        readFromNBT();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return !pouchStack.isEmpty();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return !(stack.getItem() instanceof ItemPouch);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        writeToNBT();
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        super.closeInventory(player);
        writeToNBT();
    }

    private void readFromNBT() {
        if (!pouchStack.hasTagCompound()) return;
        NBTTagCompound tag = pouchStack.getTagCompound();
        if (tag == null || !tag.hasKey(NBT_ITEMS)) return;

        NonNullList<ItemStack> items = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, items);
        for (int i = 0; i < items.size(); i++) {
            setInventorySlotContents(i, items.get(i));
        }
    }

    private void writeToNBT() {
        if (pouchStack.isEmpty()) return;
        NBTTagCompound tag = pouchStack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
        }

        NonNullList<ItemStack> items = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, getStackInSlot(i));
        }
        ItemStackHelper.saveAllItems(tag, items);
        pouchStack.setTagCompound(tag);
    }
}
