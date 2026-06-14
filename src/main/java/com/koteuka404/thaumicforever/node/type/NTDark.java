package com.koteuka404.thaumicforever.node.type;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.world.biomes.BiomeHandler;
import com.koteuka404.thaumicforever.entity.EntityAuraNode;
import com.koteuka404.thaumicforever.entity.EntitySkeletonAngry;
import com.koteuka404.thaumicforever.config.ModConfig;

public class NTDark extends NTNormal {
    public NTDark(int id) {
        super(id);
    }

    @Override
    public void performPeriodicEvent(EntityAuraNode node) {
        super.performPeriodicEvent(node);

        // Biome writes must be server-side only. Client-side biome mutation can crash with JEID hooks.
        if (ModConfig.sinisterNodeSpreadsEerieBiome && !node.world.isRemote && BiomeHandler.EERIE != null) {
            int eerieId = Biome.getIdForBiome(BiomeHandler.EERIE);
            if (eerieId < 0) {
                return;
            }
            int radius = (int)(9.0 + Math.sqrt(node.getNodeSize()));
            spreadBiome(node, BiomeHandler.EERIE, radius);
        }

        int checkRadius = 7;
        int eerieCount = 0;
        BlockPos center = new BlockPos(node.posX, node.posY, node.posZ);
        for (int dx = -checkRadius; dx <= checkRadius; dx++) {
            for (int dz = -checkRadius; dz <= checkRadius; dz++) {
                if (Math.abs(dx) + Math.abs(dz) > checkRadius) continue; 
                BlockPos checkPos = center.add(dx, 0, dz);
                Biome b = node.world.getBiome(checkPos);
                if (isTargetBiome(b, BiomeHandler.EERIE)) eerieCount++;
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
    public int calculateStrength(EntityAuraNode node) {
        int moon = node.world.provider.getMoonPhase(node.world.getWorldInfo().getWorldTime());
        float phaseFactor = 1.0f - (Math.abs(moon - 4) - 2) / 5.0f;
        phaseFactor += (node.getBrightness() - 0.5f) / 3.0f;
        return Math.max(1,(int)(Math.sqrt(node.getNodeSize() / 3.0f) * phaseFactor));
    }
}
