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

    public BoundingBox getBox() {
        return box;
    }

    public String getName() {
        return name;
    }

    public BlockPos getCenter() {
        return new BlockPos(
            (box.getMinX() + box.getMaxX()) / 2,
            (box.getMinY() + box.getMaxY()) / 2,
            (box.getMinZ() + box.getMaxZ()) / 2
        );
    }

    public static MazeDungeonWrapper findClosest(World world, BlockPos from) {
        Set<BoundingBox> bounds = WorldTickHandler.getInstance().getAllDungeonBounds();

        BoundingBox closestBox = null;
        double minDistSq = Double.MAX_VALUE;

        for (BoundingBox box : bounds) {
            BlockPos center = new BlockPos(
                (box.getMinX() + box.getMaxX()) / 2,
                (box.getMinY() + box.getMaxY()) / 2,
                (box.getMinZ() + box.getMaxZ()) / 2
            );

            double distSq = center.distanceSq(from);
            if (distSq < minDistSq) {
                minDistSq = distSq;
                closestBox = box;
            }
        }

        return closestBox != null ? new MazeDungeonWrapper(closestBox, "Таємничий лабіринт") : null;
    }

    @Override
    public String toString() {
        return name + " at " + getCenter();
    }
}
