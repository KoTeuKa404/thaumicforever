package com.koteuka404.thaumicforever.wand.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.container.slot.SlotCrystal;

public class SlotCrystalLocked extends SlotCrystal {

    public SlotCrystalLocked(Aspect aspect, IInventory inventory, int index, int xPosition, int yPosition) {
        super(aspect, inventory, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeStack(net.minecraft.entity.player.EntityPlayer playerIn) {
        return false;
    }

    @Override
    public ItemStack getStack() {
        return ItemStack.EMPTY;
    }
}
