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

public class ArcaneGolemCore implements IGolemCore {
    public static final ResourceLocation ID = new ResourceLocation("thaumicforever", "arcane");
    public static final UUID MARKER_MODIFIER_ID = UUID.fromString("52c45201-a4f0-4f48-9fbb-94dd6b4cbdad");

    private static final AttributeModifier MARKER_MODIFIER = new AttributeModifier(MARKER_MODIFIER_ID, "Thaumic Forever Arcane core marker", 0.0D, 0).setSaved(false);

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public int getPlugColor() {
        return 0xFF9F58FF;
    }

    @Override
    public boolean isActiveOnClient(EntityLivingBase golem) {
        return hasArcaneMarker(golem);
    }

    @Override
    public void onInstalled(IGolemAPI golem) {
        applyMarker(golem);
    }

    @Override
    public void onRemoved(IGolemAPI golem) {
        removeMarker(golem);
    }

    @Override
    public void onGolemTick(IGolemAPI golem) {
        applyMarker(golem);
    }

    @Override
    public void onGolemHurt(IGolemAPI golem, LivingHurtEvent event) {
        applyMarker(golem);
    }

    @Override
    public void onGolemDeath(IGolemAPI golem, DamageSource source) {
        removeMarker(golem);
    }

    private static boolean hasArcaneMarker(EntityLivingBase entity) {
        if (entity == null) return false;
        IAttributeInstance attribute = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        return attribute != null && attribute.getModifier(MARKER_MODIFIER_ID) != null;
    }

    private static void applyMarker(IGolemAPI golem) {
        if (golem == null || golem.getGolemEntity() == null) return;
        IAttributeInstance attribute = golem.getGolemEntity().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        if (attribute != null && attribute.getModifier(MARKER_MODIFIER_ID) == null) {
            attribute.applyModifier(MARKER_MODIFIER);
        }
    }

    private static void removeMarker(IGolemAPI golem) {
        if (golem == null || golem.getGolemEntity() == null) return;
        IAttributeInstance attribute = golem.getGolemEntity().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        if (attribute != null && attribute.getModifier(MARKER_MODIFIER_ID) != null) {
            attribute.removeModifier(MARKER_MODIFIER_ID);
        }
    }
}
