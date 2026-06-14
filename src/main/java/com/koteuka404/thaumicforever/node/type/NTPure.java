package com.koteuka404.thaumicforever.node.type;

import net.minecraft.util.math.BlockPos;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.common.world.biomes.BiomeHandler;
import com.koteuka404.thaumicforever.entity.EntityAuraNode;

public class NTPure extends NTNormal {
    public NTPure(int id) {
        super(id);
    }

    @Override
    public void performPeriodicEvent(EntityAuraNode node) {
        super.performPeriodicEvent(node);

        if (node.world.isRemote) {
            return;
        }

        int radius = (int)(3.0 + Math.sqrt(node.getNodeSize()));
        spreadBiome(node, BiomeHandler.MAGICAL_FOREST, radius);

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
    public void performTickEvent(EntityAuraNode node) {
        // no-op
    }
}
