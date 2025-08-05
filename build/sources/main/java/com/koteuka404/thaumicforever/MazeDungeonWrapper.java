package com.koteuka404.thaumicforever;

import java.util.Set;

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
        return new BlockPos(
            box.getMinX(),
            box.getMinY(),
            box.getMinZ()
        );
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
    public static boolean isInMaze(World world, BlockPos pos) {
        Set<BoundingBox> bounds = WorldTickHandler.getInstance().getAllDungeonBounds();
        for (BoundingBox box : bounds) {
            if (box.isVecInside(pos)) {
                return true;
            }
        }
        return false;
    }
    
 
    public static MazeDungeonWrapper findClosest(World world, BlockPos from) {
        Set<BoundingBox> bounds = WorldTickHandler.getInstance().getAllDungeonBounds();
        BoundingBox closest = null;
        double minDistSq = Double.MAX_VALUE;

        for (BoundingBox b : bounds) {
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
