package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ThaumicEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST) 
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
   
        if (event.getEntityPlayer().isSneaking()) {
            Block clickedBlock = event.getWorld().getBlockState(event.getPos()).getBlock();

        
            if (clickedBlock == Blocks.GRASS || clickedBlock == Blocks.DIRT || clickedBlock == Blocks.FARMLAND) {
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.FAIL);
            }
        }
    }
}
