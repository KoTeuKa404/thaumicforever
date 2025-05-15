package com.koteuka404.thaumicforever;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;

public class EntityBottleClean extends EntityThrowable {

    public EntityBottleClean(World worldIn) {
        super(worldIn);
    }

    public EntityBottleClean(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            BlockPos hitPos = result.getBlockPos();
            if (hitPos == null) {
                hitPos = new BlockPos(this.posX, this.posY, this.posZ);
            }

            Set<BlockPos> potentialPositions = new HashSet<>();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos newPos = hitPos.add(dx, 0, dz);
                    if (world.isAirBlock(newPos)) {
                        potentialPositions.add(newPos);
                    }
                }
            }

            Random random = new Random();
            int waterCount = 0;
            while (waterCount < 3 && !potentialPositions.isEmpty()) {
                BlockPos selectedPos = potentialPositions.stream()
                        .skip(random.nextInt(potentialPositions.size()))
                        .findFirst().orElse(null);
                if (selectedPos != null) {
                    world.setBlockState(selectedPos, BlocksTC.purifyingFluid.getDefaultState());
                    potentialPositions.remove(selectedPos);
                    waterCount++;
                }
            }

            world.playSound(null, hitPos, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);

            this.setDead();
        }
    }
}
