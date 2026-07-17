package com.koteuka404.thaumicforever.world.structure;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.koteuka404.thaumicforever.entity.EntityVoidTraider;
import com.koteuka404.thaumicforever.config.ModConfig;
import com.koteuka404.thaumicforever.world.ModDimensions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class PlayerStructureManager {
    private static final long DAY = 24000L;
    private static final long ACTIVE_TIME = 3L * DAY;
    private static final ResourceLocation VOID_STRUCTURE = new ResourceLocation("thaumicforever", "void");
    private static final ResourceLocation VOID_TRADER_STRUCTURE = new ResourceLocation("thaumicforever", "traidervoid");
    private static final ResourceLocation VOID_TRADER_MARKER_BLOCK = new ResourceLocation("thaumcraft", "stone_ancient_doorway");

    private static final Rotation VOID_TRADER_ROTATION = Rotation.CLOCKWISE_180;

    private static final int VOID_TRADER_Y = 2;

    private static final int TRADER_SHIFT_X = 0;
    private static final int TRADER_SOUTH_GAP = 22;

    private static final float VOID_TRAIDER_YAW = 180.0F;

    private static final double VOID_TRAIDER_SHIFT_X = 0.0D;
    private static final double VOID_TRAIDER_SHIFT_Z = -1.0D;

    private static final int GROUND_SCAN_MIN_Y = 0;
    private static final int GROUND_SCAN_MAX_Y = 6;

    private static final BlockPos OLD_TRADER_SIZE = new BlockPos(16, 8, 16);

    public static BlockPos getOrCreateStructureForPlayer(UUID playerID, World world) {
        PlayerStructureData data = PlayerStructureData.get(world);

        if (data.hasPlayerStructure(playerID)) {
            BlockPos center = data.getPlayerStructure(playerID);
            return center;
        }

        BlockPos genPos = generateStructurePosition(playerID);

        forceLoadChunks(world, genPos, 100);

        BlockPos existingOriginMarker = findExistingStructure(world, genPos, 100);
        if (existingOriginMarker != null) {
            BlockPos origin = new BlockPos(existingOriginMarker.getX(), genPos.getY(), existingOriginMarker.getZ());
            BlockPos center = computeCenterFromOrigin(world, origin);

            data.setPlayerStructure(playerID, center);
            return center;
        }

        BlockPos centerPos = placeStructure(world, genPos);
        data.setPlayerStructure(playerID, centerPos);

        return centerPos;
    }

    private static BlockPos generateStructurePosition(UUID playerID) {
        int hash = playerID.hashCode();
        int x = hash % 1000;
        int z = hash / 1000;
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
    }

    private static void forceLoadTemplateChunks(World world, BlockPos origin, BlockPos size) {
        int minChunkX = origin.getX() >> 4;
        int maxChunkX = (origin.getX() + Math.max(0, size.getX() - 1)) >> 4;
        int minChunkZ = origin.getZ() >> 4;
        int maxChunkZ = (origin.getZ() + Math.max(0, size.getZ() - 1)) >> 4;

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                world.getChunkProvider().provideChunk(cx, cz);
            }
        }
    }

    private static BlockPos findExistingStructure(World world, BlockPos center, int radius) {
        int startX = center.getX() - radius;
        int endX = center.getX() + radius;
        int startY = Math.max(0, center.getY() - radius);
        int endY = Math.min(255, center.getY() + radius);
        int startZ = center.getZ() - radius;
        int endZ = center.getZ() + radius;

        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y += 2) {
                    BlockPos checkPos = new BlockPos(x, y, z);
                    if (world.isBlockLoaded(checkPos)
                        && world.getBlockState(checkPos).getBlock() == Blocks.BARRIER) {
                        return checkPos;
                    }
                }
            }
        }

        return null;
    }

    private static BlockPos computeCenterFromOrigin(World world, BlockPos origin) {
        TemplateManager mgr = world.getSaveHandler().getStructureTemplateManager();
        Template tmpl = mgr.getTemplate(world.getMinecraftServer(), VOID_STRUCTURE);

        if (tmpl == null) {
            return origin;
        }

        int cx = tmpl.getSize().getX() / 2;
        int cz = tmpl.getSize().getZ() / 2;

        return origin.add(cx, 1, cz);
    }

    private static BlockPos computeOriginFromCenter(World world, BlockPos center) {
        TemplateManager mgr = world.getSaveHandler().getStructureTemplateManager();
        Template tmpl = mgr.getTemplate(world.getMinecraftServer(), VOID_STRUCTURE);

        if (tmpl == null) {
            return null;
        }

        int cx = tmpl.getSize().getX() / 2;
        int cz = tmpl.getSize().getZ() / 2;

        return center.add(-cx, -1, -cz);
    }

    private static BlockPos placeStructure(World world, BlockPos pos) {
        TemplateManager mgr = world.getSaveHandler().getStructureTemplateManager();
        Template tmpl = mgr.getTemplate(world.getMinecraftServer(), VOID_STRUCTURE);

        if (tmpl != null) {
            int cx = tmpl.getSize().getX() / 2;
            int cz = tmpl.getSize().getZ() / 2;
            BlockPos center = pos.add(cx, 1, cz);

            forceLoadTemplateChunks(world, pos, tmpl.getSize());
            tmpl.addBlocksToWorld(world, pos, new PlacementSettings());

            return center;
        }

        return pos;
    }

    private static BlockPos placeVoidTraderAtEdge(World world, BlockPos voidOrigin) {
        TemplateManager mgr = world.getSaveHandler().getStructureTemplateManager();
        Template voidTemplate = mgr.getTemplate(world.getMinecraftServer(), VOID_STRUCTURE);
        Template traderTemplate = mgr.getTemplate(world.getMinecraftServer(), VOID_TRADER_STRUCTURE);

        if (voidTemplate == null || traderTemplate == null) {
            return null;
        }

        forceLoadTemplateChunks(world, voidOrigin, voidTemplate.getSize());

        clearKnownOldTraderPlacements(world, voidOrigin, voidTemplate);

        RealBounds bounds = findRealVoidGroundBounds(world, voidOrigin, voidTemplate.getSize());
        if (bounds == null) {
            return null;
        }

        clearNonSouthTraderPlacements(world, voidOrigin, bounds, traderTemplate);

        // Always derive the position from the void structure, never from stale
        // legacy platforms left in an older world.
        BlockPos finalStart = getTraderFinalStartPosition(voidOrigin, bounds, traderTemplate);
        BlockPos placementOrigin = getTraderPlacementOriginForRotation(finalStart, traderTemplate);

        forceLoadTemplateChunks(world, finalStart.add(-2, 0, -2),
            new BlockPos(traderTemplate.getSize().getX() + 4, traderTemplate.getSize().getY() + 4, traderTemplate.getSize().getZ() + 4));

        // A previous despawn leaves a barrier shell around the trader. Remove
        // only that shell before placing the next template; never clear the
        // surrounding void structure.
        clearTraderBarrier(world, finalStart, traderTemplate.getSize());

        if (containsMarkerInArea(world, finalStart, traderTemplate.getSize())) {
            spawnVoidTraiderAtTraderCenter(world, finalStart, traderTemplate);
            return finalStart;
        }

        clearArea(world, finalStart, traderTemplate.getSize());

        PlacementSettings settings = new PlacementSettings().setRotation(VOID_TRADER_ROTATION);
        traderTemplate.addBlocksToWorld(world, placementOrigin, settings);

        spawnVoidTraiderAtTraderCenter(world, finalStart, traderTemplate);
        return finalStart;
    }

    public static void tickVoidTraider(World world) {
        if (world.isRemote || world.provider.getDimension() != ModDimensions.VOID_DIMENSION_ID) {
            return;
        }

        PlayerStructureData data = PlayerStructureData.get(world);
        // Keep the schedule on the same clock as WorldSavedData. The custom Void
        // dimension can have a separate time value or stop ticking when unloaded.
        World overworld = world.getMinecraftServer().getWorld(0);
        long now = overworld.getWorldTime();
        if (now % 20L != 0L) {
            return;
        }
        Random random = world.rand;
        for (Map.Entry<UUID, BlockPos> entry : data.getPlayerStructures().entrySet()) {
            PlayerStructureData.TraderState state = data.getTraderState(entry.getKey());

            if (state.nextSpawnTime < 0L) {
                removeLegacyTrader(world, entry.getValue());
                state.nextSpawnTime = now + randomDelay(random);
                data.markDirty();
                continue;
            }


            if (!state.active && state.nextSpawnTime > now + getMaxSpawnDelay()) {
                state.nextSpawnTime = now + randomDelay(random);
                data.markDirty();
            }

            if (state.active && state.despawnTime > now + ACTIVE_TIME) {
                removeTraderAndPlaceBarrier(world, state);
                state.active = false;
                state.despawnTime = -1L;
                state.nextSpawnTime = now + randomDelay(random);
                data.markDirty();
                continue;
            }

            if (state.active) {
                if (now >= state.despawnTime) {
                    removeTraderAndPlaceBarrier(world, state);
                    state.active = false;
                    state.despawnTime = -1L;
                    state.nextSpawnTime = now + randomDelay(random);
                    data.markDirty();
                }
                continue;
            }

            if (now >= state.nextSpawnTime) {
                BlockPos traderStart = spawnScheduledTrader(world, entry.getValue());
                Template traderTemplate = world.getSaveHandler().getStructureTemplateManager()
                    .getTemplate(world.getMinecraftServer(), VOID_TRADER_STRUCTURE);
                if (traderStart != null && traderTemplate != null) {
                    state.areaStart = traderStart;
                    state.areaX = traderTemplate.getSize().getX();
                    state.areaY = traderTemplate.getSize().getY();
                    state.areaZ = traderTemplate.getSize().getZ();
                    state.active = true;
                    state.despawnTime = now + ACTIVE_TIME;
                    data.markDirty();
                } else {
                    state.nextSpawnTime = now + DAY;
                    data.markDirty();
                }
            }
        }
    }

    private static long randomDelay(Random random) {
        long minDays = Math.max(1L, ModConfig.voidTraiderMinSpawnDays);
        long maxDays = Math.max(minDays, (long) ModConfig.voidTraiderMaxSpawnDays);
        long range = maxDays - minDays + 1L;
        long days = minDays + (range <= Integer.MAX_VALUE
            ? random.nextInt((int) range)
            : (long) (random.nextDouble() * range));
        return days * DAY;
    }

    private static long getMaxSpawnDelay() {
        long minDays = Math.max(1L, ModConfig.voidTraiderMinSpawnDays);
        long maxDays = Math.max(minDays, (long) ModConfig.voidTraiderMaxSpawnDays);
        return maxDays * DAY;
    }

    private static BlockPos spawnScheduledTrader(World world, BlockPos structureCenter) {
        BlockPos origin = computeOriginFromCenter(world, structureCenter);
        return origin == null ? null : placeVoidTraderAtEdge(world, origin);
    }

    private static void removeLegacyTrader(World world, BlockPos structureCenter) {
        AxisAlignedBB box = new AxisAlignedBB(
            structureCenter.getX() - 80, structureCenter.getY() - 16, structureCenter.getZ() - 80,
            structureCenter.getX() + 80, structureCenter.getY() + 24, structureCenter.getZ() + 80
        );
        for (EntityVoidTraider trader : world.getEntitiesWithinAABB(EntityVoidTraider.class, box)) {
            trader.setDead();
        }
    }

    private static void removeTraderAndPlaceBarrier(World world, PlayerStructureData.TraderState state) {
        if (state.areaStart == null || state.areaX <= 0) {
            return;
        }

        BlockPos start = state.areaStart;
        AxisAlignedBB box = new AxisAlignedBB(start.getX() - 3, start.getY() - 3, start.getZ() - 3,
            start.getX() + state.areaX + 3, start.getY() + state.areaY + 3, start.getZ() + state.areaZ + 3);
        for (EntityVoidTraider trader : world.getEntitiesWithinAABB(EntityVoidTraider.class, box)) {
            trader.setDead();
        }

        BlockPos size = new BlockPos(state.areaX, state.areaY, state.areaZ);
        clearArea(world, start, size);
        int maxY = Math.min(255, start.getY() + state.areaY - 1);
        for (int y = start.getY(); y <= maxY; y++) {
            for (int x = start.getX() - 1; x <= start.getX() + state.areaX; x++) {
                placeBarrierIfEmpty(world, new BlockPos(x, y, start.getZ() - 1));
                placeBarrierIfEmpty(world, new BlockPos(x, y, start.getZ() + state.areaZ));
            }
            for (int z = start.getZ(); z < start.getZ() + state.areaZ; z++) {
                placeBarrierIfEmpty(world, new BlockPos(start.getX() - 1, y, z));
                placeBarrierIfEmpty(world, new BlockPos(start.getX() + state.areaX, y, z));
            }
        }
    }

    private static void clearTraderBarrier(World world, BlockPos start, BlockPos size) {
        int maxY = Math.min(255, start.getY() + size.getY() - 1);
        for (int y = start.getY(); y <= maxY; y++) {
            for (int x = start.getX() - 1; x <= start.getX() + size.getX(); x++) {
                clearBarrierOnly(world, new BlockPos(x, y, start.getZ() - 1));
                clearBarrierOnly(world, new BlockPos(x, y, start.getZ() + size.getZ()));
            }
            for (int z = start.getZ(); z < start.getZ() + size.getZ(); z++) {
                clearBarrierOnly(world, new BlockPos(start.getX() - 1, y, z));
                clearBarrierOnly(world, new BlockPos(start.getX() + size.getX(), y, z));
            }
        }
    }

    private static void clearBarrierOnly(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == Blocks.BARRIER) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
        }
    }

    private static void placeBarrierIfEmpty(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == Blocks.AIR
            || world.getBlockState(pos).getBlock() == Blocks.BARRIER) {
            world.setBlockState(pos, Blocks.BARRIER.getDefaultState(), 2);
        }
    }

    private static BlockPos getTraderFinalStartPosition(BlockPos voidOrigin, RealBounds bounds, Template traderTemplate) {
        int traderXSize = traderTemplate.getSize().getX();

        int x = voidOrigin.getX() + bounds.minX + ((bounds.getSizeX() - traderXSize) / 2) + TRADER_SHIFT_X;
        int z = voidOrigin.getZ() + bounds.maxZ + 1 + TRADER_SOUTH_GAP;
        int y = voidOrigin.getY() + VOID_TRADER_Y;

        return new BlockPos(x, y, z);
    }

    private static BlockPos getTraderPlacementOriginForRotation(BlockPos finalStart, Template traderTemplate) {
        return finalStart.add(
            traderTemplate.getSize().getX() - 1,
            0,
            traderTemplate.getSize().getZ() - 1
        );
    }

    private static RealBounds findRealVoidGroundBounds(World world, BlockPos voidOrigin, BlockPos templateSize) {
        RealBounds bounds = null;

        int minY = Math.max(0, GROUND_SCAN_MIN_Y);
        int maxY = Math.min(templateSize.getY() - 1, GROUND_SCAN_MAX_Y);

        for (int x = 0; x < templateSize.getX(); x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = 0; z < templateSize.getZ(); z++) {
                    BlockPos pos = voidOrigin.add(x, y, z);

                    if (!world.isBlockLoaded(pos)) {
                        continue;
                    }

                    if (isIgnoredVoidPadding(world, pos)) {
                        continue;
                    }

                    if (bounds == null) {
                        bounds = new RealBounds(x, z);
                    } else {
                        bounds.include(x, z);
                    }
                }
            }
        }

        return bounds;
    }

    private static boolean isIgnoredVoidPadding(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == Blocks.AIR
            || world.getBlockState(pos).getBlock() == Blocks.BARRIER;
    }

    private static void clearKnownOldTraderPlacements(World world, BlockPos voidOrigin, Template voidTemplate) {
        int detachedX = voidTemplate.getSize().getX();
        int detachedZ = (voidTemplate.getSize().getZ() - 4) / 2;

        clearAreaIfContainsMarker(world, voidOrigin.add(detachedX, 0, detachedZ), OLD_TRADER_SIZE);
        clearAreaIfContainsMarker(world, voidOrigin.add(detachedX, 2, detachedZ), OLD_TRADER_SIZE);

        clearAreaIfContainsMarker(world, voidOrigin.add(28, 2, 29), OLD_TRADER_SIZE);
        clearAreaIfContainsMarker(world, voidOrigin.add(36, 2, 21), OLD_TRADER_SIZE);
        clearAreaIfContainsMarker(world, voidOrigin.add(68, 2, 29), OLD_TRADER_SIZE);
    }

    private static BlockPos findKnownOldTraderPlacement(World world, BlockPos voidOrigin) {
        BlockPos[] candidates = {
            voidOrigin.add(28, 2, 29),
            voidOrigin.add(36, 2, 21),
            voidOrigin.add(68, 2, 29)
        };

        for (BlockPos candidate : candidates) {
            if (containsMarkerInArea(world, candidate, OLD_TRADER_SIZE)) {
                return candidate;
            }
        }
        return null;
    }

    private static boolean clearAreaIfContainsMarker(World world, BlockPos origin, BlockPos size) {
        forceLoadTemplateChunks(world, origin, size);

        if (!containsMarkerInArea(world, origin, size)) {
            return false;
        }

        clearArea(world, origin, size);
        return true;
    }

    private static void clearNonSouthTraderPlacements(World world, BlockPos voidOrigin, RealBounds bounds, Template traderTemplate) {
        BlockPos size = traderTemplate.getSize();
        int traderXSize = size.getX();
        int traderZSize = size.getZ();
        int y = voidOrigin.getY() + VOID_TRADER_Y;

        int centeredX = voidOrigin.getX() + bounds.minX + ((bounds.getSizeX() - traderXSize) / 2) + TRADER_SHIFT_X;
        int centeredZ = voidOrigin.getZ() + bounds.minZ + ((bounds.getSizeZ() - traderZSize) / 2);

        // Keep the trader fixed on world south (+Z). These clean only old trader
        // templates that contain the marker block, so normal void terrain is left alone.
        clearAreaIfContainsMarker(world, new BlockPos(centeredX, y, voidOrigin.getZ() + bounds.minZ - traderZSize - TRADER_SOUTH_GAP), size);
        clearAreaIfContainsMarker(world, new BlockPos(voidOrigin.getX() + bounds.minX - traderXSize - TRADER_SOUTH_GAP, y, centeredZ), size);
        clearAreaIfContainsMarker(world, new BlockPos(voidOrigin.getX() + bounds.maxX + 1 + TRADER_SOUTH_GAP, y, centeredZ), size);
    }

    private static boolean containsMarkerInArea(World world, BlockPos origin, BlockPos size) {
        forceLoadTemplateChunks(world, origin, size);

        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    if (hasMarkerBlock(world, origin.add(x, y, z))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static void clearArea(World world, BlockPos origin, BlockPos size) {
        forceLoadTemplateChunks(world, origin, size);

        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    world.setBlockState(origin.add(x, y, z), Blocks.AIR.getDefaultState(), 2);
                }
            }
        }
    }

    private static void spawnVoidTraiderAtTraderCenter(World world, BlockPos traderStart, Template traderTemplate) {
        if (world.isRemote) {
            return;
        }

        double x = traderStart.getX() + traderTemplate.getSize().getX() / 2.0D + VOID_TRAIDER_SHIFT_X;
        double y = traderStart.getY() + 1.0D;
        double z = traderStart.getZ() + traderTemplate.getSize().getZ() / 2.0D + VOID_TRAIDER_SHIFT_Z;

        BlockPos spawnPos = new BlockPos(x, y, z);
        forceLoadChunks(world, spawnPos, 8);

        Entity oldVoidTraider = findVoidTraiderNear(world, x, y, z);
        if (oldVoidTraider != null) {
            oldVoidTraider.setLocationAndAngles(x, y, z, VOID_TRAIDER_YAW, 0.0F);
            fixVoidTraiderRotation(oldVoidTraider, VOID_TRAIDER_YAW);
            makeVoidTraiderStatic(oldVoidTraider);
            return;
        }

        EntityVoidTraider voidTraider = new EntityVoidTraider(world);
        voidTraider.setLocationAndAngles(x, y, z, VOID_TRAIDER_YAW, 0.0F);

        fixVoidTraiderRotation(voidTraider, VOID_TRAIDER_YAW);
        makeVoidTraiderStatic(voidTraider);

        world.spawnEntity(voidTraider);
        if (ModConfig.notifyVoidTraiderSpawn && world.getMinecraftServer() != null) {
            world.getMinecraftServer().getPlayerList().sendMessage(
                new TextComponentTranslation("chat.thaumicforever.void_trader_spawned")
            );
        }
    }

    private static Entity findVoidTraiderNear(World world, double x, double y, double z) {
        AxisAlignedBB box = new AxisAlignedBB(
            x - 8.0D, y - 4.0D, z - 8.0D,
            x + 8.0D, y + 4.0D, z + 8.0D
        );

        List<EntityVoidTraider> entities = world.getEntitiesWithinAABB(EntityVoidTraider.class, box);

        for (EntityVoidTraider entity : entities) {
            if (entity != null && !entity.isDead) {
                return entity;
            }
        }

        return null;
    }

    private static void fixVoidTraiderRotation(Entity entity, float yaw) {
        entity.rotationYaw = yaw;
        entity.prevRotationYaw = yaw;
        entity.rotationPitch = 0.0F;
        entity.prevRotationPitch = 0.0F;

        if (entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;

            living.rotationYaw = yaw;
            living.prevRotationYaw = yaw;

            living.rotationYawHead = yaw;
            living.prevRotationYawHead = yaw;

            living.renderYawOffset = yaw;
            living.prevRenderYawOffset = yaw;
        }
    }

    private static void makeVoidTraiderStatic(Entity entity) {
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
        entity.velocityChanged = true;
        entity.setNoGravity(true);

        if (entity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) entity;
            living.setNoAI(true);
            living.enablePersistence();
        }

        fixVoidTraiderRotation(entity, VOID_TRAIDER_YAW);
    }

    private static boolean hasMarkerBlock(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) {
            return false;
        }

        ResourceLocation blockName = world.getBlockState(pos).getBlock().getRegistryName();
        return VOID_TRADER_MARKER_BLOCK.equals(blockName);
    }

    private static final class RealBounds {
        private int minX;
        private int maxX;
        private int minZ;
        private int maxZ;

        private RealBounds(int x, int z) {
            this.minX = x;
            this.maxX = x;
            this.minZ = z;
            this.maxZ = z;
        }

        private void include(int x, int z) {
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (z < minZ) {
                minZ = z;
            }
            if (z > maxZ) {
                maxZ = z;
            }
        }

        private int getSizeX() {
            return maxX - minX + 1;
        }

        private int getSizeZ() {
            return maxZ - minZ + 1;
        }
    }
}
