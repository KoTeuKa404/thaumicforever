package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityTimeFreezeProjectile extends EntityThrowable {

    public EntityTimeFreezeProjectile(World worldIn) {
        super(worldIn);
    }

    public EntityTimeFreezeProjectile(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntityTimeFreezeProjectile(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            // Створюємо область дії снаряда
            int radius = 3;
            BlockPos hitPos = new BlockPos(this.posX, this.posY, this.posZ);

            // Створюємо магічні блоки в області дії
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = hitPos.add(x, y, z);
                        if (world.isAirBlock(pos) && Math.sqrt(x * x + y * y + z * z) <= radius) {
                            world.setBlockState(pos, ModBlocks.TIME_STOP.getDefaultState());

                            // Запланувати видалення блоку через 200 тік (10 секунд)
                            world.scheduleBlockUpdate(pos, ModBlocks.TIME_STOP, 100, 1);
                        }
                    }
                }
            }

            // Заморожуємо всіх живих істот у радіусі
            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(radius));
            for (EntityLivingBase entity : entities) {
                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 100, false, false));
                entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 100, 100, false, false));
                entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100, 100, false, false));
            }

            // Видаляємо снаряд після влучання
            this.setDead();
        }
    }



    @Override
    public void onUpdate() {
        super.onUpdate();

        if (world.isRemote) {
            // Використовуємо свій FXDispatcher для створення ефекту туману
            FXDispatcher.INSTANCE.drawAlumentum((float) posX, (float) posY, (float) posZ, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.5f, 4.0f);
        }
    }
}
