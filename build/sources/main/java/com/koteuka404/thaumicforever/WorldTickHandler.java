package com.koteuka404.thaumicforever;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
            if (world.isRemote) return;

            Set<BoundingBox> safeBounds;
            synchronized (dungeonBounds) {
                safeBounds = new HashSet<>(dungeonBounds);
            }

            for (BoundingBox box : safeBounds) {
                if (random.nextInt(4) == 0) {
                    spawnAngrySkeleton(world, box);
                }

                if (random.nextInt(6) == 0) {
                    int count = 5 + random.nextInt(3); 
                    spawnReviveSkeletons(world, box, count);
                }
            }
        }
    }

    private void spawnAngrySkeleton(World world, BoundingBox box) {
        BlockPos spawnPos = findValidSpawnPosition(world, box, 16);
        if (spawnPos != null) {
            EntitySkeletonAngry angrySkeleton = new EntitySkeletonAngry(world);
            angrySkeleton.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
            world.spawnEntity(angrySkeleton);
        }
    }

    private void spawnReviveSkeletons(World world, BoundingBox box, int count) {
        for (int i = 0; i < count; i++) {
            BlockPos spawnPos = findValidSpawnPosition(world, box, 16);
            if (spawnPos != null) {
                ReviveSkeletonEntity reviveSkeleton = new ReviveSkeletonEntity(world);
                reviveSkeleton.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);

                if (random.nextInt(100) < 5) { // 5% шанс дати скрол
                    int randomMeta = random.nextInt(4);
                    ItemStack scroll = new ItemStack(ModItems.SCROLL_P, 1, randomMeta);
                    reviveSkeleton.setHeldItem(EnumHand.MAIN_HAND, scroll);
                }
                world.spawnEntity(reviveSkeleton);
            }
        }
    }

    private BlockPos findValidSpawnPosition(World world, BoundingBox box, int maxTries) {
        for (int tries = 0; tries < maxTries; tries++) {
            BlockPos pos = getBalancedSpawnPosition(box);
            if (!isValidSpawnPosition(world, pos)) continue;

            boolean visibleToAnyPlayer = false;
            for (EntityPlayer player : world.playerEntities) {
                if (!box.isVecInside(player.getPosition())) { // гравець в межах лабіринту
                    continue;
                }
                double distSq = player.getDistanceSq(pos);
                if (distSq < 36.0D) continue; // не дуже близько (мін. 6 блоків)

                if (canPlayerSeePos(world, player, pos)) {
                    visibleToAnyPlayer = true;
                    break;
                }
            }
            if (!visibleToAnyPlayer) return pos;
        }
        return null;
    }

    private boolean canPlayerSeePos(World world, EntityPlayer player, BlockPos target) {
        Vec3d eyes = player.getPositionEyes(1.0F);
        Vec3d vecTarget = new Vec3d(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        RayTraceResult result = world.rayTraceBlocks(eyes, vecTarget, false, true, false);
        return result == null || result.typeOfHit == RayTraceResult.Type.MISS;
    }

    private BlockPos getBalancedSpawnPosition(BoundingBox box) {
        int zoneCount = 4;
        int zoneWidth = Math.max(1, (box.getMaxX() - box.getMinX()) / zoneCount);
        int zoneIndex = random.nextInt(zoneCount);

        int x = box.getMinX() + zoneIndex * zoneWidth + random.nextInt(zoneWidth);
        int y = box.getMinY() + random.nextInt(box.getMaxY() - box.getMinY() + 1);
        int z = box.getMinZ() + random.nextInt(box.getMaxZ() - box.getMinZ() + 1);

        return new BlockPos(x, y, z);
    }

    private boolean isValidSpawnPosition(World world, BlockPos pos) {
        if (!world.isAirBlock(pos)) return false;
        if (!world.getBlockState(pos.down()).isFullBlock()) return false;
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
    public static void breakAllIronBarsInAllDungeons(World world) {
        System.out.println("BREAK ALL IRON BARS!");
        for (BoundingBox box : WorldTickHandler.getInstance().getAllDungeonBounds()) {
            // System.out.println("BBOX: " + box);
            breakAllIronBarsInBox(world, box);
        }
    }

    public static void breakAllIronBarsInBox(World world, BoundingBox box) {
        for (int x = box.getMinX(); x <= box.getMaxX(); x++) {
            for (int y = box.getMinY(); y <= box.getMaxY(); y++) {
                for (int z = box.getMinZ(); z <= box.getMaxZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();
                    if (block == Blocks.IRON_BARS) {
                        world.destroyBlock(pos, true);
                        // System.out.println("DESTROYED BARS AT: " + pos);
                    }
                }
            }
        }
    }
    public boolean isVecInside(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        return x >= getMinX() && x <= getMaxX()
            && y >= getMinY() && y <= getMaxY()
            && z >= getMinZ() && z <= getMaxZ();
    }
    
}
