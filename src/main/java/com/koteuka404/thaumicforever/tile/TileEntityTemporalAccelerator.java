package com.koteuka404.thaumicforever.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityTemporalAccelerator extends TileEntity implements ITickable {

    private static final int RADIUS = 5;
    private static final int BONUS_TICKS = 300;

    @Override
    public void update() {
        if (world == null || world.isRemote || hasAnotherAccelerator()) {
            return;
        }

        // Work on a snapshot because accelerated machines may add or remove tile entities.
        List<TileEntity> loadedTiles = new ArrayList<>(world.loadedTileEntityList);
        for (TileEntity tile : loadedTiles) {
            if (!canAccelerate(tile)) {
                continue;
            }

            for (int tick = 0; tick < BONUS_TICKS && !tile.isInvalid(); tick++) {
                try {
                    ((ITickable) tile).update();
                } catch (Exception ignored) {
                    break;
                }
            }
        }
    }

    private boolean hasAnotherAccelerator() {
        for (TileEntity tile : world.loadedTileEntityList) {
            if (tile != this && tile instanceof TileEntityTemporalAccelerator && isInRange(tile)) {
                return true;
            }
        }
        return false;
    }

    private boolean canAccelerate(TileEntity tile) {
        return tile != this
                && tile instanceof ITickable
                && !(tile instanceof TileEntityTemporalAccelerator)
                && !(tile instanceof TileEntityTimeStone)
                && isInRange(tile);
    }

    private boolean isInRange(TileEntity tile) {
        return Math.abs(tile.getPos().getX() - pos.getX()) <= RADIUS
                && Math.abs(tile.getPos().getY() - pos.getY()) <= RADIUS
                && Math.abs(tile.getPos().getZ() - pos.getZ()) <= RADIUS;
    }
}
