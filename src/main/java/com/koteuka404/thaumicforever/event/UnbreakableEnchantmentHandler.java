package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.recipe.EnumInfusionEnchantment;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UnbreakableEnchantmentHandler {

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (!EquipmentEffectHelper.supportsEquipmentEffects(event.getEntityLiving())) {
            return;
        }

        for (ItemStack itemStack : EquipmentEffectHelper.equippedItems(event.getEntityLiving())) {
            keepUndamaged(itemStack);
        }
    }

    private void keepUndamaged(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return;
        }
        int level = EnumInfusionEnchantment.getInfusionEnchantmentLevel(itemStack, EnumInfusionEnchantment.UNBREAKABLE);
        if (level > 0) {
            NBTTagCompound tag = itemStack.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
            }
            if (!tag.getBoolean("Unbreakable")) {
                tag.setBoolean("Unbreakable", true);
            }
            int hideFlags = tag.hasKey("HideFlags") ? tag.getInteger("HideFlags") : 0;
            tag.setInteger("HideFlags", hideFlags | 4);
            itemStack.setTagCompound(tag);

            if (itemStack.isItemDamaged()) {
                itemStack.setItemDamage(0);
            }
        }
    }

}
