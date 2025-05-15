package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class PoisonEnchantmentHandler {


    @SubscribeEvent
public static void onAttackEntity(LivingHurtEvent event) {
    if (event.getSource().getTrueSource() instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        ItemStack heldItem = player.getHeldItemMainhand();

        int poisonLevel = EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldItem, EnumInfusionEnchantment.POISON);
        
        if (poisonLevel > 0 && event.getEntityLiving() instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase) event.getEntityLiving();
            
            PotionEffect effect;
            
            if (poisonLevel >= 5) {
                target.removePotionEffect(MobEffects.WITHER);
                
                int witherDuration = Math.max(40, (int) ((60 + (poisonLevel - 5) * 40) * 0.8)); 
                int witherAmplifier = poisonLevel - 5; 
                effect = new PotionEffect(MobEffects.WITHER, witherDuration, witherAmplifier, false, false);
            } else {
                int poisonDuration = Math.max(40, (int) ((60 + (poisonLevel - 1) * 40) * 0.8)); 
                int poisonAmplifier = poisonLevel - 1; 

                effect = new PotionEffect(MobEffects.POISON, poisonDuration, poisonAmplifier);
            }

            target.addPotionEffect(effect);
        }
    }
}


    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST) 
    public static void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        int poisonLevel = EnumInfusionEnchantment.getInfusionEnchantmentLevel(itemStack, EnumInfusionEnchantment.POISON);

        if (poisonLevel > 0) {
            List<String> tooltip = event.getToolTip();
            tooltip.add(1, TextFormatting.GOLD + "Poison " + TextFormatting.GRAY + "Lvl " + poisonLevel);
        }
    }
}
