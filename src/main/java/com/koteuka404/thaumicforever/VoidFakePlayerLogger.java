// package com.koteuka404.thaumicforever;

// import net.minecraft.world.World;
// import net.minecraftforge.event.world.BlockEvent;
// import net.minecraftforge.fml.common.Mod;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// @Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
// public class VoidFakePlayerLogger {

//     @SubscribeEvent
//     public static void onAnyBreak(BlockEvent.BreakEvent evt) {
//         World world = evt.getWorld();
//         System.out.println(String.format(
//             "[Debug ANY BreakEvent] dim=%d pos=%s player=%s",
//             world.provider.getDimension(),
//             evt.getPos(),
//             evt.getPlayer() == null ? "<none>" : evt.getPlayer().getName()
//         ));
//     }

//     @SubscribeEvent
//     public static void onAnyHarvest(BlockEvent.HarvestDropsEvent evt) {
//         World world = evt.getWorld();
//         System.out.println(String.format(
//             "[Debug ANY HarvestDropsEvent] dim=%d pos=%s drops=%d",
//             world.provider.getDimension(),
//             evt.getPos(),
//             evt.getDrops().size()
//         ));
//     }
// }
