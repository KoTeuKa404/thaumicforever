package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
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
        ItemStack leftSlot = inventory.getStackInSlot(0);  // Лівий слот
        ItemStack rightSlot = inventory.getStackInSlot(1); // Правий слот
    
        if (!leftSlot.isEmpty() && !rightSlot.isEmpty() && isJewelryValid(rightSlot)) {
            System.out.println("Лівий слот до трансформації: " + leftSlot);
            System.out.println("Правий слот до трансформації: " + rightSlot);
    
            ItemStack transformedJewelry = new ItemStack(rightSlot.getItem(), rightSlot.getCount());
    
            if (rightSlot.hasTagCompound()) {
                transformedJewelry.setTagCompound(rightSlot.getTagCompound().copy());
            }
    
            if (leftSlot.hasTagCompound()) {
                if (transformedJewelry.hasTagCompound()) {
                    transformedJewelry.getTagCompound().merge(leftSlot.getTagCompound());
                } else {
                    transformedJewelry.setTagCompound(leftSlot.getTagCompound().copy());
                }
    
                String leftType = getBaubleTypeFrom(leftSlot);
                transformedJewelry.getTagCompound().setString("BaubleType", leftType);
            }
    
    
            inventory.setStackInSlot(1, transformedJewelry);
    
            leftSlot.shrink(1);
            if (leftSlot.getCount() <= 0) {
                inventory.setStackInSlot(0, ItemStack.EMPTY);
            }
    
            markDirty();
            System.out.println("Оновлено стан TileEntity.");
        }
    }
    
    
    
    
    
    
    
    

    
    
    private String getBaubleTypeFrom(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BaubleType")) {
            return stack.getTagCompound().getString("BaubleType");
        }
        return "unknown"; 
    }
    


    
    

    private boolean isJewelryValid(ItemStack stack) {
        return stack.getDisplayName().toLowerCase().contains("ring") ||
               stack.getDisplayName().toLowerCase().contains("amulet") ||
               stack.getDisplayName().toLowerCase().contains("head") ||
               stack.getDisplayName().toLowerCase().contains("belt") ||
               stack.getDisplayName().toLowerCase().contains("body") ||
               stack.getDisplayName().toLowerCase().contains("charm");
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }
    public boolean isUsableByPlayer(EntityPlayer player) {
    if (this.world.getTileEntity(this.pos) != this) {
        return false;
    } else {
        return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }
}

}
