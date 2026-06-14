package com.koteuka404.thaumicforever.golemcore;

import java.lang.reflect.Method;
import java.util.UUID;

import com.koteuka404.thaumicforever.api.golemcore.IGolemCore;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thaumcraft.api.golems.IGolemAPI;

public class GoliathGolemCore implements IGolemCore {
    public static final ResourceLocation ID = new ResourceLocation("thaumicforever", "goliath");
    public static final UUID HEALTH_MODIFIER_ID = UUID.fromString("5f0ed87a-3cc5-43d1-a9a8-3b93356c5450");
    public static final UUID ARMOR_MODIFIER_ID = UUID.fromString("a31454e0-a773-4e8c-a6bc-291c5cb0ec67");

    private static final float DAMAGE_MULTIPLIER = 0.85F;
    private static final AttributeModifier HEALTH_MODIFIER = new AttributeModifier(HEALTH_MODIFIER_ID, "Thaumic Forever Goliath health", 0.30D, 1).setSaved(false);
    private static final AttributeModifier ARMOR_MODIFIER = new AttributeModifier(ARMOR_MODIFIER_ID, "Thaumic Forever Goliath armor", 0.15D, 1).setSaved(false);
    private static final String NBT_GOLIATH_SIZE = "ThaumicForeverGoliathSize";
    private static final String NBT_BASE_WIDTH = "BaseWidth";
    private static final String NBT_BASE_HEIGHT = "BaseHeight";
    private static Method setSizeMethod;
    private static boolean setSizeMethodResolved;

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public int getPlugColor() {
        return 0xFFFF2020;
    }

    @Override
    public float getPlugScale() {
        return 1.0F;
    }

    @Override
    public boolean isActiveOnClient(EntityLivingBase golem) {
        return hasGoliathHealthModifier(golem);
    }

    @Override
    public void onInstalled(IGolemAPI golem) {
        applyModifiers(golem);
        if (golem.getGolemEntity() != null) {
            golem.getGolemEntity().heal(golem.getGolemEntity().getMaxHealth() * 0.30F);
        }
    }

    @Override
    public void onRemoved(IGolemAPI golem) {
        removeModifiers(golem);
        restoreGoliathSize(golem == null ? null : golem.getGolemEntity());
    }

    @Override
    public void onGolemTick(IGolemAPI golem) {
        applyModifiers(golem);
        applyGoliathSize(golem == null ? null : golem.getGolemEntity());
    }

    @Override
    public void onGolemHurt(IGolemAPI golem, LivingHurtEvent event) {
        applyModifiers(golem);
        if (event.getAmount() > 0.0F && !event.getSource().isUnblockable()) {
            event.setAmount(event.getAmount() * DAMAGE_MULTIPLIER);
        }
    }

    @Override
    public void onGolemDeath(IGolemAPI golem, DamageSource source) {
        removeModifiers(golem);
    }

    private static void applyModifiers(IGolemAPI golem) {
        if (golem == null || golem.getGolemEntity() == null) return;
        applyModifier(golem.getGolemEntity().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH), HEALTH_MODIFIER);
        applyModifier(golem.getGolemEntity().getEntityAttribute(SharedMonsterAttributes.ARMOR), ARMOR_MODIFIER);
        applyGoliathSize(golem.getGolemEntity());
    }

    private static void removeModifiers(IGolemAPI golem) {
        if (golem == null || golem.getGolemEntity() == null) return;
        removeModifier(golem.getGolemEntity().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH), HEALTH_MODIFIER_ID);
        removeModifier(golem.getGolemEntity().getEntityAttribute(SharedMonsterAttributes.ARMOR), ARMOR_MODIFIER_ID);
        restoreGoliathSize(golem.getGolemEntity());
    }

    public static boolean hasGoliathHealthModifier(EntityLivingBase entity) {
        if (entity == null) return false;
        IAttributeInstance maxHealth = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        return maxHealth != null && maxHealth.getModifier(HEALTH_MODIFIER_ID) != null;
    }

    public static void clearGoliathEffects(EntityLivingBase entity) {
        if (entity == null) return;
        removeModifier(entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH), HEALTH_MODIFIER_ID);
        removeModifier(entity.getEntityAttribute(SharedMonsterAttributes.ARMOR), ARMOR_MODIFIER_ID);
        restoreGoliathSize(entity);
    }

    public static void applyGoliathSize(EntityLivingBase entity) {
        if (entity == null) return;

        NBTTagCompound persistent = entity.getEntityData();
        NBTTagCompound sizeData = persistent.getCompoundTag(NBT_GOLIATH_SIZE);
        if (!sizeData.hasKey(NBT_BASE_WIDTH, 5) || !sizeData.hasKey(NBT_BASE_HEIGHT, 5)) {
            sizeData.setFloat(NBT_BASE_WIDTH, entity.width);
            sizeData.setFloat(NBT_BASE_HEIGHT, entity.height);
            persistent.setTag(NBT_GOLIATH_SIZE, sizeData);
        }

        float targetWidth = sizeData.getFloat(NBT_BASE_WIDTH) * 1.5F;
        float targetHeight = sizeData.getFloat(NBT_BASE_HEIGHT) * 1.5F;
        if (Math.abs(entity.width - targetWidth) > 0.001F || Math.abs(entity.height - targetHeight) > 0.001F) {
            resizeEntity(entity, targetWidth, targetHeight);
        }
    }

    public static void restoreGoliathSize(EntityLivingBase entity) {
        if (entity == null) return;

        NBTTagCompound persistent = entity.getEntityData();
        NBTTagCompound sizeData = persistent.getCompoundTag(NBT_GOLIATH_SIZE);
        if (!sizeData.hasKey(NBT_BASE_WIDTH, 5) || !sizeData.hasKey(NBT_BASE_HEIGHT, 5)) return;

        resizeEntity(entity, sizeData.getFloat(NBT_BASE_WIDTH), sizeData.getFloat(NBT_BASE_HEIGHT));
        persistent.removeTag(NBT_GOLIATH_SIZE);
    }

    private static void resizeEntity(EntityLivingBase entity, float width, float height) {
        Method method = getSetSizeMethod();
        if (method != null) {
            try {
                method.invoke(entity, width, height);
                return;
            } catch (ReflectiveOperationException ignored) {
            }
        }

        entity.width = width;
        entity.height = height;
        double halfWidth = width / 2.0D;
        entity.setEntityBoundingBox(new AxisAlignedBB(
                entity.posX - halfWidth,
                entity.posY,
                entity.posZ - halfWidth,
                entity.posX + halfWidth,
                entity.posY + height,
                entity.posZ + halfWidth));
    }

    private static Method getSetSizeMethod() {
        if (setSizeMethodResolved) return setSizeMethod;
        setSizeMethodResolved = true;

        for (String name : new String[] { "setSize", "func_70105_a" }) {
            try {
                Method method = net.minecraft.entity.Entity.class.getDeclaredMethod(name, float.class, float.class);
                method.setAccessible(true);
                setSizeMethod = method;
                return setSizeMethod;
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return null;
    }

    private static void applyModifier(IAttributeInstance attribute, AttributeModifier modifier) {
        if (attribute != null && attribute.getModifier(modifier.getID()) == null) {
            attribute.applyModifier(modifier);
        }
    }

    private static void removeModifier(IAttributeInstance attribute, UUID modifierId) {
        if (attribute != null && attribute.getModifier(modifierId) != null) {
            attribute.removeModifier(modifierId);
        }
    }
}
