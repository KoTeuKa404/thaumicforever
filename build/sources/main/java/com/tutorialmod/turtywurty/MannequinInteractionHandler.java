package com.tutorialmod.turtywurty;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MannequinInteractionHandler {

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        // Взаємодія з EntityGuardianMannequin
        if (event.getTarget() instanceof EntityGuardianMannequin) {
            EntityPlayer player = event.getEntityPlayer();
            EntityGuardianMannequin mannequin = (EntityGuardianMannequin) event.getTarget();
            ItemStack heldItem = player.getHeldItem(event.getHand());

            // Якщо предмет — броня
            if (heldItem.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) heldItem.getItem();
                EntityEquipmentSlot armorSlot = armor.armorType;  // Отримуємо тип слота для броні
                ItemStack currentArmor = mannequin.getItemStackFromSlot(armorSlot);

                // Міняємо броню манекена на ту, що тримає гравець
                if (currentArmor.isEmpty()) {
                    mannequin.setItemStackToSlot(armorSlot, heldItem.copy());
                    player.setHeldItem(event.getHand(), ItemStack.EMPTY);
                } else {
                    player.setHeldItem(event.getHand(), currentArmor);
                    mannequin.setItemStackToSlot(armorSlot, heldItem.copy());
                }
            }

            // Якщо предмет — зброя (меч або інструмент)
            else if (heldItem.getItem() instanceof ItemSword || heldItem.getItem() instanceof ItemTool) {
                EntityEquipmentSlot handSlot = EntityEquipmentSlot.MAINHAND;  // Основна рука
                ItemStack currentWeapon = mannequin.getItemStackFromSlot(handSlot);

                // Міняємо зброю манекена на ту, що тримає гравець
                if (currentWeapon.isEmpty()) {
                    mannequin.setItemStackToSlot(handSlot, heldItem.copy());
                    player.setHeldItem(event.getHand(), ItemStack.EMPTY);
                } else {
                    player.setHeldItem(event.getHand(), currentWeapon);
                    mannequin.setItemStackToSlot(handSlot, heldItem.copy());
                }
            }
        }
    }
}
