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

public class IronSkinGolemCore implements IGolemCore {
    public static final ResourceLocation ID = new ResourceLocation("thaumicforever", "iron_skin");
    public static final UUID ARMOR_MODIFIER_ID = UUID.fromString("d2ed5321-2f5c-482f-a996-9f2a27f1f6ad");

    private static final float DAMAGE_MULTIPLIER = 0.60F;
    private static final AttributeModifier ARMOR_MODIFIER = new AttributeModifier(ARMOR_MODIFIER_ID, "Thaumic Forever Iron Skin armor", 0.60D, 1).setSaved(false);

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public int getPlugColor() {
        return 0xFFD8D8D8;
    }

    @Override
    public boolean isActiveOnClient(EntityLivingBase golem) {
        return hasIronSkinModifier(golem);
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
        if (event.getAmount() > 0.0F && !event.getSource().isUnblockable()) {
            event.setAmount(event.getAmount() * DAMAGE_MULTIPLIER);
        }
    }

    @Override
    public void onGolemDeath(IGolemAPI golem, DamageSource source) {
        removeModifier(golem);
    }

    private static boolean hasIronSkinModifier(EntityLivingBase entity) {
        if (entity == null) return false;
        IAttributeInstance armor = entity.getEntityAttribute(SharedMonsterAttributes.ARMOR);
        return armor != null && armor.getModifier(ARMOR_MODIFIER_ID) != null;
    }

    private static void applyModifier(IGolemAPI golem) {
        if (golem == null || golem.getGolemEntity() == null) return;
        IAttributeInstance armor = golem.getGolemEntity().getEntityAttribute(SharedMonsterAttributes.ARMOR);
        if (armor != null && armor.getModifier(ARMOR_MODIFIER_ID) == null) {
            armor.applyModifier(ARMOR_MODIFIER);
        }
    }

    private static void removeModifier(IGolemAPI golem) {
        if (golem == null || golem.getGolemEntity() == null) return;
        IAttributeInstance armor = golem.getGolemEntity().getEntityAttribute(SharedMonsterAttributes.ARMOR);
        if (armor != null && armor.getModifier(ARMOR_MODIFIER_ID) != null) {
            armor.removeModifier(ARMOR_MODIFIER_ID);
        }
    }
}
