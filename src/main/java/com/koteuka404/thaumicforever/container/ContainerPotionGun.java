package com.koteuka404.thaumicforever.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import com.koteuka404.thaumicforever.item.ItemPotionGun;

public class ContainerPotionGun extends Container {

    private final IInventory gunInventory;

    public ContainerPotionGun(InventoryPlayer playerInventory, IInventory gunInventory, EntityPlayer player) {
        this.gunInventory = gunInventory;

        this.addSlotToContainer(new Slot(gunInventory, 0, 56, 64) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return ItemPotionGun.isValidAmmo(stack);
            }

            @Override
            public int getSlotStackLimit() {
                return 1;
            }
        });

        bindPlayerInventory(playerInventory);
    }

    private void bindPlayerInventory(InventoryPlayer playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 16 + j * 18, 151 + i * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            addSlotToContainer(new Slot(playerInventory, k, 16 + k * 18, 209));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.gunInventory.isUsableByPlayer(playerIn);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        this.gunInventory.closeInventory(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();

            if (index == 0) {
                if (!this.mergeItemStack(stackInSlot, 1, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (ItemPotionGun.isValidAmmo(stackInSlot)) {
                if (!this.mergeItemStack(stackInSlot, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 1 && index < 28) {
                if (!this.mergeItemStack(stackInSlot, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 28 && index < 37) {
                if (!this.mergeItemStack(stackInSlot, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stackInSlot);
        }

        return itemstack;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (slotId >= 0 && slotId < this.inventorySlots.size()) {
            Slot slot = this.inventorySlots.get(slotId);
            if (slot != null && slot.inventory == this.gunInventory) {
                ItemStack carried = player.inventory.getItemStack();
                if (!carried.isEmpty() && !ItemPotionGun.isValidAmmo(carried)) {
                    return ItemStack.EMPTY;
                }
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
}
