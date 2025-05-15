package com.koteuka404.thaumicforever;

import java.util.HashSet;
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

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            synchronized (processedTiles) {
                processedTiles.clear(); 
            }
        }
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (isOtherTimeStoneActive()) {
                return; 
            }

            AxisAlignedBB area = new AxisAlignedBB(pos.add(-RADIUS, -RADIUS, -RADIUS), pos.add(RADIUS, RADIUS, RADIUS));

            synchronized (processedTiles) {
                processedTiles.clear(); 
                speedUpTileEntities(world, BONUS_TICKS, area);
            }
        }
    }

    private boolean isOtherTimeStoneActive() {
        AxisAlignedBB checkArea = new AxisAlignedBB(pos.add(-RADIUS, -RADIUS, -RADIUS), pos.add(RADIUS, RADIUS, RADIUS));
    
        for (BlockPos targetPos : BlockPos.getAllInBox(new BlockPos(checkArea.minX, checkArea.minY, checkArea.minZ),
                                                       new BlockPos(checkArea.maxX, checkArea.maxY, checkArea.maxZ))) {
            TileEntity tile = world.getTileEntity(targetPos);
            if (tile instanceof TileEntityTimeStone && tile != this) {
                return true; 
            }
        }
        return false;
    }
    

    private void speedUpTileEntities(World world, int bonusTicks, AxisAlignedBB area) {
        BlockPos.getAllInBox(new BlockPos(area.minX, area.minY, area.minZ),
                             new BlockPos(area.maxX, area.maxY, area.maxZ))
                .forEach(targetPos -> {
                    TileEntity tile = world.getTileEntity(targetPos);

                    if (tile instanceof ITickable && tile != this && !(tile instanceof TileEntityTimeStone)) {
                        synchronized (processedTiles) {
                            if (processedTiles.add(tile)) {
                                for (int i = 0; i < bonusTicks; i++) {
                                    try {
                                        ((ITickable) tile).update();
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    }
                });
    }
}
