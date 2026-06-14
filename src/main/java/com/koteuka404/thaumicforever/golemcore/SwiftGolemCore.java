package com.koteuka404.thaumicforever.golemcore;

import java.util.UUID;

import com.koteuka404.thaumicforever.api.golemcore.IGolemCore;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thaumcraft.api.golems.IGolemAPI;

public class SwiftGolemCore implements IGolemCore {
    public static final ResourceLocation ID = new ResourceLocation("thaumicforever", "swift");
    public static final UUID SPEED_MODIFIER_ID = UUID.fromString("875dedec-1ad9-4b93-b777-c37fe63113cf");

    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(SPEED_MODIFIER_ID, "Thaumic Forever Swift speed", 0.50D, 1).setSaved(false);

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public int getPlugColor() {
        return 0xFF4DEBFF;
    }

    @Override
    public boolean isActiveOnClient(EntityLivingBase golem) {
        return hasSwiftModifier(golem);
    }

    @Override
    public void onInstalled(IGolemAPI golem) {
        applyModifier(golem);
    }

    @Override
    public void onRemoved(IGolemAPI golem) {
        removeModifier(golem);
    }

    @Override
    public void onGolemTick(IGolemAPI golem) {
        applyModifier(golem);
    }

    @Override
    public void onGolemHurt(IGolemAPI golem, LivingHurtEvent event) {
        applyModifier(golem);
    }

    @Override
    public void onGolemDeath(IGolemAPI golem, DamageSource source) {
        removeModifier(golem);
    }

    public static boolean hasSwiftModifier(EntityLivingBase entity) {
        if (entity == null) return false;
        IAttributeInstance speed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        return speed != null && speed.getModifier(SPEED_MODIFIER_ID) != null;
    }

    public static void clearSwiftEffects(EntityLivingBase entity) {
        if (entity == null) return;
        removeModifier(entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED));
    }

    private static void applyModifier(IGolemAPI golem) {
        if (golem == null || golem.getGolemEntity() == null) return;
        IAttributeInstance speed = golem.getGolemEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (speed != null && speed.getModifier(SPEED_MODIFIER_ID) == null) {
            speed.applyModifier(SPEED_MODIFIER);
        }
    }

    private static void removeModifier(IGolemAPI golem) {
        if (golem == null || golem.getGolemEntity() == null) return;
        removeModifier(golem.getGolemEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED));
    }

    private static void removeModifier(IAttributeInstance attribute) {
        if (attribute != null && attribute.getModifier(SPEED_MODIFIER_ID) != null) {
            attribute.removeModifier(SPEED_MODIFIER_ID);
        }
    }
}
