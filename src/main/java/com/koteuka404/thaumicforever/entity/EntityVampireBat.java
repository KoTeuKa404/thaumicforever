package com.koteuka404.thaumicforever.entity;

import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.entity.EnumCreatureAttribute;

public class EntityVampireBat extends EntityBat implements IMob {
    private static final String TAG_BLEED_TICKS = "tf_bleed_ticks";
    private static final String TAG_BLEED_LEVEL = "tf_bleed_level";
    private static final String TAG_BLEED_COUNTER = "tf_bleed_counter";

    private static final double AGGRO_RANGE = 12.0D;
    private static final double BITE_RANGE_SQ = 1.8D * 1.8D;
    private static final int BITE_COOLDOWN_TICKS = 30;
    private static final int BLEED_DURATION_TICKS = 80;

    private int biteCooldown;

    public EntityVampireBat(World worldIn) {
        super(worldIn);
        this.experienceValue = 3;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.world.isRemote) {
            return;
        }

        if (this.getIsBatHanging()) {
            this.setIsBatHanging(false);
        }

        if (this.biteCooldown > 0) {
            this.biteCooldown--;
        }

        EntityPlayer player = this.world.getClosestPlayerToEntity(this, AGGRO_RANGE);
        if (player == null || player.isCreative() || player.isSpectator() || player.isDead) {
            return;
        }

        double dx = player.posX - this.posX;
        double dy = player.posY + (double) player.getEyeHeight() * 0.5D - this.posY;
        double dz = player.posZ - this.posZ;
        double distSq = dx * dx + dy * dy + dz * dz;

        if (distSq > 0.0001D) {
            double dist = Math.sqrt(distSq);
            dx /= dist;
            dy /= dist;
            dz /= dist;

            this.motionX += dx * 0.12D;
            this.motionY += dy * 0.10D;
            this.motionZ += dz * 0.12D;

            this.motionX = clampMotion(this.motionX);
            this.motionY = clampMotion(this.motionY);
            this.motionZ = clampMotion(this.motionZ);

            this.faceEntity(player, 30.0F, 30.0F);
        }

        if (distSq <= BITE_RANGE_SQ && this.biteCooldown <= 0) {
            if (player.attackEntityFrom(DamageSource.causeMobDamage(this), 1.0F)) {
                applyBleedingLevelOne(player);
            }
            this.biteCooldown = BITE_COOLDOWN_TICKS;
        }
    }

    private static double clampMotion(double motion) {
        if (motion > 0.35D) return 0.35D;
        if (motion < -0.35D) return -0.35D;
        return motion;
    }

    private void applyBleedingLevelOne(EntityPlayer target) {
        int currentTicks = target.getEntityData().getInteger(TAG_BLEED_TICKS);
        target.getEntityData().setInteger(TAG_BLEED_TICKS, Math.max(currentTicks, BLEED_DURATION_TICKS));
        target.getEntityData().setInteger(TAG_BLEED_LEVEL, 1);
        target.getEntityData().setInteger(TAG_BLEED_COUNTER, 0);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }
}
