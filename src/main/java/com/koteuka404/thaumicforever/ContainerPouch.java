package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPouch extends ContainerChest {

    public ContainerPouch(IInventory playerInventory, IInventory pouchInventory, EntityPlayer player) {
        super(playerInventory, pouchInventory, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            if (stack.getItem() instanceof ItemPouch) {
                return ItemStack.EMPTY;
            }
        }
        return super.transferStackInSlot(playerIn, index);
    }
}
