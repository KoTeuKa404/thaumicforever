package com.koteuka404.thaumicforever;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RegenRingHandler {

    private static final int INTERVAL_TICKS = 13 * 20;
    private static final int DURATION_TICKS = 3 * 20;
    private static final int MIN_LEVEL      = 1;  

    public RegenRingHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) return;

        long worldTime = event.player.world.getTotalWorldTime();
        if (worldTime % INTERVAL_TICKS != 0) return;

        EntityPlayer player = event.player;
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

        int count = 0;
        for (int slot = 0; slot < baubles.getSlots(); slot++) {
            if (!baubles.getStackInSlot(slot).isEmpty() &&
                baubles.getStackInSlot(slot).getItem() instanceof ItemRingRegeneration) {
                count++;
            }
        }

        if (count > 0) {
            int level = Math.max(MIN_LEVEL, count);
            player.addPotionEffect(new PotionEffect(
                MobEffects.REGENERATION,
                DURATION_TICKS,
                level - 1,
                false,  
                true    
            ));
        }
    }
}
