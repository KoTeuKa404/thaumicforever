package com.koteuka404.thaumicforever;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.common.world.biomes.BiomeHandler;

public class NTPure extends NTNormal {
    public NTPure(int id) {
        super(id);
    }

    @Override
    void performPeriodicEvent(EntityAuraNode node) {
        super.performPeriodicEvent(node);

        if (node.world.rand.nextFloat() < 0.1f) {
            int deg = node.world.rand.nextInt(360);
            Vec3d origin = new Vec3d(node.posX, node.posY, node.posZ);
            int radius = (int)(3.0 + Math.sqrt(node.getNodeSize()));
            for (int d = 0; d < radius; d++) {
                Vec3d offset = new Vec3d(d, 0.0, 0.0)
                    .rotateYaw((float)Math.toRadians(deg));
                BlockPos target = new BlockPos(
                    origin.x + offset.x,
                    origin.y + offset.y,
                    origin.z + offset.z
                );
                Biome b = node.world.getBiome(target);
                if (b != BiomeHandler.MAGICAL_FOREST) {
                    Utils.setBiomeAt(node.world, target, BiomeHandler.MAGICAL_FOREST);
                    break;
                }
            }
        }

        if (node.world.rand.nextFloat() < 0.15f) {
            AuraHandler.drainFlux(
                node.world,
                new BlockPos(node.posX, node.posY, node.posZ),
                1,
                false
            );
            node.setNodeSize(node.getNodeSize() - 1);
            if (node.getNodeSize() <= 0) {
                node.setDead();
            }
        }
    }


    @Override
    void performTickEvent(EntityAuraNode node) {
        // no-op
    }
}

