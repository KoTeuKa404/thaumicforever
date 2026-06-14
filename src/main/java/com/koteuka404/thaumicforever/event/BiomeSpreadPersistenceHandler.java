package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.storage.BiomeSpreadWorldData;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BiomeSpreadPersistenceHandler {
    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (event.getWorld() == null || event.getWorld().isRemote) {
            return;
        }

        BiomeSpreadWorldData.get(event.getWorld()).applyToChunk(event.getWorld(), event.getChunk());
    }
}
