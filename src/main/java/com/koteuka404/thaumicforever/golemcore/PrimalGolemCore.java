package com.koteuka404.thaumicforever.golemcore;

import java.util.UUID;

import com.koteuka404.thaumicforever.api.golemcore.IGolemCore;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.golems.IGolemAPI;

public class PrimalGolemCore implements IGolemCore {
    public static final ResourceLocation ID = new ResourceLocation("thaumicforever", "primal");
    public static final UUID MARKER_MODIFIER_ID = UUID.fromString("d7ca8fa9-6637-4f3d-954d-b585b87532d7");
    private static final UUID TERRA_MARKER_ID = UUID.fromString("9bb5390c-61a2-4782-b979-1bfae556271f");
    private static final UUID IGNIS_MARKER_ID = UUID.fromString("a412d6fe-58b6-4c74-ae97-3dbbfecf0609");
    private static final UUID AQUA_MARKER_ID = UUID.fromString("20778fe7-14ea-42fc-b7d6-18bd482093bc");
    private static final UUID AER_MARKER_ID = UUID.fromString("b3461e2c-8d91-4e29-bba4-69319f731547");
    private static final UUID PERDITIO_MARKER_ID = UUID.fromString("f3089fb1-01aa-4ad4-b739-0284904f7848");
    private static final UUID ORDO_MARKER_ID = UUID.fromString("4445be4e-9193-472f-9e5e-499f032cb643");

    private static final String NBT_ROOT = "ThaumicForeverPrimalCore";
    private static final String NBT_ASPECT = "Aspect";
    private static final String NBT_ASPECT_UNTIL = "AspectUntil";
    private static final AttributeModifier MARKER_MODIFIER = new AttributeModifier(MARKER_MODIFIER_ID, "Thaumic Forever Primal core marker", 0.0001D, 0).setSaved(false);
    private static final double ASPECT_MARKER_AMOUNT = 0.0001D;

    private static final int ASPECT_DURATION = 120; // 6 sec

    private enum PrimalAspect {
        TERRA("terra"),
        IGNIS("ignis"),
        AQUA("aqua"),
        AER("aer"),
        PERDITIO("perditio"),
        ORDO("ordo");

        private final String id;

        PrimalAspect(String id) {
            this.id = id;
        }

        private static PrimalAspect fromString(String id) {
            for (PrimalAspect aspect : values()) {
                if (aspect.id.equals(id)) {
                    return aspect;
                }
            }

            return ORDO;
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public int getPlugColor() {
        return 0xFFF9B3B6;
    }

    @Override
    public int getPlugColor(EntityLivingBase golem) {
        PrimalAspect aspect = getClientAspect(golem);
        return aspect == null ? getPlugColor() : getAspectColor(aspect);
    }

    @Override
    public float getPlugScale() {
        return 1.08F;
    }

    @Override
    public void onInstalled(IGolemAPI golem) {
        applyMarker(golem);
        EntityLivingBase entity = getEntity(golem);
        if (entity != null) {
            setAspect(entity, PrimalAspect.ORDO, ASPECT_DURATION);
        }
    }

    @Override
    public void onRemoved(IGolemAPI golem) {
        removeMarker(golem);
        EntityLivingBase entity = getEntity(golem);
        if (entity != null) {
            clearAspectMarkers(entity);
            entity.getEntityData().removeTag(NBT_ROOT);
        }
    }

    @Override
    public void onGolemTick(IGolemAPI golem) {
        applyMarker(golem);
        EntityLivingBase entity = getEntity(golem);
        if (entity == null || entity.world.isRemote) {
            return;
        }

        boolean expired = isAspectExpired(entity);
        if (expired) {
            clearAspectMarkers(entity);
        }

        // Emergency adaptation. Aspect can only switch after the 6-second lock expires.
        if (expired && isFallingDanger(entity)) {
            setAspect(entity, PrimalAspect.AER, ASPECT_DURATION);
            applyAer(entity);
            return;
        }

        if (expired && isFireDanger(entity)) {
            setAspect(entity, PrimalAspect.IGNIS, ASPECT_DURATION);
            applyIgnis(entity);
            return;
        }

        if (expired && isDrowningDanger(entity)) {
            setAspect(entity, PrimalAspect.AQUA, ASPECT_DURATION);
            applyAqua(entity);
            return;
        }

        if (expired && isPerditioDanger(entity)) {
            setAspect(entity, PrimalAspect.PERDITIO, ASPECT_DURATION);
            applyPerditio(entity);
            return;
        }

        PrimalAspect aspect = getAspect(entity);
        if (!expired) {
            applyPassiveAspect(entity, aspect);
        }
    }

    @Override
    public void onGolemHurt(IGolemAPI golem, LivingHurtEvent event) {
        applyMarker(golem);
        EntityLivingBase entity = getEntity(golem);
        if (entity == null || entity.world.isRemote) {
            return;
        }

        DamageSource source = event.getSource();

        if (source == null || source == DamageSource.OUT_OF_WORLD) {
            return;
        }

        PrimalAspect damageAspect = getAspectForDamage(source);

        if (damageAspect == null) {
            return;
        }

        PrimalAspect currentAspect = getAspect(entity);
        boolean expired = isAspectExpired(entity);

        if (!expired && currentAspect == damageAspect) {
            applyPassiveAspect(entity, damageAspect);
            blockDamage(event);
            return;
        }

        if (!expired) {
            applyPassiveAspect(entity, currentAspect);
            return;
        }

        setAspect(entity, damageAspect, ASPECT_DURATION);
        applyPassiveAspect(entity, damageAspect);
        blockDamage(event);
    }

    @Override
    public void onGolemDeath(IGolemAPI golem, DamageSource source) {
        removeMarker(golem);
        clearAspectMarkers(getEntity(golem));
    }

    private static PrimalAspect getAspectForDamage(DamageSource source) {
        if (source == null) {
            return null;
        }

        // ORDO
        if (isProjectileDamage(source)) {
            return PrimalAspect.ORDO;
        }

        // AER
        if (isFallDamage(source)) {
            return PrimalAspect.AER;
        }

        // AQUA
        if (isDrownDamage(source)) {
            return PrimalAspect.AQUA;
        }

        // IGNIS
        if (isFireDamage(source)) {
            return PrimalAspect.IGNIS;
        }

        // TERRA
        if (isDirectLivingDamage(source)) {
            return PrimalAspect.TERRA;
        }

        // PERDITIO
        if (isPerditioDamage(source)) {
            return PrimalAspect.PERDITIO;
        }

        return PrimalAspect.TERRA;
    }

    private static EntityLivingBase getEntity(IGolemAPI golem) {
        if (golem == null) {
            return null;
        }

        return golem.getGolemEntity();
    }

    private static void blockDamage(LivingHurtEvent event) {
        event.setAmount(0.0F);
        event.setCanceled(true);
    }

    private static boolean isProjectileDamage(DamageSource source) {
        return source != null && source.isProjectile();
    }

    private static boolean isFallDamage(DamageSource source) {
        return source == DamageSource.FALL
            || source != null && "fall".equals(source.getDamageType());
    }

    private static boolean isDrownDamage(DamageSource source) {
        return source == DamageSource.DROWN
            || source != null && "drown".equals(source.getDamageType());
    }

    private static boolean isFireDamage(DamageSource source) {
        return source != null && source.isFireDamage();
    }

    private static boolean isDirectLivingDamage(DamageSource source) {
        return source != null
            && source.getTrueSource() instanceof EntityLivingBase
            && source.getImmediateSource() == source.getTrueSource();
    }

    @Override
    public boolean isActiveOnClient(EntityLivingBase golem) {
        return hasPrimalMarker(golem);
    }

    private static boolean isPerditioDamage(DamageSource source) {
        if (source == null) {
            return false;
        }

        if (source == DamageSource.WITHER) {
            return true;
        }

        String type = source.getDamageType();

        return "wither".equals(type)
            || "poison".equals(type)
            || source.isMagicDamage();
    }

    private static boolean isFallingDanger(EntityLivingBase entity) {
        return !entity.onGround
            && entity.motionY < -0.25D
            && entity.fallDistance > 2.5F;
    }

    private static boolean isFireDanger(EntityLivingBase entity) {
        return entity.isBurning() || entity.isInLava();
    }

    private static boolean isDrowningDanger(EntityLivingBase entity) {
        return entity.isInWater() && entity.getAir() < 260;
    }

    private static boolean isPerditioDanger(EntityLivingBase entity) {
        return entity.isPotionActive(MobEffects.POISON)
            || entity.isPotionActive(MobEffects.WITHER);
    }

    private static void applyPassiveAspect(EntityLivingBase entity, PrimalAspect aspect) {
        switch (aspect) {
            case AER:
                applyAer(entity);
                break;

            case IGNIS:
                applyIgnis(entity);
                break;

            case AQUA:
                applyAqua(entity);
                break;

            case PERDITIO:
                applyPerditio(entity);
                break;

            case TERRA:
                applyTerra(entity);
                break;

            case ORDO:
            default:
                stabilizeOrdo(entity);
                break;
        }
    }

    private static void applyTerra(EntityLivingBase entity) {
        entity.motionX *= 0.15D;
        entity.motionZ *= 0.15D;
        entity.velocityChanged = true;
    }

    private static void applyIgnis(EntityLivingBase entity) {
        entity.extinguish();
        entity.setFire(0);
    }

    private static void applyAqua(EntityLivingBase entity) {
        entity.setAir(300);
    }

    private static void applyAer(EntityLivingBase entity) {
        entity.fallDistance = 0.0F;

        if (entity.motionY < -0.20D) {
            entity.motionY = -0.20D;
            entity.velocityChanged = true;
        }
    }

    private static void applyPerditio(EntityLivingBase entity) {
        if (entity.isPotionActive(MobEffects.POISON)) {
            entity.removePotionEffect(MobEffects.POISON);
        }

        if (entity.isPotionActive(MobEffects.WITHER)) {
            entity.removePotionEffect(MobEffects.WITHER);
        }
    }

    private static void stabilizeOrdo(EntityLivingBase entity) {
        if (entity instanceof EntityLiving && entity.collidedHorizontally && entity.ticksExisted % 40 == 0) {
            ((EntityLiving) entity).getNavigator().clearPath();
        }
    }

    private static PrimalAspect getAspect(EntityLivingBase entity) {
        NBTTagCompound root = entity.getEntityData().getCompoundTag(NBT_ROOT);

        if (!root.hasKey(NBT_ASPECT, 8)) {
            return PrimalAspect.ORDO;
        }

        return PrimalAspect.fromString(root.getString(NBT_ASPECT));
    }

    private static boolean isAspectExpired(EntityLivingBase entity) {
        NBTTagCompound root = entity.getEntityData().getCompoundTag(NBT_ROOT);

        if (!root.hasKey(NBT_ASPECT_UNTIL, 3)) {
            return true;
        }

        return entity.ticksExisted >= root.getInteger(NBT_ASPECT_UNTIL);
    }

    private static void setAspect(EntityLivingBase entity, PrimalAspect aspect, int durationTicks) {
        NBTTagCompound root = entity.getEntityData().getCompoundTag(NBT_ROOT);

        root.setString(NBT_ASPECT, aspect.id);
        root.setInteger(NBT_ASPECT_UNTIL, entity.ticksExisted + durationTicks);

        entity.getEntityData().setTag(NBT_ROOT, root);
        applyAspectMarker(entity, aspect);
    }

    private static boolean hasPrimalMarker(EntityLivingBase entity) {
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

    private static PrimalAspect getClientAspect(EntityLivingBase entity) {
        if (entity == null) return null;
        IAttributeInstance attribute = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        if (attribute == null) return null;

        for (PrimalAspect aspect : PrimalAspect.values()) {
            UUID markerId = getAspectMarkerId(aspect);
            if (markerId != null && attribute.getModifier(markerId) != null) {
                return aspect;
            }
        }

        return null;
    }

    private static void applyAspectMarker(EntityLivingBase entity, PrimalAspect aspect) {
        if (entity == null || aspect == null) return;
        IAttributeInstance attribute = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        if (attribute == null) return;

        clearAspectMarkers(attribute);
        UUID markerId = getAspectMarkerId(aspect);
        if (markerId != null && attribute.getModifier(markerId) == null) {
            attribute.applyModifier(new AttributeModifier(markerId, "Thaumic Forever Primal " + aspect.id + " marker", ASPECT_MARKER_AMOUNT, 0).setSaved(false));
        }
    }

    private static void clearAspectMarkers(EntityLivingBase entity) {
        if (entity == null) return;
        clearAspectMarkers(entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH));
    }

    private static void clearAspectMarkers(IAttributeInstance attribute) {
        if (attribute == null) return;
        for (PrimalAspect aspect : PrimalAspect.values()) {
            UUID markerId = getAspectMarkerId(aspect);
            if (markerId != null && attribute.getModifier(markerId) != null) {
                attribute.removeModifier(markerId);
            }
        }
    }

    private static UUID getAspectMarkerId(PrimalAspect aspect) {
        switch (aspect) {
            case TERRA:
                return TERRA_MARKER_ID;
            case IGNIS:
                return IGNIS_MARKER_ID;
            case AQUA:
                return AQUA_MARKER_ID;
            case AER:
                return AER_MARKER_ID;
            case PERDITIO:
                return PERDITIO_MARKER_ID;
            case ORDO:
            default:
                return ORDO_MARKER_ID;
        }
    }

    private static int getAspectColor(PrimalAspect aspect) {
        Aspect thaumcraftAspect;
        switch (aspect) {
            case TERRA:
                thaumcraftAspect = Aspect.EARTH;
                break;
            case IGNIS:
                thaumcraftAspect = Aspect.FIRE;
                break;
            case AQUA:
                thaumcraftAspect = Aspect.WATER;
                break;
            case AER:
                thaumcraftAspect = Aspect.AIR;
                break;
            case PERDITIO:
                thaumcraftAspect = Aspect.ENTROPY;
                break;
            case ORDO:
            default:
                thaumcraftAspect = Aspect.ORDER;
                break;
        }

        return 0xFF000000 | (thaumcraftAspect.getColor() & 0xFFFFFF);
    }
}
