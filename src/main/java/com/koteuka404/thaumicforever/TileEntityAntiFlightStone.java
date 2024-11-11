package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityAntiFlightStone extends TileEntity implements ITickable {

    private static final int RADIUS_BLOCKS = 48;

    private AxisAlignedBB detectionBox = null;

    @Override
    public void update() {
        if (!world.isRemote) {
            if (detectionBox == null) {
                detectionBox = new AxisAlignedBB(
                    pos.add(-RADIUS_BLOCKS, -RADIUS_BLOCKS, -RADIUS_BLOCKS), 
                    pos.add(RADIUS_BLOCKS, RADIUS_BLOCKS, RADIUS_BLOCKS)
                );
            }

            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, detectionBox);

            for (EntityPlayer player : players) {
                if (player.capabilities.allowFlying) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();
                }
            }
        }
    }
}