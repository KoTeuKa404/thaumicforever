package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.item.ItemWarpBlade;
import com.koteuka404.thaumicforever.registry.ModItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WarpBladeDamageHandler {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        Entity trueSource = event.getSource().getTrueSource();

        if (!(trueSource instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) trueSource;

        if (!"player".equals(event.getSource().getDamageType())) {
            return;
        }

        ItemStack held = player.getHeldItemMainhand();

        if (held.isEmpty() || held.getItem() != ModItems.WARP_BLADE) {
            return;
        }

        float bonusDamage = ItemWarpBlade.getWarpBonusDamage(player);

        if (bonusDamage <= 0.0F) {
            return;
        }

        event.setAmount(event.getAmount() + bonusDamage);
    }
}