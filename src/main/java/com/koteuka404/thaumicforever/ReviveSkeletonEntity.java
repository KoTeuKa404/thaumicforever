package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ReviveSkeletonEntity extends EntityMob {

    private int attackCooldown = 0; 

    public ReviveSkeletonEntity(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);

        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(11.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.18D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        this.setHealth(this.getMaxHealth());

        if (this.rand.nextInt(100) < 5) {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.SCROLL_P, 1, this.rand.nextInt(4))); 
        }
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.2D, false));
        this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 0.8D)); 
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, true));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));

    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        if (this.getAttackTarget() == null) {
            joinGroupAttack();
        }

        EntityPlayer player = this.world.getClosestPlayerToEntity(this, 15.0D);
        if (player != null) {
            double distance = this.getDistance(player);

            if (distance < 10.0D) {
                this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.18D);
            } else {
                this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.18D);
            }

            if (distance <= 2.0D && attackCooldown == 0) {
                performAttack(player);
            }
        }
    }

    private void performAttack(EntityPlayer player) {
        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        player.attackEntityFrom(DamageSource.causeMobDamage(this), damage);

        attackCooldown = 20; 
        System.out.println("Attacked player with " + damage + " damage!");
    }

    private void callForHelp() {
        List<ReviveSkeletonEntity> nearbySkeletons = this.world.getEntitiesWithinAABB(ReviveSkeletonEntity.class, this.getEntityBoundingBox().grow(10.0D));
        for (ReviveSkeletonEntity skeleton : nearbySkeletons) {
            if (skeleton != this && skeleton.getAttackTarget() == null) {
                skeleton.setAttackTarget(this.getAttackTarget());
                System.out.println("Calling for help from nearby skeleton!");
            }
        }
    }

    private void synchronizeAttackTarget(EntityLivingBase target) {
        List<ReviveSkeletonEntity> nearbySkeletons = this.world.getEntitiesWithinAABB(ReviveSkeletonEntity.class, this.getEntityBoundingBox().grow(10.0D));
        for (ReviveSkeletonEntity skeleton : nearbySkeletons) {
            if (skeleton != this && skeleton.getAttackTarget() == null) {
                skeleton.setAttackTarget(target);
                System.out.println("Synchronizing attack target!");
            }
        }
    }

    @Override
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        ItemStack heldItem = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        if (!heldItem.isEmpty()) {
            this.entityDropItem(heldItem, 0.0F); 
        }
        for (int i = 0; i < 3; i++) {
            this.entityDropItem(new ItemStack(ModItems.OldBone), 0.0F);
        }

        if (this.world.rand.nextInt(100) < 10) { 
            this.entityDropItem(new ItemStack(ModItems.OldBone), 0.0F);
        }

        super.dropLoot(wasRecentlyHit, lootingModifier, source);
    }

    private void joinGroupAttack() {
        List<ReviveSkeletonEntity> nearbySkeletons = this.world.getEntitiesWithinAABB(ReviveSkeletonEntity.class, this.getEntityBoundingBox().grow(10.0D));
        for (ReviveSkeletonEntity skeleton : nearbySkeletons) {
            if (skeleton != this && skeleton.getAttackTarget() != null) {
                this.setAttackTarget(skeleton.getAttackTarget());
                break;
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean result = super.attackEntityFrom(source, amount);

        if (source.getTrueSource() instanceof EntityLivingBase) {
            this.setAttackTarget((EntityLivingBase) source.getTrueSource());
            callForHelp();
        }

        return result;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD; 
    }
}
