package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.recipe.EnumInfusionEnchantment;
import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.network.PacketRubyProtectFX;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RubyProtectEnchantmentHandler {

    @SubscribeEvent
    public void onPlayerDamaged(LivingHurtEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof EntityLivingBase && !entity.world.isRemote) {
            EntityLivingBase defender = (EntityLivingBase) entity;
            if (!EquipmentEffectHelper.supportsEquipmentEffects(defender)) {
                return;
            }

            Entity attacker = event.getSource().getTrueSource();
            if (attacker == null) {
                attacker = event.getSource().getImmediateSource();
            }

            if (attacker instanceof EntityLivingBase && attacker != defender) {
                int totalLevel = 0;
                for (ItemStack armorPiece : EquipmentEffectHelper.armor(defender)) {
                    totalLevel += EnumInfusionEnchantment.getInfusionEnchantmentLevel(armorPiece, EnumInfusionEnchantment.RUBYPROTECT);
                }
                if (totalLevel > 0) {
                    float damage = 1.0f + (totalLevel * 0.5f);
                    attacker.attackEntityFrom(DamageSource.causeThornsDamage(defender), damage);
                    if (totalLevel >= 10) {
                        attacker.setFire(Math.min(12, totalLevel - 7));
                    }
                    ThaumicForever.network.sendToAllAround(
                        new PacketRubyProtectFX(defender.getEntityId()),
                        new NetworkRegistry.TargetPoint(
                            defender.world.provider.getDimension(),
                            defender.posX,
                            defender.posY,
                            defender.posZ,
                            48.0D
                        )
                    );
                }
            }
        }
    }
}
