package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ThaumicEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST) // Пріоритет, щоб обробити івент раніше Botania
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        // Перевіряємо, чи гравець знаходиться на шифті
        if (event.getEntityPlayer().isSneaking()) {
            Block clickedBlock = event.getWorld().getBlockState(event.getPos()).getBlock();

            // Перевіряємо, чи це один із блоків, що використовуються Botania
            if (clickedBlock == Blocks.GRASS || clickedBlock == Blocks.DIRT || clickedBlock == Blocks.FARMLAND) {
                // Скасовуємо івент
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.FAIL);
            }
        }
    }
}
