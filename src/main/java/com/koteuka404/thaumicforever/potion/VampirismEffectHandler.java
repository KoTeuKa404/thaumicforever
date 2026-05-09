package com.koteuka404.thaumicforever.potion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VampirismEffectHandler {

    private static final int REFRESH_DURATION_TICKS = 20 * 60 * 5; // 5 min, auto-refreshed while active.

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event == null || event.getEntityLiving() == null || event.getEntityLiving().world == null || event.getEntityLiving().world.isRemote) {
            return;
        }
        if (event.getAmount() <= 0.0F || event.isCanceled()) {
            return;
        }

        Entity source = event.getSource() != null ? event.getSource().getTrueSource() : null;
        if (!(source instanceof EntityLivingBase)) {
            return;
        }

        EntityLivingBase attacker = (EntityLivingBase) source;
        if (!attacker.isPotionActive(PotionVampirism.INSTANCE)) {
            return;
        }

        // 1 HP (half-heart) heal per successful hit.
        attacker.heal(1.0F);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null || entity.world == null || entity.world.isRemote) {
            return;
        }

        PotionEffect current = entity.getActivePotionEffect(PotionVampirism.INSTANCE);
        if (current == null) {
            return;
        }

        // Vampirism is treated as persistent state: once present, keep refreshing duration.
        if (current.getDuration() < REFRESH_DURATION_TICKS / 2) {
            entity.addPotionEffect(new PotionEffect(
                PotionVampirism.INSTANCE,
                REFRESH_DURATION_TICKS,
                current.getAmplifier(),
                current.getIsAmbient(),
                current.doesShowParticles()
            ));
        }
    }
}
