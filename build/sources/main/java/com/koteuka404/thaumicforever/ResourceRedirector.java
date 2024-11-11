// package com.koteuka404.thaumicforever;

// import java.io.File;

// import net.minecraft.client.Minecraft;
// import net.minecraft.client.resources.SimpleReloadableResourceManager;
// import net.minecraftforge.fml.common.Mod;
// import net.minecraftforge.fml.common.event.FMLInitializationEvent;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// @Mod.EventBusSubscriber(modid = "thaumicforever")
// public class ResourceRedirector {

//     @SubscribeEvent
//     public static void onInitialization(FMLInitializationEvent event) {
//         SimpleReloadableResourceManager resourceManager = (SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();

//         // Реєстрація кастомного ресурсного паку
//         Minecraft.getMinecraft().getResourcePackRepository().addPackFinder((consumer, factory) -> {
//             consumer.accept(new CustomResourcePack(new File("src/main/resources")));
//         });
//     }
// }
