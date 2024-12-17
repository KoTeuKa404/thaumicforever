package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAbandonedChest extends Container {
    private final IInventory tileEntity;

    public ContainerAbandonedChest(InventoryPlayer playerInventory, IInventory tileEntity) {
        this.tileEntity = tileEntity;
    
        int chestSlotStartX = 78; 
        int chestSlotStartY = 32;
    
        for (int i = 0; i < 3; ++i) {
            for (int j = 1; j < 8; ++j) {
                this.addSlotToContainer(new Slot(tileEntity, (j - 1) + i * 7, chestSlotStartX + (j - 1) * 18, chestSlotStartY + i * 18));
            }
        }
    
        int playerInventoryStartY = 100 + 8;
    
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, chestSlotStartX + j * 18 - 18, playerInventoryStartY + i * 18));
            }
        }
    
        int hotbarY = 160 + 6;
    
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInventory, i, chestSlotStartX + i * 18 - 18, hotbarY));
        }
    }
    

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileEntity.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.tileEntity.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack1, this.tileEntity.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.tileEntity.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}
