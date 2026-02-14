package com.koteuka404.thaumicforever;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MazeDungeonWrapper {
    private final BoundingBox box;
    private final String name;

    public MazeDungeonWrapper(BoundingBox box, String name) {
        this.box = box;
        this.name = name;
    }

    public BlockPos getOrigin() {
        return new BlockPos(box.getMinX(), box.getMinY(), box.getMinZ());
    }

    public BlockPos getCenter() {
        return new BlockPos(
            (box.getMinX() + box.getMaxX()) / 2,
            (box.getMinY() + box.getMaxY()) / 2,
            (box.getMinZ() + box.getMaxZ()) / 2
        );
    }

    public String getName() {
        return name;
    }

    private static java.util.Set<BoundingBox> getAllBoxesMerged(World world) {
        java.util.Set<BoundingBox> merged = new java.util.HashSet<>();
        merged.addAll(WorldTickHandler.getInstance().getAllDungeonBounds());
        DungeonBoundsData data = DungeonBoundsData.get(world);
        if (data != null) merged.addAll(data.getBoxes());
        return merged;
    }

    public static boolean isInMaze(World world, BlockPos pos) {
        for (BoundingBox box : getAllBoxesMerged(world)) {
            if (box.isVecInside(pos)) return true;
        }
        return false;
    }

    public static MazeDungeonWrapper findClosest(World world, BlockPos from) {
        BoundingBox closest = null;
        double minDistSq = Double.MAX_VALUE;

        for (BoundingBox b : getAllBoxesMerged(world)) {
            BlockPos origin = new BlockPos(b.getMinX(), b.getMinY(), b.getMinZ());
            double distSq = origin.distanceSq(from);
            if (distSq < minDistSq) {
                minDistSq = distSq;
                closest = b;
            }
        }

        if (closest != null) {
            return new MazeDungeonWrapper(closest, "Mystery Maze");
        }
        return null;
    }

    @Override
    public String toString() {
        BlockPos o = getOrigin();
        return name + " at " + o.getX() + ", " + o.getY() + ", " + o.getZ();
    }
}
