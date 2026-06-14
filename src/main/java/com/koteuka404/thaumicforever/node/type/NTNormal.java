package com.koteuka404.thaumicforever.node.type;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.world.aura.AuraHandler;
import com.koteuka404.thaumicforever.entity.EntityAuraNode;
import com.koteuka404.thaumicforever.node.NodeType;
import com.koteuka404.thaumicforever.storage.BiomeSpreadWorldData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class NTNormal extends NodeType {
    public NTNormal(int id) {
        super(id);
    }

    @Override
    public void performTickEvent(EntityAuraNode node) {
        if (node.stablized) return;

    }

    @Override
    public void performPeriodicEvent(EntityAuraNode node) {
        if (node.stablized) return;

        if (node.world.isRemote) {
            return;
        }
        Aspect aspect = node.getAspect();
        if (aspect != null && AuraHandler.getVis(node.world, node.getPosition()) < AuraHandler.getAuraBase(node.world, node.getPosition())) {
            AuraHandler.addVis(node.world, node.getPosition(), this.calculateStrength(node));
        }
    }

    @Override
    public int calculateStrength(EntityAuraNode node) {
        int m = node.world.provider.getMoonPhase(node.world.getWorldInfo().getWorldTime());
        float b = 1.0f + (float)(Math.abs(m - 4) - 2) / 5.0f;
        return (int)Math.max(1.0, Math.sqrt((float)node.getNodeSize() / 3.0f) * (double)b * 2.5);
    }

    protected boolean spreadBiome(EntityAuraNode node, Biome targetBiome, int radius) {
        if (targetBiome == null || node.world.isRemote) {
            return false;
        }

        String biomeKey = getBiomeKey(targetBiome);
        String savedBiomeKey = node.getEntityData().getString("tfBiomeSpreadTarget");
        if (!biomeKey.equals(savedBiomeKey)) {
            node.getEntityData().setString("tfBiomeSpreadTarget", biomeKey);
            node.getEntityData().setInteger("tfBiomeSpreadCursor", 0);
        }

        BlockPos center = new BlockPos(node.posX, node.posY, node.posZ);

        int maxCursor = 1 + 4 * radius * (radius + 1);
        int cursor = node.getEntityData().getInteger("tfBiomeSpreadCursor");
        int attempts = Math.min(maxCursor, 32);
        for (int attempt = 0; attempt < attempts; attempt++) {
            if (cursor < 0 || cursor >= maxCursor) {
                cursor = 0;
            }

            BlockPos target = getSpreadPos(center, cursor);
            cursor++;
            node.getEntityData().setInteger("tfBiomeSpreadCursor", cursor >= maxCursor ? 0 : cursor);

            if (target != null && convertBiome(node, target, targetBiome)) {
                return true;
            }
        }

        return false;
    }

    private BlockPos getSpreadPos(BlockPos center, int cursor) {
        if (cursor <= 0) {
            return center;
        }

        int remaining = cursor - 1;
        for (int ring = 1; ; ring++) {
            int perimeter = ring * 8;
            if (remaining < perimeter) {
                int index = remaining;
                int dx;
                int dz;
                if (index < ring * 2) {
                    dx = -ring + index;
                    dz = -ring;
                } else if (index < ring * 4) {
                    dx = ring;
                    dz = -ring + (index - ring * 2);
                } else if (index < ring * 6) {
                    dx = ring - (index - ring * 4);
                    dz = ring;
                } else {
                    dx = -ring;
                    dz = ring - (index - ring * 6);
                }

                return center.add(dx, 0, dz);
            }
            remaining -= perimeter;
        }
    }

    private String getBiomeKey(Biome biome) {
        return biome.getRegistryName() != null
            ? biome.getRegistryName().toString()
            : "id:" + Biome.getIdForBiome(biome);
    }

    protected boolean isTargetBiome(Biome biome, Biome targetBiome) {
        if (biome == targetBiome) {
            return true;
        }

        int targetId = Biome.getIdForBiome(targetBiome);
        return biome != null && targetId >= 0 && Biome.getIdForBiome(biome) == targetId;
    }

    private boolean convertBiome(EntityAuraNode node, BlockPos pos, Biome targetBiome) {
        if (!node.world.isBlockLoaded(pos)) {
            return false;
        }

        if (!isTargetBiome(node.world.getBiome(pos), targetBiome)) {
            BiomeSpreadWorldData.setBiomeAt(node.world, pos, targetBiome);
            return true;
        }

        if (node.world.rand.nextInt(8) == 0) {
            int[][] directions = {
                { 1,  0}, {-1,  0}, { 0,  1}, { 0, -1},
                { 1,  1}, { 1, -1}, {-1,  1}, {-1, -1}
            };
            int start = node.world.rand.nextInt(directions.length);
            for (int i = 0; i < directions.length; i++) {
                int[] direction = directions[(start + i) % directions.length];
                BlockPos adjacent = pos.add(direction[0], 0, direction[1]);
                if (node.world.isBlockLoaded(adjacent) && !isTargetBiome(node.world.getBiome(adjacent), targetBiome)) {
                    BiomeSpreadWorldData.setBiomeAt(node.world, adjacent, targetBiome);
                    return true;
                }
            }
        }

        return false;
    }
}
