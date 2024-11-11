package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerRepurposer extends Container {

    private final TileEntityRepurposer tileEntity;

    public ContainerRepurposer(InventoryPlayer playerInv, TileEntityRepurposer tileEntity) {
        this.tileEntity = tileEntity;

        // Input and output slots з обмеженням на тип предметів
        this.addSlotToContainer(new JewelrySlot(tileEntity.getInventory(), 0, 55, 13)); // Input слот
        this.addSlotToContainer(new JewelrySlot(tileEntity.getInventory(), 1, 104, 13)); // Output слот

        // Player inventory slots
        bindPlayerInventory(playerInv);
    }

    private void bindPlayerInventory(InventoryPlayer playerInventory) {
        int inventoryYOffset = 59; // Зміщення Y для інвентаря гравця
        int hotbarYOffset = 117; // Зміщення Y для хот-бару

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, inventoryYOffset + i * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, hotbarYOffset));
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
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();

            // Якщо предмет з блоку (слоти 0 та 1), переміщаємо в інвентар гравця
            if (index < 2) {
                if (!this.mergeItemStack(stackInSlot, 2, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stackInSlot, itemstack);
            } else {
                // Якщо предмет з інвентаря гравця, переміщаємо у відповідні слоти блоку
                if (index >= 2 && index < this.inventorySlots.size()) {
                    if (!this.mergeItemStack(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
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

    private static class JewelrySlot extends SlotItemHandler {
        public JewelrySlot(net.minecraftforge.items.IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }
    
        @Override
        public boolean isItemValid(ItemStack stack) {
            // Перевіряємо, чи предмет є одним з типів біжутерії
            return isJewelryValid(stack);
        }
    
        private boolean isJewelryValid(ItemStack stack) {
            if (stack.getDisplayName().toLowerCase().contains("ring") ||
                stack.getDisplayName().toLowerCase().contains("amulet") ||
                stack.getDisplayName().toLowerCase().contains("head") ||
                stack.getDisplayName().toLowerCase().contains("belt") ||
                stack.getDisplayName().toLowerCase().contains("body") ||
                stack.getDisplayName().toLowerCase().contains("charm")) {
                return true;
            }
            return false;
        }
    }
    
}
