package com.koteuka404.thaumicforever.registry;

import com.koteuka404.thaumicforever.ThaumicForever;

import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.koteuka404.thaumicforever.potion.PotionFlight;
import com.koteuka404.thaumicforever.potion.PotionVampirism;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public final class ModPotions {
    private ModPotions() {}

    @SubscribeEvent
    public static void onRegisterPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(PotionFlight.INSTANCE);
        event.getRegistry().register(PotionVampirism.INSTANCE);
    }
}
