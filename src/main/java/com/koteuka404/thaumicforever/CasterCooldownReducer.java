package com.koteuka404.thaumicforever;

import java.lang.reflect.Field;
import java.util.Map;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.common.items.casters.CasterManager;

public class CasterCooldownReducer {

    private static final double REDUCTION_PER_RING = 0.03;

    public CasterCooldownReducer() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) return;
        EntityPlayer player = event.player;

        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        int count = 0;
        for (int slot = 0; slot < baubles.getSlots(); slot++) {
            ItemStack bs = baubles.getStackInSlot(slot);
            if (!bs.isEmpty() && bs.getItem() instanceof ItemRingCooldown) { 
                count++;
            }
        }
        if (count <= 0) return;

        try {
            Field f = CasterManager.class.getDeclaredField("cooldownServer");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Integer, Long> cooldownServer = (Map<Integer, Long>) f.get(null);

            int id = player.getEntityId();
            if (!cooldownServer.containsKey(id)) return;

            long expireTime = cooldownServer.get(id);
            long remaining  = expireTime - System.currentTimeMillis();
            if (remaining <= 0) {
                return;
            }

            double factor = Math.pow(1.0 - REDUCTION_PER_RING, count);
            long newRemaining = (long) Math.ceil(remaining * factor);

            cooldownServer.put(id, System.currentTimeMillis() + newRemaining);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}