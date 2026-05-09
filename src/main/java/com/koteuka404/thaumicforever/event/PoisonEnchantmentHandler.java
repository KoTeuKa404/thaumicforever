package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.recipe.EnumInfusionEnchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PoisonEnchantmentHandler {

    @SubscribeEvent
    public void onAttackEntity(LivingAttackEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }
        Entity source = event.getSource().getTrueSource();
        if (source == null) {
            source = event.getSource().getImmediateSource();
        }
        if (!(source instanceof EntityLivingBase)) {
            return;
        }

        EntityLivingBase attacker = (EntityLivingBase) source;
        if (!EquipmentEffectHelper.supportsEquipmentEffects(attacker)) {
            return;
        }

        ItemStack heldItem = attacker.getHeldItemMainhand();
        int poisonLevel = EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldItem, EnumInfusionEnchantment.POISON);
        if (poisonLevel <= 0 || event.getEntityLiving() == attacker) {
            return;
        }

        EntityLivingBase target = event.getEntityLiving();
        PotionEffect effect;

        if (poisonLevel >= 5) {
            target.removePotionEffect(MobEffects.WITHER);

            int witherDuration = 100 + (poisonLevel - 5) * 60;
            int witherAmplifier = poisonLevel - 5;
            effect = new PotionEffect(MobEffects.WITHER, witherDuration, witherAmplifier, false, true);
        } else {
            int poisonDuration = 100 + (poisonLevel - 1) * 60;
            int poisonAmplifier = poisonLevel - 1;
            effect = new PotionEffect(MobEffects.POISON, poisonDuration, poisonAmplifier, false, true);
        }

        target.addPotionEffect(effect);
    }
}
