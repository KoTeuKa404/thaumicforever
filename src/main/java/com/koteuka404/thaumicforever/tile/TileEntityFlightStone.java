package com.koteuka404.thaumicforever.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.koteuka404.thaumicforever.potion.PotionFlight;

public class TileEntityFlightStone extends TileEntity implements ITickable {

    public static final String TAG_FLIGHT_FROM_STONE = "thaumicforever_flightstone_source";
    private static final int RADIUS_BLOCKS = 48;
    private static final double RADIUS_SQ = (double) RADIUS_BLOCKS * (double) RADIUS_BLOCKS;
    private static final int CHECK_INTERVAL = 10;
    private static final int EFFECT_DURATION_TICKS = 40;
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
            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, detectionBox);
            for (EntityPlayer player : players) {
                player.addPotionEffect(new PotionEffect(PotionFlight.INSTANCE, EFFECT_DURATION_TICKS, 0, true, true));
                player.getEntityData().setBoolean(TAG_FLIGHT_FROM_STONE, true);
            }
        }

        tickCounter++;
    }

    public static void cleanupAfterBreak(World world, BlockPos removedPos) {
        if (world == null || world.isRemote || removedPos == null) {
            return;
        }

        AxisAlignedBB box = new AxisAlignedBB(
            removedPos.add(-RADIUS_BLOCKS, -RADIUS_BLOCKS, -RADIUS_BLOCKS),
            removedPos.add(RADIUS_BLOCKS, RADIUS_BLOCKS, RADIUS_BLOCKS)
        );

        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, box);
        for (EntityPlayer player : players) {
            if (player == null) {
                continue;
            }

            if (!player.getEntityData().getBoolean(TAG_FLIGHT_FROM_STONE)) {
                continue;
            }

            if (isPlayerInsideAnyFlightField(world, player, removedPos)) {
                continue;
            }

            player.removePotionEffect(PotionFlight.INSTANCE);
            if (player.capabilities.isCreativeMode || player.isSpectator()) {
                PotionFlight.resetFlySpeed(player);
                PotionFlight.clearFlightTags(player);
            } else {
                PotionFlight.revokeFlight(player);
            }
            player.getEntityData().removeTag(TAG_FLIGHT_FROM_STONE);
        }
    }

    public static boolean isPlayerInsideAnyFlightField(World world, EntityPlayer player, BlockPos ignorePos) {
        if (world == null || player == null) return false;
        BlockPos playerPos = player.getPosition();
        for (TileEntity te : world.loadedTileEntityList) {
            if (!(te instanceof TileEntityFlightStone) || te.isInvalid() || te.getWorld() != world) {
                continue;
            }
            if (ignorePos != null && ignorePos.equals(te.getPos())) {
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
