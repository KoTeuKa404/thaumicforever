package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityTimeStone extends TileEntity implements ITickable {

    private static final int RADIUS = 3;
    private static final int BONUS_TICKS = 3;

    private static final Set<TileEntity> processedTiles = new HashSet<>();
    private static final List<TileEntityTimeStone> activeTimeStones = new ArrayList<>();
    private static final ThreadLocal<Integer> tickDepth = ThreadLocal.withInitial(() -> 0);

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            activeTimeStones.add(this);
        }
    }

    @Override
    public void onChunkUnload() {
        if (!world.isRemote) {
            activeTimeStones.remove(this);
        }
    }

    @Override
    public void invalidate() {
        if (!world.isRemote) {
            activeTimeStones.remove(this);
        }
        super.invalidate();
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            int depth = tickDepth.get();
            if (depth > 10) {
                return;
            }
            tickDepth.set(depth + 1);

            if (activeTimeStones.isEmpty() || activeTimeStones.get(0) != this) {
                tickDepth.set(depth);
                return; 
            }

            processedTiles.clear(); 
            AxisAlignedBB area = new AxisAlignedBB(pos.add(-RADIUS, -RADIUS, -RADIUS), pos.add(RADIUS, RADIUS, RADIUS));
            speedUpTileEntities(world, BONUS_TICKS, area);

            tickDepth.set(depth);
        }
    }

    private void speedUpTileEntities(World world, int bonusTicks, AxisAlignedBB area) {
        for (BlockPos targetPos : BlockPos.getAllInBox(new BlockPos(area.minX, area.minY, area.minZ),
                                                       new BlockPos(area.maxX, area.maxY, area.maxZ))) {
            TileEntity tile = world.getTileEntity(targetPos);
            if (tile instanceof ITickable && tile != this && !processedTiles.contains(tile)) {
                processedTiles.add(tile); 
                for (int i = 0; i < bonusTicks; i++) {
                    ((ITickable) tile).update();
                }
            }
        }
    }
}
