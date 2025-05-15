package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityTimeSlow extends TileEntity implements ITickable {

    private static final int RADIUS = 3; 
    private static final int SLOWDOWN_INTERVAL = 3;

    private static final Map<BlockPos, Integer> slowRegistry = new HashMap<>();

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            addToRegistry();
        }
    }

    @Override
    public void onChunkUnload() {
        if (!world.isRemote) {
            removeFromRegistry();
        }
    }

    @Override
    public void invalidate() {
        if (!world.isRemote) {
            removeFromRegistry();
        }
        super.invalidate();
    }

    private void addToRegistry() {
        AxisAlignedBB area = new AxisAlignedBB(pos.add(-RADIUS, -RADIUS, -RADIUS), pos.add(RADIUS, RADIUS, RADIUS));
        for (BlockPos targetPos : BlockPos.getAllInBox(new BlockPos(area.minX, area.minY, area.minZ),
                                                       new BlockPos(area.maxX, area.maxY, area.maxZ))) {
            if (!slowRegistry.containsKey(targetPos)) {
                slowRegistry.put(targetPos, 0); 
            }
        }
    }

    private void removeFromRegistry() {
        AxisAlignedBB area = new AxisAlignedBB(pos.add(-RADIUS, -RADIUS, -RADIUS), pos.add(RADIUS, RADIUS, RADIUS));
        for (BlockPos targetPos : BlockPos.getAllInBox(new BlockPos(area.minX, area.minY, area.minZ),
                                                       new BlockPos(area.maxX, area.maxY, area.maxZ))) {
            slowRegistry.remove(targetPos); 
        }
    }

    @Override
    public void update() {
        if (!world.isRemote) {
        }
    }

    public static boolean shouldUpdate(BlockPos pos) {
        if (slowRegistry.containsKey(pos)) {
            int ticksSkipped = slowRegistry.get(pos);
            ticksSkipped++;
            if (ticksSkipped >= SLOWDOWN_INTERVAL) {
                slowRegistry.put(pos, 0);
                return true;
            } else {
                slowRegistry.put(pos, ticksSkipped); 
                return false; 
            }
        }
        return true; 
    }
}
