package com.koteuka404.thaumicforever;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ReviveSkeletonEntity extends EntityCreature {

    public ReviveSkeletonEntity(World world) {
        super(world);
        this.setSize(0.6F, 1.8F); 
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D); 
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D); 
    }

    @Override
    public boolean canDespawn() {
        return false; 
    }

    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);
        ItemStack currentItem = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);

        if (!this.world.isRemote) {
            if (!heldItem.isEmpty()) {
                if (!currentItem.isEmpty()) {
                    player.addItemStackToInventory(currentItem);
                }
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, heldItem.copy());
                player.setHeldItem(hand, ItemStack.EMPTY);
                return EnumActionResult.SUCCESS;
            } else if (heldItem.isEmpty() && !currentItem.isEmpty()) {
                player.setHeldItem(hand, currentItem.copy());
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        ItemStack mainHandItem = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        if (!mainHandItem.isEmpty()) {
            System.out.println("ReviveSkeletonEntity is holding: " + mainHandItem.getDisplayName());
        } else {
            System.out.println("ReviveSkeletonEntity has empty hands.");
        }
    }
}
