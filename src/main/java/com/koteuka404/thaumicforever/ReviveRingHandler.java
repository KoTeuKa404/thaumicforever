package com.koteuka404.thaumicforever;

import java.util.ArrayList;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReviveRingHandler {
    private static final int COOLDOWN_TICKS = 5 * 60 * 20;

    public ReviveRingHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (player.world.isRemote) return;

        if (event.getAmount() < player.getHealth()) return;

        IBaublesItemHandler inv = BaublesApi.getBaublesHandler(player);
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof ItemRingRevive)) continue;

            Item ring = stack.getItem();
            if (player.getCooldownTracker().hasCooldown(ring)) continue;

            event.setCanceled(true);

            for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
                if (effect.getPotion().isBadEffect()) {
                    player.removePotionEffect(effect.getPotion());
                }
            }

            player.setHealth(1.0f);

            player.getCooldownTracker().setCooldown(ring, COOLDOWN_TICKS);

            player.world.playSound(
                null,
                player.getPosition(),
                SoundEvents.ITEM_TOTEM_USE,
                SoundCategory.PLAYERS,
                1.0f, 1.0f
            );

            return;  
        }
    }
}
