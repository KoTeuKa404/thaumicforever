package com.koteuka404.thaumicforever;

import java.util.UUID;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class PlayerStructureManager {
    private static final String STRUCTURE_NAME = "thaumicforever:void";

    public static BlockPos getOrCreateStructureForPlayer(UUID playerID, World world) {
        // System.out.println("ThaumicForever DEBUG: getOrCreateStructureForPlayer " + playerID);

        PlayerStructureData data = PlayerStructureData.get(world);

        if (data.hasPlayerStructure(playerID)) {
            // System.out.println("ThaumicForever DEBUG: Структура вже є для " + playerID);
            return data.getPlayerStructure(playerID);
        }

        BlockPos genPos = generateStructurePosition(playerID);

        forceLoadChunks(world, genPos, 100);

        BlockPos existingOriginMarker = findExistingStructure(world, genPos, 100);
        if (existingOriginMarker != null) {

            BlockPos origin = new BlockPos(existingOriginMarker.getX(), genPos.getY(), existingOriginMarker.getZ());
            BlockPos center = computeCenterFromOrigin(world, origin);

            // System.out.println("ThaumicForever DEBUG: Знайдено існуючу структуру (бар'єр) у " + existingOriginMarker
            //     + " => center " + center);

            data.setPlayerStructure(playerID, center);
            return center;
        }

        BlockPos centerPos = placeStructure(world, genPos);
        data.setPlayerStructure(playerID, centerPos);

        // System.out.println("ThaumicForever DEBUG: Згенерували нову структуру для " + playerID + " у " + centerPos);

        return centerPos;
    }

    private static BlockPos generateStructurePosition(UUID playerID) {
        int hash = playerID.hashCode();
        int x = (hash % 1000);
        int z = (hash / 1000);
        return new BlockPos(x, 64, z);
    }

    private static void forceLoadChunks(World world, BlockPos center, int radius) {
        int minChunkX = (center.getX() - radius) >> 4;
        int maxChunkX = (center.getX() + radius) >> 4;
        int minChunkZ = (center.getZ() - radius) >> 4;
        int maxChunkZ = (center.getZ() + radius) >> 4;
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                world.getChunkProvider().provideChunk(cx, cz);
            }
        }
        // System.out.println("ThaumicForeve,r DEBUG: forceLoaded chunks for " + center + " r=" + radius);
    }

    private static BlockPos findExistingStructure(World world, BlockPos center, int radius) {
        int startX = center.getX() - radius;
        int endX   = center.getX() + radius;
        int startY = Math.max(0, center.getY() - radius);
        int endY   = Math.min(255, center.getY() + radius);
        int startZ = center.getZ() - radius;
        int endZ   = center.getZ() + radius;

        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y += 2) {
                    BlockPos checkPos = new BlockPos(x, y, z);
                    if (world.isBlockLoaded(checkPos) &&
                        world.getBlockState(checkPos).getBlock() == Blocks.BARRIER) {
                        // System.out.println("ThaumicForever DEBUG: findExistingStructure: barrier found at " + checkPos);
                        return checkPos;
                    }
                }
            }
        }
        return null;
    }

    private static BlockPos computeCenterFromOrigin(World world, BlockPos origin) {
        TemplateManager mgr = world.getSaveHandler().getStructureTemplateManager();
        Template tmpl = mgr.getTemplate(world.getMinecraftServer(), new ResourceLocation(STRUCTURE_NAME));
    
        if (tmpl == null) {
            // System.err.println("ThaumicForever: Failed to load structure template: " + STRUCTURE_NAME);
            return origin;
        }
    
        int cx = tmpl.getSize().getX() / 2;
        int cz = tmpl.getSize().getZ() / 2;
    
        return origin.add(cx, 1, cz);
    }
    

    private static BlockPos placeStructure(World world, BlockPos pos) {
        TemplateManager mgr = world.getSaveHandler().getStructureTemplateManager();
        Template tmpl = mgr.getTemplate(world.getMinecraftServer(), new ResourceLocation(STRUCTURE_NAME));

        if (tmpl != null) {
            int cx = tmpl.getSize().getX() / 2;
            int cz = tmpl.getSize().getZ() / 2;
            BlockPos center = pos.add(cx, 1, cz);

            if (!world.isBlockLoaded(pos)) {
                world.getChunkProvider().provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
            }

            tmpl.addBlocksToWorld(world, pos, new PlacementSettings());
            // System.out.println("ThaumicForever DEBUG: placeStructure at " + pos);
            return center;
        } else {
            // System.err.println("ThaumicForever: Failed to load structure template: " + STRUCTURE_NAME);
            return pos;
        }
    }
}
