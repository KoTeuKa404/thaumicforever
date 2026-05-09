package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.recipe.EnumInfusionEnchantment;
import com.koteuka404.thaumicforever.ThaumicForever;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.koteuka404.thaumicforever.network.PacketBleedingFX;
import com.koteuka404.thaumicforever.potion.PotionVampirism;

public class BleedingEnchantmentHandler {
    private static final String TAG_BLEED_TICKS = "tf_bleed_ticks";
    private static final String TAG_BLEED_LEVEL = "tf_bleed_level";
    private static final String TAG_BLEED_COUNTER = "tf_bleed_counter";
    private static final String TAG_BLEED_OWNER_UUID_MOST = "tf_bleed_owner_most";
    private static final String TAG_BLEED_OWNER_UUID_LEAST = "tf_bleed_owner_least";
    // Balance: keep bleed as pressure tool, not a primary kill source.
    private static final int BLEED_TICK_INTERVAL = 40; // 2 seconds

    @SubscribeEvent
    public void onAttackEntity(LivingHurtEvent event) {
        Entity source = event.getSource().getTrueSource();
        if (source == null) {
            source = event.getSource().getImmediateSource();
        }
        if (!(source instanceof EntityLivingBase)) {
            return;
        }

        EntityLivingBase attacker = (EntityLivingBase) source;
        if (!EquipmentEffectHelper.supportsEquipmentEffects(attacker)) {
            return;
        }

        ItemStack heldItem = attacker.getHeldItemMainhand();
        int bleedingLevel = EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldItem, EnumInfusionEnchantment.BLEEDING);

        if (bleedingLevel <= 0 || !(event.getEntityLiving() instanceof EntityLivingBase)) {
            return;
        }

        EntityLivingBase target = (EntityLivingBase) event.getEntityLiving();
        int baseDuration = 80 + bleedingLevel * 40;
        int durationTicks = isCriticalHit(attacker) ? baseDuration + 40 : baseDuration;
        int currentTicks = target.getEntityData().getInteger(TAG_BLEED_TICKS);
        int currentLevel = target.getEntityData().getInteger(TAG_BLEED_LEVEL);
        target.getEntityData().setInteger(TAG_BLEED_TICKS, Math.max(currentTicks, durationTicks));
        target.getEntityData().setInteger(TAG_BLEED_LEVEL, Math.max(currentLevel, bleedingLevel));
        target.getEntityData().removeTag(TAG_BLEED_OWNER_UUID_MOST);
        target.getEntityData().removeTag(TAG_BLEED_OWNER_UUID_LEAST);
        if (attacker instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacker;
            target.getEntityData().setLong(TAG_BLEED_OWNER_UUID_MOST, player.getUniqueID().getMostSignificantBits());
            target.getEntityData().setLong(TAG_BLEED_OWNER_UUID_LEAST, player.getUniqueID().getLeastSignificantBits());
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase target = (EntityLivingBase) event.getEntityLiving();
        if (target.world.isRemote || target.isDead) {
            return;
        }

        int ticks = target.getEntityData().getInteger(TAG_BLEED_TICKS);
        if (ticks <= 0) {
            return;
        }

        int level = Math.max(1, target.getEntityData().getInteger(TAG_BLEED_LEVEL));
        int counter = target.getEntityData().getInteger(TAG_BLEED_COUNTER) + 1;
        ticks--;

        target.getEntityData().setInteger(TAG_BLEED_TICKS, ticks);
        target.getEntityData().setInteger(TAG_BLEED_COUNTER, counter);

        if (counter >= BLEED_TICK_INTERVAL) {
            target.getEntityData().setInteger(TAG_BLEED_COUNTER, 0);
            float bleedDamage = 0.5F + (level - 1) * 0.25F;
            target.attackEntityFrom(DamageSource.MAGIC, bleedDamage);
            healBleedOwnerIfVampirism(target);

            ThaumicForever.network.sendToAllAround(
                new PacketBleedingFX(target.getEntityId(), level),
                new NetworkRegistry.TargetPoint(
                    target.world.provider.getDimension(),
                    target.posX,
                    target.posY,
                    target.posZ,
                    48.0D
                )
            );
        }

        if (ticks <= 0) {
            target.getEntityData().removeTag(TAG_BLEED_TICKS);
            target.getEntityData().removeTag(TAG_BLEED_LEVEL);
            target.getEntityData().removeTag(TAG_BLEED_COUNTER);
            target.getEntityData().removeTag(TAG_BLEED_OWNER_UUID_MOST);
            target.getEntityData().removeTag(TAG_BLEED_OWNER_UUID_LEAST);
        }
    }

    private void healBleedOwnerIfVampirism(EntityLivingBase target) {
        if (!target.getEntityData().hasKey(TAG_BLEED_OWNER_UUID_MOST) || !target.getEntityData().hasKey(TAG_BLEED_OWNER_UUID_LEAST)) {
            return;
        }
        long most = target.getEntityData().getLong(TAG_BLEED_OWNER_UUID_MOST);
        long least = target.getEntityData().getLong(TAG_BLEED_OWNER_UUID_LEAST);
        EntityPlayer owner = target.world.getPlayerEntityByUUID(new java.util.UUID(most, least));
        if (owner == null || owner.isDead || !owner.isPotionActive(PotionVampirism.INSTANCE)) {
            return;
        }
        // 1 HP (half-heart) lifesteal per successful bleed tick.
        owner.heal(1.0F);
    }

    private boolean isCriticalHit(EntityLivingBase attacker) {
        if (!(attacker instanceof EntityPlayer)) {
            return false;
        }
        EntityPlayer player = (EntityPlayer) attacker;
        return player.fallDistance > 0.0F
            && !player.onGround
            && !player.isOnLadder()
            && !player.isInWater()
            && !player.isPotionActive(MobEffects.BLINDNESS)
            && player.getRidingEntity() == null;
    }

}
