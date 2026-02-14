package com.koteuka404.thaumicforever;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.world.biomes.BiomeHandler;

public class NTDark extends NTNormal {
    public NTDark(int id) {
        super(id);
    }

    @Override
    void performPeriodicEvent(EntityAuraNode node) {
        super.performPeriodicEvent(node);

        int deg = node.world.rand.nextInt(360);
        Vec3d origin = new Vec3d(node.posX, node.posY, node.posZ);
        int radius = (int)(9.0 + Math.sqrt(node.getNodeSize()));
        for (int q = 0; q < radius; q++) {
            Vec3d offset = new Vec3d(q, 0.0, 0.0).rotateYaw((float)Math.toRadians(deg));
            BlockPos targetPos = new BlockPos(
                origin.x + offset.x,
                origin.y + offset.y,
                origin.z + offset.z
            );
            Biome b = node.world.getBiome(targetPos);
            if (b != BiomeHandler.EERIE) {
                Utils.setBiomeAt(node.world, targetPos, BiomeHandler.EERIE);
                break;
            }
        }

        int checkRadius = 7;
        int eerieCount = 0;
        BlockPos center = new BlockPos(node.posX, node.posY, node.posZ);
        for (int dx = -checkRadius; dx <= checkRadius; dx++) {
            for (int dz = -checkRadius; dz <= checkRadius; dz++) {
                if (Math.abs(dx) + Math.abs(dz) > checkRadius) continue; 
                BlockPos checkPos = center.add(dx, 0, dz);
                Biome b = node.world.getBiome(checkPos);
                if (b == BiomeHandler.EERIE) eerieCount++;
            }
        }
        eerieCount = Math.min(eerieCount, 15);

        double baseChance = 1.0 / 2000.0;
        double actualChance = baseChance * (1 + eerieCount);

        if (!node.world.isRemote && node.world.rand.nextDouble() < actualChance) {
            BlockPos nodePos = new BlockPos(node.posX, node.posY, node.posZ);

            BlockPos ground = nodePos;
            while (
                ground.getY() > 1 && (
                    node.world.isAirBlock(ground) ||
                    node.world.getBlockState(ground).getMaterial().isLiquid() ||
                    node.world.getBlockState(ground).getBlock().isReplaceable(node.world, ground)
                )
            ) {
                ground = ground.down();
            }
            BlockPos spawnPos = ground.up();

            boolean canSpawn = node.world.isAirBlock(spawnPos) && node.world.isAirBlock(spawnPos.up());
            if (canSpawn) {
                EntityGiantBrainyZombie zombie = new EntityGiantBrainyZombie(node.world);
                zombie.setLocationAndAngles(
                    spawnPos.getX() + 0.5,
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.5,
                    node.world.rand.nextFloat() * 360.0F, 
                    0.0F
                );
                zombie.onInitialSpawn(node.world.getDifficultyForLocation(spawnPos), null);
                node.world.spawnEntity(zombie);

                EntitySkeletonAngry skeleton = new EntitySkeletonAngry(node.world);
                skeleton.setLocationAndAngles(
                    spawnPos.getX() + 0.5,
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.5,
                    node.world.rand.nextFloat() * 360.0F,
                    0.0F
                );
                skeleton.onInitialSpawn(node.world.getDifficultyForLocation(spawnPos), null);
                node.world.spawnEntity(skeleton);
            }
        }
    }

    @Override
    int calculateStrength(EntityAuraNode node) {
        int moon = node.world.provider.getMoonPhase(node.world.getWorldInfo().getWorldTime());
        float phaseFactor = 1.0f - (Math.abs(moon - 4) - 2) / 5.0f;
        phaseFactor += (node.getBrightness() - 0.5f) / 3.0f;
        return Math.max(1,(int)(Math.sqrt(node.getNodeSize() / 3.0f) * phaseFactor));
    }
}
