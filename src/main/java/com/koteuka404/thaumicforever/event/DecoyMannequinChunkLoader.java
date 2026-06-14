package com.koteuka404.thaumicforever.event;

import java.util.List;

import com.koteuka404.thaumicforever.entity.EntityDecoyMannequin;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

public class DecoyMannequinChunkLoader implements ForgeChunkManager.LoadingCallback {
    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
        for (ForgeChunkManager.Ticket ticket : tickets) {
            Entity entity = ticket.getEntity();
            if (entity instanceof EntityDecoyMannequin) {
                ((EntityDecoyMannequin) entity).restoreChunkTicket(ticket);
            } else {
                ForgeChunkManager.releaseTicket(ticket);
            }
        }
    }
}
