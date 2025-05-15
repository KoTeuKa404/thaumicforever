package com.koteuka404.thaumicforever;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModSounds {
    public static final SoundEvent COINS = new SoundEvent(new ResourceLocation("thaumicforever", "coins")).setRegistryName("coins");

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(COINS);
    }
}
