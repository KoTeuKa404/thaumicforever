package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.blocks.BlocksTC;

public class MannequinUnfreezeHandler {

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (world.isRemote) return;
        if (event.phase != TickEvent.Phase.END) return;
        if (world.getTotalWorldTime() % 10 != 0) return;

        int pedestalRadius = 5;

        for (EntityPlayer player : world.playerEntities) {
            if (player.isSpectator() || player.isCreative()) continue;

            BlockPos playerPos = player.getPosition();
            boolean foundPedestal = false;
            BlockPos pedestalPos = null;

            search:
            for (int dx = -pedestalRadius; dx <= pedestalRadius; dx++) {
                for (int dy = -pedestalRadius; dy <= pedestalRadius; dy++) {
                    for (int dz = -pedestalRadius; dz <= pedestalRadius; dz++) {
                        BlockPos check = playerPos.add(dx, dy, dz);
                        if (world.getBlockState(check).getBlock() == BlocksTC.pedestalArcane) {
                            foundPedestal = true;
                            pedestalPos = check;
                            break search;
                        }
                    }
                }
            }

            if (foundPedestal && pedestalPos != null) {
                AxisAlignedBB aabb = new AxisAlignedBB(
                    pedestalPos.add(-pedestalRadius, -pedestalRadius, -pedestalRadius),
                    pedestalPos.add(pedestalRadius + 1, pedestalRadius + 1, pedestalRadius + 1)
                );
                List<EntityGuardianMannequin> mannequins = world.getEntitiesWithinAABB(
                    EntityGuardianMannequin.class, aabb,
                    m -> m.isHostile() && !m.wasActivatedByPedestal()
                );
                for (EntityGuardianMannequin mannequin : mannequins) {
                    mannequin.setNoAI(false);
                    mannequin.setWasActivatedByPedestal(true);

                    DungeonUtil.breakAllIronBarsInAllDungeons(world);
                }
            }
        }
    }
}
