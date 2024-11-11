package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntitySkeletonAngry extends EntityMob {

    private int pathRecalculationTimer = 0; 
    private int dodgeCooldown = 0;
    private final Random random = new Random();

    public EntitySkeletonAngry(World worldIn) {
        super(worldIn);
        setSize(0.6F, 1.99F);

        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D); 
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D); 
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D); 
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);

        this.setPathPriority(PathNodeType.WATER, -1.0F); 

        if (this.getNavigator() instanceof PathNavigateGround) {
            PathNavigateGround navigator = (PathNavigateGround) this.getNavigator();
            navigator.setCanSwim(true);
            navigator.setAvoidSun(false);
        }
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.2D, false)); 
        this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F)); 
        this.tasks.addTask(3, new EntityAIWanderAvoidWater(this, 0.8D));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F)); 
        this.tasks.addTask(6, new EntityAILookIdle(this)); 

        // Цілі атаки
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true)); 
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false)); 
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        EntityPlayer player = this.world.getClosestPlayerToEntity(this, 15.0D); 
        if (player != null) {
            double heightDifference = player.posY - this.posY;

            if (heightDifference > 0 && heightDifference <= 2.5 && this.collidedHorizontally) {
                this.getJumpHelper().setJumping(); // Примус стрибка
            }

            if (this.getDistance(player) < 10.0D) {
                this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
            } else {
                this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D);
            }

            if (this.getDistance(player) < 5.0D && random.nextInt(20) == 0) {
                this.getJumpHelper().setJumping();
            }

            if (dodgeCooldown == 0 && player.getHeldItemMainhand().getItem() == Items.BOW) {
                if (this.getDistance(player) < 15.0D) {
                    dodge();
                    dodgeCooldown = 60;
                }
            } else if (dodgeCooldown > 0) {
                dodgeCooldown--;
            }

            if (!this.getNavigator().noPath()) {
                pathRecalculationTimer = 0;
            } else {
                pathRecalculationTimer++;
                if (pathRecalculationTimer >= 40) {
                    this.getNavigator().tryMoveToEntityLiving(player, 1.2D); // Перерахунок шляху
                    pathRecalculationTimer = 0;
                }
            }
        }
    }

    private void dodge() {
        if (random.nextBoolean()) {
            this.motionX += (random.nextDouble() - 0.5) * 0.5;
        } else {
            this.motionZ += (random.nextDouble() - 0.5) * 0.5;
        }
    }

    private void callForHelp() {
        for (int i = 0; i < 2; i++) {
            EntitySkeleton helper = new EntitySkeleton(this.world);
            helper.setPosition(this.posX + random.nextInt(5) - 2, this.posY, this.posZ + random.nextInt(5) - 2);
            this.world.spawnEntity(helper);
        }
    }

    @Override
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        entityDropItem(new ItemStack(Items.BONE), 1.5f);

        if (world.rand.nextInt(100) < 15 + lootingModifier) {
            entityDropItem(new ItemStack(ModItems.Bone), 1.5f);
        }

        super.dropLoot(wasRecentlyHit, lootingModifier, source);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        // Потужне відкидання при ударі
        boolean success = super.attackEntityAsMob(entityIn);
        if (success && entityIn instanceof EntityLivingBase) {
            double knockbackStrength = 1.0;
            double dx = entityIn.posX - this.posX;
            double dz = entityIn.posZ - this.posZ;
            ((EntityLivingBase) entityIn).knockBack(this, (float) knockbackStrength, dx, dz);
        }
        return success;
    }
}
