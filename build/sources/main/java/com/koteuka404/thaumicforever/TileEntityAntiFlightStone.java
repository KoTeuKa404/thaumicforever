package com.koteuka404.thaumicforever;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityAntiFlightStone extends TileEntity implements ITickable {

    private static final int RADIUS_BLOCKS = 48;
    private static final int CHECK_INTERVAL = 1; 
    private AxisAlignedBB detectionBox = null;
    private int tickCounter = 0;
    private final Set<BlockPos> cachedAntiFlightStones = new HashSet<>();

    @Override
    public void update() {
        if (!world.isRemote) {
            if (detectionBox == null) {
                detectionBox = new AxisAlignedBB(
                    pos.add(-RADIUS_BLOCKS, -RADIUS_BLOCKS, -RADIUS_BLOCKS),
                    pos.add(RADIUS_BLOCKS, RADIUS_BLOCKS, RADIUS_BLOCKS)
                );
            }

            if (tickCounter % CHECK_INTERVAL == 0) {
                updateNearbyAntiFlightStones();
            }

            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, detectionBox);
            boolean hasNearbyAntiFlightBlocks = !cachedAntiFlightStones.isEmpty();

            for (EntityPlayer player : players) {
                if (!player.isCreative() && player.capabilities.allowFlying) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();
                } else if (!player.isCreative() && !hasNearbyAntiFlightBlocks && !player.capabilities.allowFlying) {
                    player.capabilities.allowFlying = true;
                    player.sendPlayerAbilities();
                }
            }

            tickCounter++;
        }
    }

    private void updateNearbyAntiFlightStones() {
        cachedAntiFlightStones.clear();
        for (BlockPos checkPos : BlockPos.getAllInBoxMutable(
                pos.add(-RADIUS_BLOCKS, -RADIUS_BLOCKS, -RADIUS_BLOCKS),
                pos.add(RADIUS_BLOCKS, RADIUS_BLOCKS, RADIUS_BLOCKS))) {

            TileEntity te = world.getTileEntity(checkPos);
            if (te instanceof TileEntityAntiFlightStone && !checkPos.equals(this.pos)) {
                cachedAntiFlightStones.add(checkPos.toImmutable());
            }
        }
    }
}
