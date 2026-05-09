package com.koteuka404.thaumicforever.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityAntiFlightStone extends TileEntity implements ITickable {

    private static final int RADIUS_BLOCKS = 48;
    private static final int CHECK_INTERVAL = 10;
    private static final double RADIUS_SQ = (double) RADIUS_BLOCKS * (double) RADIUS_BLOCKS;
    private AxisAlignedBB detectionBox = null;
    private int tickCounter = 0;

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }

        if (detectionBox == null) {
            detectionBox = new AxisAlignedBB(
                pos.add(-RADIUS_BLOCKS, -RADIUS_BLOCKS, -RADIUS_BLOCKS),
                pos.add(RADIUS_BLOCKS, RADIUS_BLOCKS, RADIUS_BLOCKS)
            );
        }

        if (tickCounter % CHECK_INTERVAL == 0) {
            List<EntityPlayer> players = world.playerEntities;
            for (EntityPlayer player : players) {
                if (player == null || player.isCreative()) {
                    continue;
                }

                boolean insideThisField = detectionBox.contains(player.getPositionVector());
                if (insideThisField) {
                    if (player.capabilities.allowFlying || player.capabilities.isFlying) {
                        player.capabilities.allowFlying = false;
                        player.capabilities.isFlying = false;
                        player.sendPlayerAbilities();
                    }
                } else if (!player.capabilities.allowFlying && !isPlayerInsideAnyAntiFlightField(player)) {
                    player.capabilities.allowFlying = true;
                    player.sendPlayerAbilities();
                }
            }
        }

        tickCounter++;
    }

    private boolean isPlayerInsideAnyAntiFlightField(EntityPlayer player) {
        BlockPos playerPos = player.getPosition();
        for (TileEntity te : world.loadedTileEntityList) {
            if (!(te instanceof TileEntityAntiFlightStone) || te.isInvalid() || te.getWorld() != world) {
                continue;
            }
            double dx = (double) te.getPos().getX() - (double) playerPos.getX();
            double dy = (double) te.getPos().getY() - (double) playerPos.getY();
            double dz = (double) te.getPos().getZ() - (double) playerPos.getZ();
            if (dx * dx + dy * dy + dz * dz <= RADIUS_SQ) {
                return true;
            }
        }
        return false;
    }
}
