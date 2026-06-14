package com.koteuka404.thaumicforever.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerVoidChest extends Container {
    private static final int CHEST_ROWS = 8;
    private static final int CHEST_COLUMNS = 9;
    private static final int CHEST_START_X = 8;
    private static final int CHEST_START_Y = 9;
    private static final int PLAYER_INV_START_Y = 158;
    private static final int HOTBAR_Y = 216;

    private final IInventory chestInventory;

    public ContainerVoidChest(InventoryPlayer playerInventory, IInventory chestInventory) {
        this.chestInventory = chestInventory;
        chestInventory.openInventory(playerInventory.player);

        for (int row = 0; row < CHEST_ROWS; row++) {
            for (int col = 0; col < CHEST_COLUMNS; col++) {
                this.addSlotToContainer(new Slot(chestInventory, col + row * CHEST_COLUMNS, CHEST_START_X + col * 18, CHEST_START_Y + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, PLAYER_INV_START_Y + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, HOTBAR_Y));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.chestInventory.isUsableByPlayer(playerIn);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        this.chestInventory.closeInventory(playerIn);
    }


    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();

            if (index < this.chestInventory.getSizeInventory()) {
                if (!this.mergeItemStack(stack, this.chestInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack, 0, this.chestInventory.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return result;
    }
}
