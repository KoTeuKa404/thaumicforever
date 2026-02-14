package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class DoubleTableContainer extends Container {
    private final IInventory tileEntity;

    public DoubleTableContainer(InventoryPlayer playerInventory, IInventory tileEntity) {
        this.tileEntity = tileEntity;

        this.addSlotToContainer(new Slot(tileEntity, 0, 15, 16));
        this.addSlotToContainer(new Slot(tileEntity, 1, 15, 32 + 8));
        this.addSlotToContainer(new Slot(tileEntity, 2, 15, 48 + 16));
        this.addSlotToContainer(new Slot(tileEntity, 3, 15, 64 + 25));
        this.addSlotToContainer(new Slot(tileEntity, 4, 15, 80 + 31));

        this.addSlotToContainer(new Slot(tileEntity, 5, 85, 64) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem().getRegistryName().toString().equals("thaumicforever:scroll_o");
            }
        });

        this.addSlotToContainer(new Slot(tileEntity, 6, 85, 96) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem().getRegistryName().toString().equals("minecraft:paper");
            }
        });

        int playerInventoryStartX = 43;
        int playerInventoryStartY = 160;
        int slotSpacingX = 17;
        int slotSpacingY = 18;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, playerInventoryStartX + j * slotSpacingX, playerInventoryStartY + i * slotSpacingY));
            }
        }

        int hotbarX = 43;
        int hotbarY = 218;
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(playerInventory, i, hotbarX + i * slotSpacingX, hotbarY));
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

            int containerSlotCount = 7;

            if (index < containerSlotCount) {
                if (!this.mergeItemStack(itemstack1, containerSlotCount, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {

                if (isScrollO(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 5, 6, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (isPaper(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 6, 7, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else {
                    if (!this.mergeItemStack(itemstack1, 0, containerSlotCount, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    private boolean isScrollO(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem().getRegistryName() == null) return false;
        return "thaumicforever:scroll_o".equals(stack.getItem().getRegistryName().toString());
    }

    private boolean isPaper(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem().getRegistryName() == null) return false;
        return "minecraft:paper".equals(stack.getItem().getRegistryName().toString());
    }

}
