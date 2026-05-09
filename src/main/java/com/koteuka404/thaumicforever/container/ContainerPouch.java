package com.koteuka404.thaumicforever.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.ClickType;
import com.koteuka404.thaumicforever.item.ItemPouch;

public class ContainerPouch extends ContainerChest {

    private final IInventory pouchInventory;

    public ContainerPouch(IInventory playerInventory, IInventory pouchInventory, EntityPlayer player) {
        super(playerInventory, pouchInventory, player);
        this.pouchInventory = pouchInventory;
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

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        if (slotIn != null && slotIn.inventory == this.pouchInventory && isPouchStack(stack)) {
            return false;
        }
        return super.canMergeSlot(stack, slotIn);
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (slotId >= 0 && slotId < this.inventorySlots.size()) {
            Slot slot = this.inventorySlots.get(slotId);
            if (slot != null && slot.inventory == this.pouchInventory) {
                ItemStack carried = player.inventory.getItemStack();
                if (isPouchStack(carried)) return ItemStack.EMPTY;
                if (clickTypeIn == ClickType.SWAP) {
                    ItemStack hotbarStack = player.inventory.getStackInSlot(dragType);
                    if (isPouchStack(hotbarStack)) return ItemStack.EMPTY;
                }
                if (clickTypeIn == ClickType.QUICK_CRAFT) {
                    if (isPouchStack(carried)) return ItemStack.EMPTY;
                }
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        if (isPouchStack(stack)) {
            for (int i = startIndex; i < endIndex; i++) {
                Slot slot = this.inventorySlots.get(i);
                if (slot != null && slot.inventory == this.pouchInventory) {
                    return false;
                }
            }
        }
        return super.mergeItemStack(stack, startIndex, endIndex, reverseDirection);
    }

    private static boolean isPouchStack(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemPouch;
    }
}
