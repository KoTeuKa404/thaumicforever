package com.koteuka404.thaumicforever;

import java.util.List;

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
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityGorilla extends EntityMob {
    private float rightArmAngle = 0.0F;
    private float leftArmAngle = 0.0F;
    private float animationProgress = 0.0F;
    private boolean isSpecialAttacking = false;
    private int specialAttackCooldown = 0;
    private int distractedTime = 0;
    private boolean isNeutral = true; 
    private float rightArmAngleX = 0.0F;
    private float leftArmAngleX = 0.0F;

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

        if (heldItem.getItem() == ModItems.banana) {
            if (!player.isCreative()) {
                heldItem.shrink(1);
            }
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
            System.out.println("Gorilla attacked by: " + source.getTrueSource().getName());
        }

        return result;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (distractedTime > 0) {
            distractedTime--;
            return;
        }

        if (this.isNeutral && this.ticksExisted % 100 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(5.0F); 
        }

        if (this.getHealth() <= this.getMaxHealth() * 0.35 && specialAttackCooldown == 0 && !isSpecialAttacking) {
            isSpecialAttacking = true;
            animationProgress = 1.0F;
        }

        if (isSpecialAttacking) {
            handleSpecialAttackAnimation();
        }

        if (specialAttackCooldown > 0) {
            specialAttackCooldown--;
        }
    }


    private void handleSpecialAttackAnimation() {
        float progress = 1.0F - animationProgress;
    
        if (progress <= 0.4F) {
            float liftProgress = progress / 0.4F;
            rightArmAngle = (float) Math.toRadians(45 + 80 * liftProgress);
            leftArmAngle = (float) Math.toRadians(45 + 80 * liftProgress); 
        } else {
            float slamProgress = (progress - 0.4F) / 0.6F;
            rightArmAngle = (float) Math.toRadians(125 - 80 * slamProgress);
            leftArmAngle = (float) Math.toRadians(125 - 80 * slamProgress); 
    
            if (slamProgress > 0.8F && slamProgress <= 1.0F) {
                performSlamAttack();
            }
        }
    
        animationProgress -= 0.05F;
    
        if (animationProgress <= 0) {
            isSpecialAttacking = false;
            specialAttackCooldown = 200; 
        }
    }
    
    private void performSlamAttack() {
        this.world.playSound(null, this.posX, this.posY, this.posZ,
                net.minecraft.init.SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.HOSTILE, 1.0F, 1.0F);

        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class,
                new AxisAlignedBB(this.posX - 2, this.posY - 1, this.posZ - 2,
                        this.posX + 2, this.posY + 1, this.posZ + 2));

        for (EntityLivingBase entity : entities) {
            if (entity != this) {
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), 20.0F);
                entity.knockBack(this, 1.0F, this.posX - entity.posX, this.posZ - entity.posZ);
            }
        }

        for (int i = 0; i < 20; i++) {
            double offsetX = (this.rand.nextDouble() - 0.5) * 2.0;
            double offsetY = this.rand.nextDouble() * 1.0;
            double offsetZ = (this.rand.nextDouble() - 0.5) * 2.0;

            this.world.spawnParticle(net.minecraft.util.EnumParticleTypes.EXPLOSION_NORMAL,
                    this.posX + offsetX, this.posY + offsetY, this.posZ + offsetZ,
                    0.0, 0.0, 0.0);
        }
    }

    public void updateModelAngles(Monkey model, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch) {
        model.head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
        model.head.rotateAngleX = headPitch * ((float) Math.PI / 180F);
    
        if (isSpecialAttacking) {
            model.right_arm.rotateAngleY = rightArmAngle;
            model.left_arm.rotateAngleY = leftArmAngle;  
        } else {
            model.right_arm.rotateAngleY = (float) Math.toRadians(45);
            model.left_arm.rotateAngleY = (float) Math.toRadians(45);
    
            model.right_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            model.left_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        }
    
        model.right_Leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        model.left_Leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
    }
    @Override
    protected boolean canDespawn() {
        return false; 
    }

    @Override
    public boolean isNoDespawnRequired() {
        return true; 
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.world.getBlockState(this.getPosition().down()).getMaterial().isSolid() && this.world.canSeeSky(this.getPosition());
    }

    
}    
