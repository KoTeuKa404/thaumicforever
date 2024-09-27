package com.tutorialmod.turtywurty;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
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

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            BlockPos hitPos = result.getBlockPos() != null ? result.getBlockPos() : new BlockPos(result.entityHit);
            createTimeFreezeDome(hitPos);

            // Якщо це влучання в сутність, виконаємо ефект заморожування
            if (result.entityHit instanceof EntityLivingBase) {
                applyFreezeEffect((EntityLivingBase) result.entityHit);
            }

            setDead();  // Знищуємо сутність після впливу
        }
    }

    private void createTimeFreezeDome(BlockPos center) {
        // Логіка для створення купола розміром 3x3x3 з льоду
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = center.add(x, y, z);
                    // Тимчасово замінюємо блоки на лід
                    if (world.isAirBlock(pos)) {  // Замінюємо тільки повітря
                        world.setBlockState(pos, Blocks.ICE.getDefaultState());
                    }
                }
            }
        }
    }

    private void applyFreezeEffect(EntityLivingBase entity) {
        // Ефекти заморожування для сутностей
        entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 5, false, true));
        entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 200, 1, false, true));
    }
}
