package com.tutorialmod.turtywurty;

import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArmorStandToMannequinHandler {

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget() instanceof EntityArmorStand) {
            EntityPlayer player = event.getEntityPlayer();
            EntityArmorStand armorStand = (EntityArmorStand) event.getTarget();
            ItemStack heldItem = player.getHeldItem(event.getHand());
            World world = event.getWorld();
            BlockPos pos = armorStand.getPosition();

            // Якщо гравець використовує "магічний предмет" (наприклад, ModItems.MAGIC_DUST)
            if (heldItem.getItem() == ModItems.MAGIC_DUST) {
                if (!world.isRemote) {
                    // Вивантажуємо всі предмети зі стійки для броні
                    for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                        ItemStack itemStack = armorStand.getItemStackFromSlot(slot);
                        if (!itemStack.isEmpty()) {
                            armorStand.setItemStackToSlot(slot, ItemStack.EMPTY);
                            armorStand.entityDropItem(itemStack, 0.5F);
                        }
                    }

                    // Видаляємо ArmorStand
                    armorStand.setDead();

                    // Створюємо EntityGuardianMannequin на місці ArmorStand
                    EntityGuardianMannequin guardianMannequin = new EntityGuardianMannequin(world);
                    guardianMannequin.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);  // Встановлюємо точну позицію
                    world.spawnEntity(guardianMannequin);

                    // Знімаємо один предмет ModItems.MAGIC_DUST у гравця
                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                    }

                    // Програємо ефект взаємодії
                    world.playEvent(2001, pos, 0); // Ефект для руйнування стійки
                }
            }
        }
    }
}
