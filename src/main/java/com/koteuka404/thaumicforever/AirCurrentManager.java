// package com.koteuka404.thaumicforever;

// import java.util.Random;

// import net.minecraft.entity.player.EntityPlayer;
// import net.minecraft.util.math.BlockPos;
// import net.minecraft.util.math.Vec3d;
// import net.minecraft.world.World;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
// import net.minecraftforge.fml.common.gameevent.TickEvent;

// public class AirCurrentManager {

//     private static final Random random = new Random();
//     private static final int WIND_HEIGHT_THRESHOLD = 110; 
//     private static final int WIND_INTERVAL = 300; 
//     private static int windCooldown = WIND_INTERVAL;

//     @SubscribeEvent
//     public static void onWorldTick(TickEvent.WorldTickEvent event) {
//         World world = event.world;
//         if (world.isRemote) return; 

//         windCooldown--;

//         if (windCooldown <= 0) {
//             windCooldown = WIND_INTERVAL;
//             spawnWindNearPlayers(world);
//         }
//     }

//     private static void spawnWindNearPlayers(World world) {
//         for (EntityPlayer player : world.playerEntities) {
//             if (player.posY >= WIND_HEIGHT_THRESHOLD) {
//                 Vec3d lookVec = player.getLookVec();
                
//                 double distance = 5 + random.nextDouble() * 5; 
//                 double angle = (random.nextDouble() - 0.5) * Math.PI / 2; 
                
//                 double offsetX = lookVec.x * distance * Math.cos(angle) - lookVec.z * distance * Math.sin(angle);
//                 double offsetZ = lookVec.z * distance * Math.cos(angle) + lookVec.x * distance * Math.sin(angle);
//                 double offsetY = -1 + random.nextInt(3); 
//                 double x = player.posX + offsetX;
//                 double y = player.posY + offsetY;
//                 double z = player.posZ + offsetZ;

//                 EntityAirCurrent wind = new EntityAirCurrent(world, x, y, z);
//                 world.spawnEntity(wind);
//                 System.out.println("Wind spawned at: " + new BlockPos(x, y, z));
//             }
//         }
//     }

// }
