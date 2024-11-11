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
        this.setSize(0.6F, 1.8F); // Розміри моба
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D); // Здоров'я
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D); // Швидкість
    }

    @Override
    public boolean canDespawn() {
        return false; // Забороняє мобу зникати автоматично
    }

    // Метод для взаємодії гравця з сутністю та передачі предмету
    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);
        ItemStack currentItem = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);

        if (!this.world.isRemote) {
            if (!heldItem.isEmpty()) {
                // Якщо у скелета є предмет у руці, передаємо його гравцеві
                if (!currentItem.isEmpty()) {
                    player.addItemStackToInventory(currentItem);
                }
                // Ставимо новий предмет у руку скелета
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, heldItem.copy());
                player.setHeldItem(hand, ItemStack.EMPTY);
                return EnumActionResult.SUCCESS;
            } else if (heldItem.isEmpty() && !currentItem.isEmpty()) {
                // Якщо гравець взаємодіє з порожньою рукою, а скелет тримає предмет
                player.setHeldItem(hand, currentItem.copy());
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    // Логіка для виведення у консоль інформації про предмет у руці для тестування
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
