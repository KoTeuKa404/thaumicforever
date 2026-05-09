package com.koteuka404.thaumicforever.world.dungeon;

import net.minecraft.util.math.BlockPos;

public class BoundingBox {
    private final BlockPos start;
    private final BlockPos end;

    public BoundingBox(BlockPos start, BlockPos end) {
        this.start = start;
        this.end = end;
    }

    public int getMinX() { return Math.min(start.getX(), end.getX()); }
    public int getMaxX() { return Math.max(start.getX(), end.getX()); }
    public int getMinY() { return Math.min(start.getY(), end.getY()); }
    public int getMaxY() { return Math.max(start.getY(), end.getY()); }
    public int getMinZ() { return Math.min(start.getZ(), end.getZ()); }
    public int getMaxZ() { return Math.max(start.getZ(), end.getZ()); }

    public boolean isVecInside(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        return x >= getMinX() && x <= getMaxX()
            && y >= getMinY() && y <= getMaxY()
            && z >= getMinZ() && z <= getMaxZ();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoundingBox)) return false;
        BoundingBox b = (BoundingBox) o;
        return getMinX() == b.getMinX() && getMinY() == b.getMinY() && getMinZ() == b.getMinZ()
            && getMaxX() == b.getMaxX() && getMaxY() == b.getMaxY() && getMaxZ() == b.getMaxZ();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getMinX();
        result = 31 * result + getMinY();
        result = 31 * result + getMinZ();
        result = 31 * result + getMaxX();
        result = 31 * result + getMaxY();
        result = 31 * result + getMaxZ();
        return result;
    }
}
