package com.koteuka404.thaumicforever;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
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
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static WorldTickHandler getInstance() {
        return INSTANCE;
    }

    public void addDungeonBounds(BlockPos startPos, BlockPos endPos) {
        synchronized (dungeonBounds) {
            dungeonBounds.add(new BoundingBox(startPos, endPos));
        }
    }
    public Set<BoundingBox> getAllDungeonBounds() {
        synchronized (dungeonBounds) {
            return new HashSet<>(dungeonBounds);
        }
    }
    
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            World world = event.world;
            if (world.isRemote) {
                return;
            }
    
            Set<BoundingBox> safeBounds;
            synchronized (dungeonBounds) {
                safeBounds = new HashSet<>(dungeonBounds);
            }

            for (BoundingBox box : safeBounds) {

                if (random.nextInt(4) == 0) {
                    spawnAngrySkeleton(world, box);
                }

                if (random.nextInt(6) == 0) { 
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

        } 
    }

    private void spawnReviveSkeletons(World world, BoundingBox box, int count) {
    for (int i = 0; i < count; i++) {
        BlockPos spawnPos = getBalancedSpawnPosition(box);
        if (isValidSpawnPosition(world, spawnPos)) {
            ReviveSkeletonEntity reviveSkeleton = new ReviveSkeletonEntity(world);
            reviveSkeleton.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);

            if (random.nextInt(100) < 5) { 
                int randomMeta = random.nextInt(4);
                ItemStack scroll = new ItemStack(ModItems.SCROLL_P, 1, randomMeta); 
                reviveSkeleton.setHeldItem(EnumHand.MAIN_HAND, scroll); 
            }

            world.spawnEntity(reviveSkeleton);
        }
    }
}


    private BlockPos getBalancedSpawnPosition(BoundingBox box) {
        int zoneCount = 4; 
        int zoneWidth = (box.getMaxX() - box.getMinX()) / zoneCount;
        int zoneIndex = random.nextInt(zoneCount);

        int x = box.getMinX() + zoneIndex * zoneWidth + random.nextInt(zoneWidth);
        int y = box.getMinY() + random.nextInt(box.getMaxY() - box.getMinY() + 1);
        int z = box.getMinZ() + random.nextInt(box.getMaxZ() - box.getMinZ() + 1);

        return new BlockPos(x, y, z);
    }

    private boolean isValidSpawnPosition(World world, BlockPos pos) {
        if (!world.isAirBlock(pos)) {
            return false;
        }
        if (!world.getBlockState(pos.down()).isFullBlock()) {
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
