package com.koteuka404.thaumicforever.entity;

import com.koteuka404.thaumicforever.potion.PotionResonanceDisruption;
import com.koteuka404.thaumicforever.potion.ResonanceDisruptionHandler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityResonanceBolt extends EntityThrowable {

    private static final float IMPACT_DAMAGE = 1.0F;
    private static final int DISRUPTION_TICKS = ResonanceDisruptionHandler.MARK_DURATION_TICKS;

    public EntityResonanceBolt(World worldIn) {
        super(worldIn);
        setSize(0.25F, 0.25F);
    }

    public EntityResonanceBolt(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        setSize(0.25F, 0.25F);
    }

    public EntityResonanceBolt(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        setSize(0.25F, 0.25F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            for (int i = 0; i < 2; i++) {
                world.spawnParticle(EnumParticleTypes.SPELL_WITCH,
                        posX - motionX * 0.2D, posY + 0.05D - motionY * 0.2D, posZ - motionZ * 0.2D,
                        0.02D * (rand.nextDouble() - 0.5D), 0.02D * (rand.nextDouble() - 0.5D), 0.02D * (rand.nextDouble() - 0.5D));
            }
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.005F;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            if (result.entityHit instanceof EntityLivingBase) {
                EntityLivingBase target = (EntityLivingBase) result.entityHit;
                target.addPotionEffect(new PotionEffect(PotionResonanceDisruption.INSTANCE, DISRUPTION_TICKS, 0, false, true));
                ResonanceDisruptionHandler.applyInitialResonanceHit(target);
                target.attackEntityFrom(new EntityDamageSourceIndirect("thaumicforever.resonance", this, getThrower()).setMagicDamage(), IMPACT_DAMAGE);
            }

            if (world instanceof WorldServer) {
                ((WorldServer) world).spawnParticle(EnumParticleTypes.SPELL_WITCH, posX, posY, posZ,
                        18, 0.25D, 0.25D, 0.25D, 0.02D);
                ((WorldServer) world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, posX, posY, posZ,
                        10, 0.15D, 0.15D, 0.15D, 0.04D);
            }

            world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_NOTE_CHIME, SoundCategory.PLAYERS, 0.75F, 0.65F);
            setDead();
        }
    }
}
