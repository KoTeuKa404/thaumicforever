package com.koteuka404.thaumicforever.api.golemcore;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thaumcraft.api.golems.IGolemAPI;

/**
 * Runtime behavior for a golem core. Register implementations through
 * {@link GolemCoreRegistry}; the active core id is stored on the golem entity.
 */
public interface IGolemCore {
    ResourceLocation getId();

    /**
     * ARGB color used to tint the small plug rendered on the golem body.
     */
    default int getPlugColor() {
        return 0xFFFFFFFF;
    }

    default float getPlugScale() {
        return 1.0F;
    }

    /**
     * Client-side fallback for visual-only state when persistent golem NBT is not
     * available on the client. Server logic still uses the stored active core id.
     */
    default boolean isActiveOnClient(EntityLivingBase golem) {
        return false;
    }

    default void onInstalled(IGolemAPI golem) {
    }

    default void onRemoved(IGolemAPI golem) {
    }

    default void onGolemTick(IGolemAPI golem) {
    }

    default void onGolemHurt(IGolemAPI golem, LivingHurtEvent event) {
    }

    default void onGolemAttack(IGolemAPI golem, EntityLivingBase target, DamageSource source, float amount) {
    }

    default void onGolemDeath(IGolemAPI golem, DamageSource source) {
    }
}
