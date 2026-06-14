package com.koteuka404.thaumicforever.potion;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ResonanceDisruptionHandler {

    public static final int MARK_DURATION_TICKS = 20 * 6;
    public static final int SHIELD_COLLAPSE_TICKS = 20 * 2;

    private static final String NBT_CHARGE = "TF_ResonanceCharge";
    private static final String NBT_COLLAPSE = "TF_ShieldCollapseTicks";
    private static final String NBT_PENDING_WOUND = "TF_PendingResonanceWound";
    private static final String NBT_LAST_HURT = "TF_LastResonanceHurt";
    private static final String NBT_MOVE_COOLDOWN = "TF_ResonanceMoveCooldown";
    private static final String NBT_HAS_LAST_POS = "TF_ResonanceHasLastPos";
    private static final String NBT_LAST_X = "TF_ResonanceLastX";
    private static final String NBT_LAST_Y = "TF_ResonanceLastY";
    private static final String NBT_LAST_Z = "TF_ResonanceLastZ";
    private static final String NBT_ENERGY_ARMOR_PREFIX = "TF_ResonanceArmorEnergy";
    private static final String NBT_ENERGY_BAUBLE_PREFIX = "TF_ResonanceBaubleEnergy";

    private static final int OVERLOAD_THRESHOLD = 3;

    private static final int INITIAL_DRAIN_PERCENT = 10;
    private static final int INITIAL_DRAIN_MIN = 50_000;
    private static final int INITIAL_DRAIN_MAX = 500_000;

    private static final int MARK_TICK_DRAIN_PERCENT = 2;
    private static final int MARK_TICK_DRAIN_MIN = 25_000;
    private static final int MARK_TICK_DRAIN_MAX = 250_000;

    private static final int BLOCKED_HIT_DRAIN_PERCENT = 6;
    private static final int BLOCKED_HIT_DRAIN_MIN = 50_000;
    private static final int BLOCKED_HIT_DRAIN_MAX = 500_000;

    private static final int COLLAPSE_DRAIN_PERCENT = 15;
    private static final int COLLAPSE_DRAIN_MIN = 100_000;
    private static final int COLLAPSE_DRAIN_MAX = 1_500_000;

    private static final float INITIAL_ABSORPTION_REMOVE = 8.0F;
    private static final float BLOCKED_HIT_ABSORPTION_REMOVE = 4.0F;
    private static final float MAX_ABSORPTION_WHILE_MARKED = 4.0F;
    private static final float MAX_DIRECT_WOUND_PER_TICK = 4.0F;

    private static final boolean DEBUG_RESONANCE_PARTICLES = true;

    public static void applyInitialResonanceHit(EntityLivingBase target) {
        if (!isValidServerTarget(target)) {
            return;
        }

        addCharge(target, 1);
        suppressAbsorption(target, INITIAL_ABSORPTION_REMOVE);
        drainProtectedEquipmentEnergy(target, INITIAL_DRAIN_PERCENT, INITIAL_DRAIN_MIN, INITIAL_DRAIN_MAX);
        snapshotProtectedEquipmentEnergy(target);
        debugBurst(target, EnumParticleTypes.SPELL_WITCH, 24, 0.45D, 0.05D);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onLivingAttack(LivingAttackEvent event) {
        if (event == null) {
            return;
        }

        EntityLivingBase target = event.getEntityLiving();
        if (!hasActiveResonanceMark(target) || isResonanceDamage(event.getSource())) {
            return;
        }

        if (event.isCanceled()) {
            boolean collapsed = isShieldCollapsed(target);

            addCharge(target, collapsed ? 2 : 1);
            suppressAbsorption(target, collapsed ? INITIAL_ABSORPTION_REMOVE : BLOCKED_HIT_ABSORPTION_REMOVE);
            drainProtectedEquipmentEnergy(target,
                    collapsed ? COLLAPSE_DRAIN_PERCENT : BLOCKED_HIT_DRAIN_PERCENT,
                    collapsed ? COLLAPSE_DRAIN_MIN : BLOCKED_HIT_DRAIN_MIN,
                    collapsed ? COLLAPSE_DRAIN_MAX : BLOCKED_HIT_DRAIN_MAX);

            if (collapsed) {
                queueDirectResonanceWound(target, Math.min(event.getAmount(), MAX_DIRECT_WOUND_PER_TICK));
            }

            debugBurst(target, collapsed ? EnumParticleTypes.DRAGON_BREATH : EnumParticleTypes.CRIT_MAGIC,
                    collapsed ? 18 : 8, 0.35D, 0.04D);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingHurt(LivingHurtEvent event) {
        if (event == null || event.isCanceled()) {
            return;
        }

        EntityLivingBase target = event.getEntityLiving();
        if (!hasActiveResonanceMark(target) || isResonanceDamage(event.getSource())) {
            return;
        }

        target.getEntityData().setFloat(NBT_LAST_HURT, event.getAmount());

        if (isShieldCollapsed(target)) {
            suppressAbsorption(target, INITIAL_ABSORPTION_REMOVE);
            drainProtectedEquipmentEnergy(target, COLLAPSE_DRAIN_PERCENT, COLLAPSE_DRAIN_MIN, COLLAPSE_DRAIN_MAX);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onLivingDamage(LivingDamageEvent event) {
        if (event == null || event.isCanceled()) {
            return;
        }

        EntityLivingBase target = event.getEntityLiving();
        if (!hasActiveResonanceMark(target) || isResonanceDamage(event.getSource())) {
            return;
        }

        NBTTagCompound data = target.getEntityData();
        float beforeFinalProtection = data.getFloat(NBT_LAST_HURT);
        data.removeTag(NBT_LAST_HURT);

        if (beforeFinalProtection >= 4.0F && event.getAmount() <= Math.max(0.75F, beforeFinalProtection * 0.15F)) {
            addCharge(target, 1);
            drainProtectedEquipmentEnergy(target, BLOCKED_HIT_DRAIN_PERCENT, BLOCKED_HIT_DRAIN_MIN, BLOCKED_HIT_DRAIN_MAX);
            debugBurst(target, EnumParticleTypes.CRIT_MAGIC, 10, 0.30D, 0.03D);
        }

        if (isShieldCollapsed(target)) {
            target.hurtResistantTime = Math.min(target.hurtResistantTime, 4);
            suppressAbsorption(target, INITIAL_ABSORPTION_REMOVE);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event == null) {
            return;
        }

        EntityLivingBase target = event.getEntityLiving();
        if (!isValidServerTarget(target)) {
            return;
        }

        if (!hasActiveResonanceMark(target)) {
            clearResonanceRuntimeData(target);
            return;
        }

        NBTTagCompound data = target.getEntityData();
        int collapseTicks = data.getInteger(NBT_COLLAPSE);
        boolean collapsed = collapseTicks > 0;

        blockProtectedEquipmentRecharge(target);
        applyQueuedDirectWound(target);

        if (collapsed) {
            data.setInteger(NBT_COLLAPSE, collapseTicks - 1);
            target.setAbsorptionAmount(0.0F);
            target.hurtResistantTime = Math.min(target.hurtResistantTime, 4);

            if (target.ticksExisted % 10 == 0) {
                drainProtectedEquipmentEnergy(target, COLLAPSE_DRAIN_PERCENT, COLLAPSE_DRAIN_MIN, COLLAPSE_DRAIN_MAX);
            }

            if (DEBUG_RESONANCE_PARTICLES && target.ticksExisted % 4 == 0) {
                debugBurst(target, EnumParticleTypes.DRAGON_BREATH, 3, 0.32D, 0.01D);
            }
        } else {
            if (target.getAbsorptionAmount() > MAX_ABSORPTION_WHILE_MARKED) {
                target.setAbsorptionAmount(MAX_ABSORPTION_WHILE_MARKED);
            }

            if (target.ticksExisted % 20 == 0) {
                drainProtectedEquipmentEnergy(target, MARK_TICK_DRAIN_PERCENT, MARK_TICK_DRAIN_MIN, MARK_TICK_DRAIN_MAX);
            }

            handleMovementBuildup(target);

            if (DEBUG_RESONANCE_PARTICLES && target.ticksExisted % 10 == 0) {
                debugRingPoint(target, EnumParticleTypes.SPELL_WITCH);
            }
        }
    }

    private static boolean hasActiveResonanceMark(EntityLivingBase target) {
        return isValidServerTarget(target) && target.getActivePotionEffect(PotionResonanceDisruption.INSTANCE) != null;
    }

    private static boolean isValidServerTarget(EntityLivingBase target) {
        return target != null && target.world != null && !target.world.isRemote;
    }

    private static boolean isResonanceDamage(DamageSource source) {
        return source != null && "thaumicforever.resonance".equals(source.getDamageType());
    }

    private static boolean isShieldCollapsed(EntityLivingBase target) {
        return target.getEntityData().getInteger(NBT_COLLAPSE) > 0;
    }

    private static void addCharge(EntityLivingBase target, int amount) {
        if (!isValidServerTarget(target) || amount <= 0) {
            return;
        }

        NBTTagCompound data = target.getEntityData();
        int charge = Math.max(0, data.getInteger(NBT_CHARGE)) + amount;
        data.setInteger(NBT_CHARGE, charge);

        if (DEBUG_RESONANCE_PARTICLES) {
            debugChargePoint(target, charge);
        }

        if (charge >= OVERLOAD_THRESHOLD && data.getInteger(NBT_COLLAPSE) <= 0) {
            triggerShieldCollapse(target);
        }
    }

    private static void triggerShieldCollapse(EntityLivingBase target) {
        NBTTagCompound data = target.getEntityData();
        data.setInteger(NBT_CHARGE, 0);
        data.setInteger(NBT_COLLAPSE, SHIELD_COLLAPSE_TICKS);

        target.setAbsorptionAmount(0.0F);
        drainProtectedEquipmentEnergy(target, COLLAPSE_DRAIN_PERCENT, COLLAPSE_DRAIN_MIN, COLLAPSE_DRAIN_MAX);

        target.world.playSound(null, target.posX, target.posY, target.posZ,
                SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 0.9F, 0.65F);
        target.world.playSound(null, target.posX, target.posY, target.posZ,
                SoundEvents.BLOCK_NOTE_PLING, SoundCategory.PLAYERS, 0.7F, 0.35F);

        debugBurst(target, EnumParticleTypes.DRAGON_BREATH, 40, 0.55D, 0.08D);
        debugBurst(target, EnumParticleTypes.CRIT_MAGIC, 28, 0.45D, 0.05D);
    }

    private static void handleMovementBuildup(EntityLivingBase target) {
        NBTTagCompound data = target.getEntityData();

        if (data.getInteger(NBT_MOVE_COOLDOWN) > 0) {
            data.setInteger(NBT_MOVE_COOLDOWN, data.getInteger(NBT_MOVE_COOLDOWN) - 1);
        }

        if (!data.getBoolean(NBT_HAS_LAST_POS)) {
            storeLastPosition(target, data);
            return;
        }

        double dx = target.posX - data.getDouble(NBT_LAST_X);
        double dy = target.posY - data.getDouble(NBT_LAST_Y);
        double dz = target.posZ - data.getDouble(NBT_LAST_Z);
        double horizontalSpeedSq = dx * dx + dz * dz;
        double totalSpeedSq = horizontalSpeedSq + dy * dy;

        storeLastPosition(target, data);

        if (data.getInteger(NBT_MOVE_COOLDOWN) > 0) {
            return;
        }

        boolean teleportLikeMovement = totalSpeedSq > 4.0D;
        boolean fastMovement = horizontalSpeedSq > 0.075D;
        boolean sprintMovement = target.isSprinting() && horizontalSpeedSq > 0.020D;
        boolean flying = target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.isFlying;
        boolean unstableAirMovement = !target.onGround && Math.abs(dy) > 0.30D;

        if (teleportLikeMovement) {
            addCharge(target, 2);
            drainProtectedEquipmentEnergy(target, 8, 75_000, 750_000);
            data.setInteger(NBT_MOVE_COOLDOWN, 20);
        } else if (flying || fastMovement || unstableAirMovement) {
            addCharge(target, 1);
            drainProtectedEquipmentEnergy(target, flying ? 6 : 4, 50_000, 500_000);
            data.setInteger(NBT_MOVE_COOLDOWN, flying ? 20 : 30);
        } else if (sprintMovement) {
            addCharge(target, 1);
            data.setInteger(NBT_MOVE_COOLDOWN, 40);
        }
    }

    private static void storeLastPosition(EntityLivingBase target, NBTTagCompound data) {
        data.setBoolean(NBT_HAS_LAST_POS, true);
        data.setDouble(NBT_LAST_X, target.posX);
        data.setDouble(NBT_LAST_Y, target.posY);
        data.setDouble(NBT_LAST_Z, target.posZ);
    }

    private static void suppressAbsorption(EntityLivingBase target, float amount) {
        if (target == null || amount <= 0.0F) {
            return;
        }

        float absorption = target.getAbsorptionAmount();
        if (absorption > 0.0F) {
            target.setAbsorptionAmount(Math.max(0.0F, absorption - amount));
        }
    }

    private static void queueDirectResonanceWound(EntityLivingBase target, float amount) {
        if (!isValidServerTarget(target) || amount <= 0.0F) {
            return;
        }

        NBTTagCompound data = target.getEntityData();
        float pending = data.getFloat(NBT_PENDING_WOUND);
        data.setFloat(NBT_PENDING_WOUND, Math.min(MAX_DIRECT_WOUND_PER_TICK, pending + amount));
    }

    private static void applyQueuedDirectWound(EntityLivingBase target) {
        NBTTagCompound data = target.getEntityData();
        float pending = data.getFloat(NBT_PENDING_WOUND);
        if (pending <= 0.0F) {
            return;
        }

        data.removeTag(NBT_PENDING_WOUND);

        if (target.getHealth() > 1.0F) {
            float wound = Math.min(MAX_DIRECT_WOUND_PER_TICK, pending);
            target.setHealth(Math.max(1.0F, target.getHealth() - wound));
            debugBurst(target, EnumParticleTypes.DAMAGE_INDICATOR, 6, 0.20D, 0.02D);
        }
    }

    private static void clearResonanceRuntimeData(EntityLivingBase target) {
        NBTTagCompound data = target.getEntityData();
        data.removeTag(NBT_CHARGE);
        data.removeTag(NBT_COLLAPSE);
        data.removeTag(NBT_PENDING_WOUND);
        data.removeTag(NBT_LAST_HURT);
        data.removeTag(NBT_MOVE_COOLDOWN);
        data.removeTag(NBT_HAS_LAST_POS);
        data.removeTag(NBT_LAST_X);
        data.removeTag(NBT_LAST_Y);
        data.removeTag(NBT_LAST_Z);
        clearTrackedEquipmentEnergy(data);
    }

    private static void drainProtectedEquipmentEnergy(EntityLivingBase target, int percent, int minDrain, int maxDrain) {
        if (!isValidServerTarget(target)) {
            return;
        }

        for (ItemStack armor : target.getArmorInventoryList()) {
            drainEnergyFromStack(armor, percent, minDrain, maxDrain);
        }

        if (target instanceof EntityPlayer) {
            drainBaublesEnergy((EntityPlayer) target, percent, minDrain, maxDrain);
        }
    }

    private static void snapshotProtectedEquipmentEnergy(EntityLivingBase target) {
        if (!isValidServerTarget(target)) {
            return;
        }

        NBTTagCompound data = target.getEntityData();
        int slot = 0;
        for (ItemStack armor : target.getArmorInventoryList()) {
            snapshotEnergy(data, NBT_ENERGY_ARMOR_PREFIX + slot, armor);
            slot++;
        }

        if (target instanceof EntityPlayer) {
            try {
                IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer) target);
                if (baubles != null) {
                    for (int i = 0; i < baubles.getSlots(); i++) {
                        snapshotEnergy(data, NBT_ENERGY_BAUBLE_PREFIX + i, baubles.getStackInSlot(i));
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private static void blockProtectedEquipmentRecharge(EntityLivingBase target) {
        if (!isValidServerTarget(target)) {
            return;
        }

        NBTTagCompound data = target.getEntityData();
        int slot = 0;
        for (ItemStack armor : target.getArmorInventoryList()) {
            blockRechargeInStack(data, NBT_ENERGY_ARMOR_PREFIX + slot, armor);
            slot++;
        }

        if (target instanceof EntityPlayer) {
            try {
                IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer) target);
                if (baubles != null) {
                    for (int i = 0; i < baubles.getSlots(); i++) {
                        blockRechargeInStack(data, NBT_ENERGY_BAUBLE_PREFIX + i, baubles.getStackInSlot(i));
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private static void snapshotEnergy(NBTTagCompound data, String key, ItemStack stack) {
        IEnergyStorage energy = getEnergy(stack);
        if (energy == null) {
            data.removeTag(key);
            return;
        }
        data.setInteger(key, energy.getEnergyStored());
    }

    private static void blockRechargeInStack(NBTTagCompound data, String key, ItemStack stack) {
        IEnergyStorage energy = getEnergy(stack);
        if (energy == null) {
            data.removeTag(key);
            return;
        }

        int current = energy.getEnergyStored();
        if (data.hasKey(key)) {
            int allowed = Math.max(0, data.getInteger(key));
            if (current > allowed) {
                energy.extractEnergy(current - allowed, false);
                current = energy.getEnergyStored();
            }
        }

        data.setInteger(key, current);
    }

    private static void clearTrackedEquipmentEnergy(NBTTagCompound data) {
        for (int i = 0; i < 8; i++) {
            data.removeTag(NBT_ENERGY_ARMOR_PREFIX + i);
        }
        for (int i = 0; i < 64; i++) {
            data.removeTag(NBT_ENERGY_BAUBLE_PREFIX + i);
        }
    }

    private static void drainBaublesEnergy(EntityPlayer player, int percent, int minDrain, int maxDrain) {
        try {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            if (baubles == null) {
                return;
            }

            for (int slot = 0; slot < baubles.getSlots(); slot++) {
                drainEnergyFromStack(baubles.getStackInSlot(slot), percent, minDrain, maxDrain);
            }
        } catch (Throwable ignored) {
            // Keeps the resonance handler safe if a Baubles fork exposes an unusual handler implementation.
        }
    }

    private static int drainEnergyFromStack(ItemStack stack, int percent, int minDrain, int maxDrain) {
        IEnergyStorage energy = getEnergy(stack);
        if (energy == null || energy.getEnergyStored() <= 0) {
            return 0;
        }

        int stored = energy.getEnergyStored();
        int percentDrain = Math.max(1, stored * Math.max(1, percent) / 100);
        int requested = MathHelper.clamp(Math.max(minDrain, percentDrain), 1, Math.max(1, maxDrain));
        requested = Math.min(requested, stored);

        return energy.extractEnergy(requested, false);
    }

    private static IEnergyStorage getEnergy(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return null;
        }
        return stack.getCapability(CapabilityEnergy.ENERGY, null);
    }

    private static void debugChargePoint(EntityLivingBase target, int charge) {
        if (!DEBUG_RESONANCE_PARTICLES || !(target.world instanceof WorldServer)) {
            return;
        }

        double angle = (charge % OVERLOAD_THRESHOLD) * (Math.PI * 2.0D / OVERLOAD_THRESHOLD);
        double x = target.posX + Math.cos(angle) * 0.75D;
        double z = target.posZ + Math.sin(angle) * 0.75D;
        double y = target.posY + target.height + 0.20D;

        ((WorldServer) target.world).spawnParticle(EnumParticleTypes.REDSTONE, x, y, z,
                8, 0.02D, 0.02D, 0.02D, 0.0D);
    }

    private static void debugRingPoint(EntityLivingBase target, EnumParticleTypes particle) {
        if (!DEBUG_RESONANCE_PARTICLES || !(target.world instanceof WorldServer)) {
            return;
        }

        double angle = (target.ticksExisted % 40) / 40.0D * Math.PI * 2.0D;
        double x = target.posX + Math.cos(angle) * 0.65D;
        double z = target.posZ + Math.sin(angle) * 0.65D;
        double y = target.posY + target.height * 0.55D;

        ((WorldServer) target.world).spawnParticle(particle, x, y, z,
                2, 0.015D, 0.015D, 0.015D, 0.0D);
    }

    private static void debugBurst(EntityLivingBase target, EnumParticleTypes particle, int count, double spread, double speed) {
        if (!DEBUG_RESONANCE_PARTICLES || !(target.world instanceof WorldServer)) {
            return;
        }

        ((WorldServer) target.world).spawnParticle(particle,
                target.posX, target.posY + target.height * 0.55D, target.posZ,
                count, spread, spread, spread, speed);
    }
}
