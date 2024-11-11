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

    private int pathRecalculationTimer = 0; // Таймер для перерахунку шляху
    private int dodgeCooldown = 0; // Таймер для ухилення від стріл
    private boolean calledForHelp = false; // Чи викликав скелет підмогу
    private final Random random = new Random();

    public EntitySkeletonAngry(World worldIn) {
        super(worldIn);
        setSize(0.6F, 1.99F); // Розмір моба

        // Налаштування атрибутів
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D); // Більше здоров'я
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D); // Швидкість зомбі
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D); // Сильніше вдаряє
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D); // Збільшує радіус пошуку цілі

        // Покращуємо навігацію
        this.setPathPriority(PathNodeType.WATER, -1.0F);  // Уникає води

        if (this.getNavigator() instanceof PathNavigateGround) {
            PathNavigateGround navigator = (PathNavigateGround) this.getNavigator();
            navigator.setCanSwim(true);
            navigator.setAvoidSun(false);
        }
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.2D, false)); // Більш агресивна атака
        this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F)); // Стрибки до цілі
        this.tasks.addTask(3, new EntityAIWanderAvoidWater(this, 0.8D)); // Блукає, якщо немає цілі
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F)); // Дивиться на гравця
        this.tasks.addTask(6, new EntityAILookIdle(this)); // Інколи просто дивиться навколо

        // Цілі атаки
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true)); // Нападає на найближчого гравця
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false)); // Реагує на атаку
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        EntityPlayer player = this.world.getClosestPlayerToEntity(this, 15.0D); // Пошук гравця в радіусі 15 блоків
        if (player != null) {
            double heightDifference = player.posY - this.posY;

            // Стрибкова логіка
            if (heightDifference > 0 && heightDifference <= 2.5 && this.collidedHorizontally) {
                this.getJumpHelper().setJumping(); // Примус стрибка
            }

            // Агресивне переслідування
            if (this.getDistance(player) < 10.0D) {
                this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D); // Прискорення при наближенні до гравця
            } else {
                this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D); // Нормальна швидкість
            }

            // Випадковий стрибок, коли наближається до гравця
            if (this.getDistance(player) < 5.0D && random.nextInt(20) == 0) {
                this.getJumpHelper().setJumping();
            }

            // Ухилення від стріл (тільки кожні 3 секунди)
            if (dodgeCooldown == 0 && player.getHeldItemMainhand().getItem() == Items.BOW) {
                if (this.getDistance(player) < 15.0D) {
                    dodge();
                    dodgeCooldown = 60; // Ухиляється кожні 3 секунди
                }
            } else if (dodgeCooldown > 0) {
                dodgeCooldown--;
            }

            // Виклик підмоги, якщо здоров'я нижче 50% і ще не викликав
            // if (this.getHealth() < this.getMaxHealth() * 0.5 && !calledForHelp) {
            //     callForHelp();
            //     calledForHelp = true;
            // }

            // Перерахунок шляху кожні 40 тік (2 секунди)
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

    // Метод для ухилення від стріли
    private void dodge() {
        if (random.nextBoolean()) {
            this.motionX += (random.nextDouble() - 0.5) * 0.5;
        } else {
            this.motionZ += (random.nextDouble() - 0.5) * 0.5;
        }
    }

    // Виклик підмоги
    private void callForHelp() {
        for (int i = 0; i < 2; i++) {
            EntitySkeleton helper = new EntitySkeleton(this.world);
            helper.setPosition(this.posX + random.nextInt(5) - 2, this.posY, this.posZ + random.nextInt(5) - 2);
            this.world.spawnEntity(helper);
        }
    }

    @Override
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        // 100% шанс на дроп звичайної кістки
        entityDropItem(new ItemStack(Items.BONE), 1.5f);

        // 15% шанс на дроп кастомної кістки з урахуванням модифікатора здобичі
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
            double knockbackStrength = 1.0; // Збільшене відкидання
            double dx = entityIn.posX - this.posX;
            double dz = entityIn.posZ - this.posZ;
            ((EntityLivingBase) entityIn).knockBack(this, (float) knockbackStrength, dx, dz);
        }
        return success;
    }
}
