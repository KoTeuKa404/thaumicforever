package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityRepurposer extends TileEntity implements ITickable {

    private ItemStackHandler inventory = new ItemStackHandler(2); 
    
    @Override
    public void update() {
        if (!world.isRemote) {
            handleJewelryTransformation();
        }
    }

    private void handleJewelryTransformation() {
        ItemStack leftSlot = inventory.getStackInSlot(0);  
        ItemStack rightSlot = inventory.getStackInSlot(1); 

        if (!leftSlot.isEmpty() && !rightSlot.isEmpty() && isJewelryValid(rightSlot)) {

            Item rightItem = rightSlot.getItem();
            Item leftItem = leftSlot.getItem();

            if (rightItem instanceof IBauble && leftItem instanceof IBauble) {
                if (leftItem instanceof MutableBaubleItem) {
                    BaubleType newType = ((IBauble) leftItem).getBaubleType(leftSlot);

                    if (rightItem instanceof MutableBaubleItem) {
                        MutableBaubleItem mutableBauble = (MutableBaubleItem) rightItem;
                        mutableBauble.setBaubleType(rightSlot, newType);

                        inventory.setStackInSlot(1, rightSlot);
                        markDirty();

                        inventory.setStackInSlot(0, ItemStack.EMPTY);
                    } 
                } 
            } 
        }         
    }

    private boolean isJewelryValid(ItemStack stack) {
        if (stack.getItem() instanceof IBauble) {
            return true;
        } else {
            return false;
        }
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }
    
    public boolean isUsableByPlayer(EntityPlayer playerIn) {
        return this.world.getTileEntity(this.pos) == this && playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }
}

class MutableBaubleItem extends Item implements IBauble {
    private BaubleType baubleType;

    public MutableBaubleItem(BaubleType defaultType) {
        this.baubleType = defaultType;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return this.baubleType;
    }

    public void setBaubleType(ItemStack itemstack, BaubleType newType) {
        this.baubleType = newType;
    }
}
