package com.koteuka404.thaumicforever;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class WatcherEntity extends EntityGuardian {

    private boolean spawnedFromMonument = false;

    public WatcherEntity(World worldIn) {
        super(worldIn);
        this.setSize(1.0F, 1.0F);
        this.experienceValue = 10;

        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.world.isRemote) {
            EntityLivingBase target = this.getAttackTarget();

            if (target != null && this.canSeeTarget(target)) {
                if (this.getTargetedEntity() == null) {
                    this.setAttackTarget(target); 
                }

                if (this.ticksExisted % 80 == 0) {
                    this.attackLaser(target);
                }
            }
        }
    }

    private void attackLaser(EntityLivingBase target) {
        target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this), 6.0F);
        target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 0));
        target.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 100, 0));
    }

    private boolean canSeeTarget(EntityLivingBase target) {
        return this.getEntitySenses().canSee(target) && this.getDistance(target) < 20.0D;
    }

    @Override
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        this.entityDropItem(new ItemStack(ModItems.orb_of_soul), 0.0F);
        super.dropLoot(wasRecentlyHit, lootingModifier, source);
    }

    @Override
    protected ResourceLocation getLootTable() {
        return null; 
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return super.getAmbientSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return super.getHurtSound(damageSourceIn);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return super.getDeathSound();
    }

    @Override
    public boolean isNonBoss() {
        return false;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("SpawnedFromMonument", this.spawnedFromMonument);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.spawnedFromMonument = compound.getBoolean("SpawnedFromMonument");
    }

    @Override
    public boolean getCanSpawnHere() {
        if (this.spawnedFromMonument) {
            return false; 
        }
        return super.getCanSpawnHere();
    }
}
