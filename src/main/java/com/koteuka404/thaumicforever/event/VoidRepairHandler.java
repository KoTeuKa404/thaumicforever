package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.recipe.EnumInfusionEnchantment;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class VoidRepairHandler {

    private static final int REPAIR_INTERVAL = 40;

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!EquipmentEffectHelper.supportsEquipmentEffects(entity)) {
            return;
        }

        for (ItemStack itemStack : EquipmentEffectHelper.equippedItems(entity)) {
            repairItem(itemStack, entity);
        }
    }

    private void repairItem(ItemStack itemStack, EntityLivingBase entity) {
        if (itemStack != null && itemStack.isItemDamaged()
            && EnumInfusionEnchantment.getInfusionEnchantmentLevel(itemStack, EnumInfusionEnchantment.VOIDREPAIR) > 0) {
            if (entity.world.getTotalWorldTime() % REPAIR_INTERVAL == 0) {
                itemStack.setItemDamage(itemStack.getItemDamage() - 1);
            }
        }
    }
}

