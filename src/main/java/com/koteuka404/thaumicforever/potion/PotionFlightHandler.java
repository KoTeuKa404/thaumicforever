package com.koteuka404.thaumicforever.potion;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import com.koteuka404.thaumicforever.tile.TileEntityAntiFlightStone;
import com.koteuka404.thaumicforever.tile.TileEntityFlightStone;

public class PotionFlightHandler {
    private static final int ANTI_FLIGHT_RADIUS = 48;
    private static final double ANTI_FLIGHT_RADIUS_SQ = (double) ANTI_FLIGHT_RADIUS * (double) ANTI_FLIGHT_RADIUS;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        EntityPlayer player = event.player;
        if (player == null || player.world == null || player.world.isRemote) {
            return;
        }
        NBTTagCompound data = player.getEntityData();
        boolean hasFlightOwnedTag = data.getBoolean(PotionFlight.TAG_FLIGHT_OWNED);
        boolean fromFlightStone = data.getBoolean(TileEntityFlightStone.TAG_FLIGHT_FROM_STONE);
        boolean insideFlightField = TileEntityFlightStone.isPlayerInsideAnyFlightField(player.world, player, null);

        if (player.capabilities.isCreativeMode || player.isSpectator()) {
            if (!insideFlightField && player.isPotionActive(PotionFlight.INSTANCE) && (fromFlightStone || hasFlightOwnedTag)) {
                if (player.isPotionActive(PotionFlight.INSTANCE)) {
                    player.removePotionEffect(PotionFlight.INSTANCE);
                }
                PotionFlight.resetFlySpeed(player);
                PotionFlight.clearFlightTags(player);
                data.removeTag(TileEntityFlightStone.TAG_FLIGHT_FROM_STONE);
                return;
            }
            if ((hasFlightOwnedTag || fromFlightStone) && !player.isPotionActive(PotionFlight.INSTANCE)) {
                PotionFlight.resetFlySpeed(player);
            }
            PotionFlight.clearFlightTags(player);
            data.removeTag(TileEntityFlightStone.TAG_FLIGHT_FROM_STONE);
            return;
        }

        if (fromFlightStone && !insideFlightField) {
            if (!player.isPotionActive(PotionFlight.INSTANCE)) {
                PotionFlight.revokeFlight(player);
                data.removeTag(TileEntityFlightStone.TAG_FLIGHT_FROM_STONE);
                return;
            }
        }

        if (!data.getBoolean(PotionFlight.TAG_FLIGHT_OWNED)) {
            return;
        }

        if (player.isPotionActive(PotionFlight.INSTANCE)) {
            data.setInteger(PotionFlight.TAG_FLIGHT_GRACE, PotionFlight.DEFAULT_GRACE_TICKS);
            return;
        }

        int grace = data.getInteger(PotionFlight.TAG_FLIGHT_GRACE);
        if (grace > 0) {
            if (isInsideAntiFlightField(player)) {
                PotionFlight.revokeFlight(player);
                return;
            }
            data.setInteger(PotionFlight.TAG_FLIGHT_GRACE, grace - 1);
            player.fallDistance = 0.0F;
            if (!player.capabilities.allowFlying) {
                player.capabilities.allowFlying = true;
                player.sendPlayerAbilities();
            }
            return;
        }

        PotionFlight.revokeFlight(player);
    }

    private boolean isInsideAntiFlightField(EntityPlayer player) {
        BlockPos playerPos = player.getPosition();
        for (TileEntity te : player.world.loadedTileEntityList) {
            if (!(te instanceof TileEntityAntiFlightStone) || te.isInvalid() || te.getWorld() != player.world) {
                continue;
            }
            double dx = (double) te.getPos().getX() - (double) playerPos.getX();
            double dy = (double) te.getPos().getY() - (double) playerPos.getY();
            double dz = (double) te.getPos().getZ() - (double) playerPos.getZ();
            if (dx * dx + dy * dy + dz * dz <= ANTI_FLIGHT_RADIUS_SQ) {
                return true;
            }
        }
        return false;
    }
}
