package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class RainCauldronFiller {

    private static final int CHECK_INTERVAL = 100; 
    private int tickCounter = 0;

    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event) {
        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) {
            return;
        }
        tickCounter = 0; 

        World world = event.world;

        if (world.isRaining()) {
            world.playerEntities.forEach(player -> {
                BlockPos playerPos = player.getPosition();
                Random random = world.rand;

                for (int i = 0; i < 5; i++) { 
                    int xOffset = random.nextInt(21) - 10;
                    int zOffset = random.nextInt(21) - 10;
                    BlockPos cauldronPos = playerPos.add(xOffset, 0, zOffset);
                    IBlockState state = world.getBlockState(cauldronPos);

                    if (state.getBlock() instanceof BlockCauldron) {
                        int level = state.getValue(BlockCauldron.LEVEL);

                        if (level < 3) {
                            if (random.nextInt(100) < 90) { 
                                world.setBlockState(cauldronPos, state.withProperty(BlockCauldron.LEVEL, level + 1), 2);
                            }
                        }
                    }
                }
            });
        }
    }
}
