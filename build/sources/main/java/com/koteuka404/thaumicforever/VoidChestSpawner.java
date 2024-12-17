// package com.koteuka404.thaumicforever;

// import java.util.Random;

// import net.minecraft.init.Blocks;
// import net.minecraft.inventory.IInventory;
// import net.minecraft.item.ItemStack;
// import net.minecraft.tileentity.TileEntity;
// import net.minecraft.tileentity.TileEntityChest;
// import net.minecraft.world.World;
// import net.minecraftforge.common.MinecraftForge;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
// import net.minecraftforge.fml.common.gameevent.TickEvent;

// public class VoidChestSpawner {

//     private static final int CHECK_INTERVAL = 20 * 100; // Перевіряти раз в 100 днів (20 тік * 24000 тік у дні)
//     private static final Random RANDOM = new Random();

//     public VoidChestSpawner() {
//         MinecraftForge.EVENT_BUS.register(this);
//     }

//     @SubscribeEvent
//     public void onWorldTick(TickEvent.WorldTickEvent event) {
//         if (event.phase != TickEvent.Phase.END) {
//             return;
//         }

//         World world = event.world;
//         long worldTime = world.getWorldTime();

//         // Перевірка кожні 100 ігрових днів
//         if (worldTime % CHECK_INTERVAL == 0) {
//             spawnVoidTeleportItem(world);
//         }
//     }

//     private void spawnVoidTeleportItem(World world) {
//         for (TileEntity tileEntity : world.loadedTileEntityList) {
//             if (tileEntity instanceof TileEntityChest) {
//                 TileEntityChest chest = (TileEntityChest) tileEntity;

//                 // Перевіряємо, чи це сундук-ловушка
//                 if (chest.getBlockType() == Blocks.TRAPPED_CHEST) {
//                     IInventory inventory = chest;

//                     // Генеруємо позицію для предмета в сундуку
//                     int slot = RANDOM.nextInt(inventory.getSizeInventory());
//                     ItemStack currentStack = inventory.getStackInSlot(slot);

//                     // Якщо слот порожній, вставляємо предмет
//                     if (currentStack.isEmpty()) {
//                         inventory.setInventorySlotContents(slot, new ItemStack(ModItems.VOID_TELEPORT_ITEM));
//                         return;
//                     }
//                 }
//             }
//         }
//     }
// }
