package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityGorilla extends EntityMob {

    private int  swingTicks = 0;
    private boolean swinging = false;

    private float  animationProgress = 0.0F;
    private boolean isSpecialAttacking = false;
    private boolean slamAppliedThisCycle = false;
    private int    specialAttackCooldown = 0;

    private static final float SPECIAL_ATTACK_STEP = 0.13f;
    private int specialAttacksDone = 0;

    private int  distractedTime = 0;
    private boolean isNeutral   = true;

    @SideOnly(Side.CLIENT) private boolean clientSlam = false;
    @SideOnly(Side.CLIENT) private float   clientSlamProgress = 0.0F;

    private static final byte STATUS_START_SLAM = 10;

    public EntityGorilla(World worldIn) {
        super(worldIn);
        this.setSize(1.5F, 2.5F);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(3, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(5, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(250.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (!heldItem.isEmpty() && heldItem.getItem() == ModItems.banana) {
            if (!player.isCreative()) heldItem.shrink(1);
            this.distractedTime = 60;
            this.world.playSound(null, this.posX, this.posY, this.posZ,
                    net.minecraft.init.SoundEvents.ENTITY_GENERIC_EAT,
                    SoundCategory.NEUTRAL, 1.0F, 1.0F);
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean result = super.attackEntityFrom(source, amount);
        if (!this.world.isRemote && source.getTrueSource() instanceof EntityLivingBase) {
            this.setRevengeTarget((EntityLivingBase) source.getTrueSource());
            isNeutral = false;
        }
        return result;
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean hit = super.attackEntityAsMob(target);
        this.swingArm(EnumHand.MAIN_HAND);
        if (hit && !this.swinging) { this.swinging = true; this.swingTicks = 0; }
        return hit;
    }

    @Override
    public float getSwingProgress(float partialTicks) {
        float sp = super.getSwingProgress(partialTicks);
        if (sp > 0.0F) return sp;

        if (swinging) {
            float alt = (this.swingTicks + partialTicks) / 10.0F;
            if (alt > 1.0F) alt = 1.0F;
            return alt;
        }

        if (this.world != null && this.world.isRemote && this.swingProgressInt > 0) {
            int end = getArmSwingAnimEndCompat();
            return Math.min(1.0F, (float)this.swingProgressInt / (float)end);
        }
        return 0.0F;
    }

    private int getArmSwingAnimEndCompat() {
        int end = 6;
        PotionEffect haste = this.getActivePotionEffect(MobEffects.HASTE);
        if (haste != null) {
            end = 6 - (1 + haste.getAmplifier());
            if (end < 1) end = 1;
        }
        PotionEffect fatigue = this.getActivePotionEffect(MobEffects.MINING_FATIGUE);
        if (fatigue != null) {
            end = 6 + (1 + fatigue.getAmplifier()) * 2;
        }
        return end;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (swinging) {
            swingTicks++;
            if (swingTicks >= 10) { swinging = false; swingTicks = 0; }
        }

        if (this.world.isRemote) {
            if (clientSlam) {
                clientSlamProgress -= 0.05F;
                if (clientSlamProgress < 0.0F) clientSlamProgress = 0.0F;

                float progress = 1.0F - clientSlamProgress;
                if (this.ticksExisted % 2 == 0) {
                    int dust = (progress < 0.4F) ? 4 : 8;
                    spawnFootstepDust(dust, 0.9D, 0.08D);
                }
                if (progress > 0.2F) {
                    spawnSweepTrails(2, 0.9D);
                }

                if (clientSlamProgress <= 0.0F) {
                    clientSlam = false;
                }
            }
        }

        if (distractedTime > 0) {
            distractedTime--;
            return;
        }

        if (this.isNeutral && this.ticksExisted % 100 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(5.0F);
        }

        tryStartSpecialAttackByHealthSteps();

        if (!this.world.isRemote && isSpecialAttacking) {
            handleSpecialAttackAnimationServer();
        }

        if (specialAttackCooldown > 0) specialAttackCooldown--;
    }

    private void tryStartSpecialAttackByHealthSteps() {
        if (this.world.isRemote) return;
        if (isSpecialAttacking || specialAttackCooldown > 0) return;

        float max = (float) this.getMaxHealth();
        if (max <= 0f) return;

        float lostFrac = 1.0f - (this.getHealth() / max);
        int expectedSteps = (int) Math.floor(lostFrac / SPECIAL_ATTACK_STEP);

        if (expectedSteps > specialAttacksDone && hasValidSlamTarget()) {
            specialAttacksDone = expectedSteps;
            startSpecialAttackServer();
            specialAttackCooldown = 10;
        }
    }

    private void startSpecialAttackServer() {
        isSpecialAttacking = true;
        slamAppliedThisCycle = false;
        animationProgress = 1.0F;

        this.world.playSound(null, this.posX, this.posY, this.posZ,
                net.minecraft.init.SoundEvents.ENTITY_IRONGOLEM_ATTACK,
                SoundCategory.HOSTILE, 0.9F, 0.6F);

        this.world.setEntityState(this, STATUS_START_SLAM);
    }

    private boolean hasValidSlamTarget() {
        EntityLivingBase tgt = this.getAttackTarget();
        if (tgt == null || tgt.isDead) return false;
        double maxDistSq = 12.0D * 12.0D;
        if (this.getDistanceSq(tgt) > maxDistSq) return false;
        return this.canEntityBeSeen(tgt);
    }

    public float getAttackSwingProgress(float partialTicks) {
        if (!swinging) return 0.0F;
        float progress = (swingTicks + partialTicks) / 10.0F;
        return progress > 1.0F ? 1.0F : progress;
    }

    public boolean isSwinging() { return swinging; }

    private void handleSpecialAttackAnimationServer() {
        float progress = 1.0F - animationProgress;

        if (progress > 0.4F) {
            float slamProgress = (progress - 0.4F) / 0.6F;
            if (!slamAppliedThisCycle && slamProgress > 0.8F) {
                performSlamAttack();
                slamAppliedThisCycle = true;
            }
        }

        animationProgress -= 0.05F;
        if (animationProgress <= 0.0F) {
            isSpecialAttacking = false;
            animationProgress = 0.0F;
            specialAttackCooldown = 40;
        }
    }

    private void performSlamAttack() {
        this.world.playSound(null, this.posX, this.posY, this.posZ,
                net.minecraft.init.SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.HOSTILE, 1.0F, 1.0F);

        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                new AxisAlignedBB(this.posX - 2, this.posY - 1, this.posZ - 2,
                                    this.posX + 2, this.posY + 1, this.posZ + 2));

        for (EntityLivingBase e : entities) {
            if (e != this) {
                e.attackEntityFrom(DamageSource.causeMobDamage(this), 20.0F);
                e.knockBack(this, 1.0F, this.posX - e.posX, this.posZ - e.posZ);
            }
        }

        for (int i = 0; i < 20; i++) {
            double dx = (this.rand.nextDouble() - 0.5) * 2.0;
            double dy = this.rand.nextDouble();
            double dz = (this.rand.nextDouble() - 0.5) * 2.0;
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,
                    this.posX + dx, this.posY + dy, this.posZ + dz, 0.0, 0.0, 0.0);
        }

        if (this.world.isRemote) {
            spawnFootstepDust(30, 1.6D, 0.15D);
            spawnSweepTrails(6, 1.2D);
        }
    }

    public boolean isSpecialAttacking() {
        if (this.world != null && this.world.isRemote) return clientSlam;
        return this.isSpecialAttacking;
    }

    public float getSlamProgress() {
        if (this.world != null && this.world.isRemote) return 1.0F - this.clientSlamProgress;
        return 1.0F - this.animationProgress;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        super.handleStatusUpdate(id);
        if (id == STATUS_START_SLAM) {
            this.clientSlam = true;
            this.clientSlamProgress = 1.0F;
        }
    }

    private void spawnFootstepDust(int count, double spreadXZ, double upVel) {
        if (!this.world.isRemote) return;

        BlockPos under = new BlockPos(this.posX, this.getEntityBoundingBox().minY - 0.2D, this.posZ);
        IBlockState st = this.world.getBlockState(under);
        if (st.getMaterial().isLiquid() || st.getMaterial().isReplaceable()) return;

        int id = Block.getStateId(st);
        for (int i = 0; i < count; i++) {
            double ox = (this.rand.nextDouble() - 0.5D) * spreadXZ;
            double oz = (this.rand.nextDouble() - 0.5D) * spreadXZ;
            double vx = (this.rand.nextDouble() - 0.5D) * 0.02D;
            double vz = (this.rand.nextDouble() - 0.5D) * 0.02D;
            this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST,
                    this.posX + ox,
                    this.getEntityBoundingBox().minY + 0.05D,
                    this.posZ + oz,
                    vx, upVel, vz,
                    id);
        }
    }

    private void spawnSweepTrails(int count, double radius) {
        if (!this.world.isRemote) return;

        for (int i = 0; i < count; i++) {
            double ang = this.rand.nextDouble() * Math.PI * 2.0;
            double rx = this.posX + Math.cos(ang) * radius;
            double rz = this.posZ + Math.sin(ang) * radius;
            double ry = this.posY + this.height * 0.9D;
            this.world.spawnParticle(EnumParticleTypes.SWEEP_ATTACK, rx, ry, rz, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override protected boolean canDespawn() { return false; }
    @Override public boolean isNoDespawnRequired() { return true; }
    @Override
    public boolean getCanSpawnHere() {
        return this.world.getBlockState(this.getPosition().down()).getMaterial().isSolid()
                && this.world.canSeeSky(this.getPosition());
    }
}
