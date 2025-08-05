// package com.koteuka404.thaumicforever;

// import net.minecraft.entity.Entity;
// import net.minecraft.entity.player.EntityPlayer;
// import net.minecraft.util.ResourceLocation;
// import net.minecraftforge.common.MinecraftForge;
// import net.minecraftforge.event.AttachCapabilitiesEvent;
// import net.minecraftforge.fml.common.Mod;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// @Mod.EventBusSubscriber
// public class CapabilityHandler {

//     private static final ResourceLocation CUSTOM_BAUBLES_CAP =
//         new ResourceLocation("thaumicforever", "custom_baubles");

//     public static void register() {
//         MinecraftForge.EVENT_BUS.register(CapabilityHandler.class);
//     }

//     @SubscribeEvent
//     public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
//         if (event.getObject() instanceof EntityPlayer) {
//             event.addCapability(
//                 new ResourceLocation("thaumicforever", "custom_baubles"),
//                 new CustomBaublesProvider((EntityPlayer) event.getObject())
//             );
//         }
//     }
    
// }
