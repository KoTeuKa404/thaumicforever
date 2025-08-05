// package com.koteuka404.thaumicforever;

// import net.minecraft.block.Block;
// import net.minecraft.block.state.IBlockState;
// import net.minecraft.entity.player.EntityPlayer;
// import net.minecraft.util.math.BlockPos;
// import net.minecraft.world.MinecraftException;
// import net.minecraft.world.WorldServer;
// import net.minecraftforge.common.MinecraftForge;
// import net.minecraftforge.common.util.FakePlayer;
// import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
// import net.minecraftforge.fml.common.Mod;
// import net.minecraftforge.fml.common.event.FMLInitializationEvent;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// @Mod(modid = ThaumicForever.MODID, name = ThaumicForever.NAME, version = ThaumicForever.VERSION)
// public class VoidForceSaveHandler {

//     @Mod.EventHandler
//     public void init(FMLInitializationEvent event) {
//         MinecraftForge.EVENT_BUS.register(this);
//         System.out.println("[ThaumicForever] VoidForceSaveHandler registered");
//     }

//     @SubscribeEvent
//     public void onHarvestDrops(HarvestDropsEvent evt) {
//         if (evt.getWorld().isRemote) return;
//         if (evt.getWorld().provider.getDimension() != ModDimensions.VOID_DIMENSION_ID) return;

//         EntityPlayer harv = evt.getHarvester();
//         if (!(harv instanceof FakePlayer)) return;

//         BlockPos pos = evt.getPos();
//         IBlockState state = evt.getState();
//         WorldServer ws = (WorldServer) evt.getWorld();

//         // Видаляємо блок
//         ws.setBlockToAir(pos);
//         ws.playEvent(2001, pos, Block.getStateId(state));
//         // Маркуємо чанк «брудним»
//         ws.getChunkFromBlockCoords(pos).markDirty();

//         // Примусово зберігаємо всі чанки, обгорнувши у try/catch
//         try {
//             ws.saveAllChunks(true, null);
//             System.out.println("[ThaumicForever:Void] Forced save after break at " + pos);
//         } catch (MinecraftException e) {
//             e.printStackTrace();
//         }
//     }
// }
