package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class RubyProtectEnchantmentHandler {

    @SubscribeEvent
    public static void onPlayerDamaged(LivingHurtEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof EntityPlayer && !entity.world.isRemote) {
            EntityPlayer player = (EntityPlayer) entity;
            Entity attacker = event.getSource().getTrueSource();

            if (attacker instanceof EntityLivingBase) {
                for (ItemStack armorPiece : player.getArmorInventoryList()) {
                    int level = EnumInfusionEnchantment.getInfusionEnchantmentLevel(armorPiece, EnumInfusionEnchantment.RUBYPROTECT);
                    if (level > 0) {
                        float damage = 1.0f + (level * 0.5f);
                        attacker.attackEntityFrom(DamageSource.causeThornsDamage(player), damage);

                        spawnRedRunesEffect(player);

                        break;
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static void spawnRedRunesEffect(EntityPlayer player) {
        Minecraft.getMinecraft().effectRenderer.addEffect(
            new FXRubyRunes(
                player.world,
                player.posX,
                player.posY + player.height / 2.0,
                player.posZ,
                player,
                30,
                player.rotationYaw,
                player.rotationPitch
            )
        );
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        int level = EnumInfusionEnchantment.getInfusionEnchantmentLevel(itemStack, EnumInfusionEnchantment.RUBYPROTECT);

        if (level > 0) {
            List<String> tooltip = event.getToolTip();
            tooltip.add(1, TextFormatting.GOLD + "Ruby Protection " + TextFormatting.GRAY + "Lvl " + level);
        }
    }
}
