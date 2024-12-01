package com.koteuka404.thaumicforever;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class WorldTickHandler {

    private static final WorldTickHandler INSTANCE = new WorldTickHandler();
    private final Set<BoundingBox> dungeonBounds = new HashSet<>();
    private final Random random = new Random();

    private WorldTickHandler() {
        System.out.println("[DEBUG] WorldTickHandler initialized!");
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static WorldTickHandler getInstance() {
        return INSTANCE;
    }

    public void addDungeonBounds(BlockPos startPos, BlockPos endPos) {
        synchronized (dungeonBounds) {
            dungeonBounds.add(new BoundingBox(startPos, endPos));
        }
        System.out.println("[DEBUG] Added dungeon bounds: Start=" + startPos + ", End=" + endPos);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            World world = event.world;

            // Створюємо копію колекції для безпечної ітерації
            Set<BoundingBox> safeBounds;
            synchronized (dungeonBounds) {
                safeBounds = new HashSet<>(dungeonBounds);
            }

            for (BoundingBox box : safeBounds) {
                System.out.println("[DEBUG] Checking dungeon bounds: " + box);

                // Спавн Angry Skeletons по одному
                if (random.nextInt(4) == 0) { // 25% шанс
                    spawnAngrySkeleton(world, box);
                }

                // Спавн груп Revive Skeletons
                if (random.nextInt(6) == 0) { // 16.7% шанс
                    spawnReviveSkeletons(world, box, 3);
                }
            }
        }
    }

    private void spawnAngrySkeleton(World world, BoundingBox box) {
        BlockPos spawnPos = getBalancedSpawnPosition(box);
        if (isValidSpawnPosition(world, spawnPos)) {
            EntitySkeletonAngry angrySkeleton = new EntitySkeletonAngry(world);
            angrySkeleton.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
            world.spawnEntity(angrySkeleton);

            System.out.println("[DEBUG] Angry Skeleton spawned at: " + spawnPos);
        } else {
            System.out.println("[DEBUG] Invalid position for Angry Skeleton at: " + spawnPos);
        }
    }

    private void spawnReviveSkeletons(World world, BoundingBox box, int count) {
        for (int i = 0; i < count; i++) {
            BlockPos spawnPos = getBalancedSpawnPosition(box);
            if (isValidSpawnPosition(world, spawnPos)) {
                ReviveSkeletonEntity reviveSkeleton = new ReviveSkeletonEntity(world);
                reviveSkeleton.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
                world.spawnEntity(reviveSkeleton);

                System.out.println("[DEBUG] Revive Skeleton spawned at: " + spawnPos);
            } else {
                System.out.println("[DEBUG] Invalid position for Revive Skeleton at: " + spawnPos);
            }
        }
    }

    private BlockPos getBalancedSpawnPosition(BoundingBox box) {
        int zoneCount = 4; // Ділимо данж на 4 рівні зони
        int zoneWidth = (box.getMaxX() - box.getMinX()) / zoneCount;
        int zoneIndex = random.nextInt(zoneCount); // Обираємо випадкову зону

        int x = box.getMinX() + zoneIndex * zoneWidth + random.nextInt(zoneWidth);
        int y = box.getMinY() + random.nextInt(box.getMaxY() - box.getMinY() + 1); // Обмежуємо по Y
        int z = box.getMinZ() + random.nextInt(box.getMaxZ() - box.getMinZ() + 1);

        return new BlockPos(x, y, z);
    }

    private boolean isValidSpawnPosition(World world, BlockPos pos) {
        if (!world.isAirBlock(pos)) {
            System.out.println("[DEBUG] Block is not air at: " + pos);
            return false;
        }
        if (!world.getBlockState(pos.down()).isFullBlock()) {
            System.out.println("[DEBUG] Block below is not solid at: " + pos.down());
            return false;
        }
        return true;
    }
}

class BoundingBox {
    private final BlockPos start;
    private final BlockPos end;

    public BoundingBox(BlockPos start, BlockPos end) {
        this.start = start;
        this.end = end;
    }

    public int getMinX() {
        return Math.min(start.getX(), end.getX());
    }

    public int getMaxX() {
        return Math.max(start.getX(), end.getX());
    }

    public int getMinY() {
        return Math.min(start.getY(), end.getY());
    }

    public int getMaxY() {
        return Math.max(start.getY(), end.getY());
    }

    public int getMinZ() {
        return Math.min(start.getZ(), end.getZ());
    }

    public int getMaxZ() {
        return Math.max(start.getZ(), end.getZ());
    }

    @Override
    public String toString() {
        return "BoundingBox{start=" + start + ", end=" + end + "}";
    }
}
