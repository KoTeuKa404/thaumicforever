// package com.koteuka404.thaumicforever;

// import net.minecraft.entity.player.EntityPlayer;
// import net.minecraft.item.ItemStack;
// import net.minecraft.nbt.NBTTagCompound;
// import net.minecraftforge.event.entity.player.PlayerInteractEvent;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
// import thaumcraft.api.items.ItemsTC;

// public class Auraevent {
//     private static final float MAX_AURA = 300.0f; // Константа для збереженої аури

//     @SubscribeEvent
//     public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
//         if (!event.getWorld().isRemote) { // Працюємо на серверній стороні
//             if (event.getTarget() instanceof AuraNodeEntity) {
//                 System.out.println("Interacted with AuraNodeEntity");

//                 EntityPlayer player = event.getEntityPlayer();
//                 ItemStack heldItem = player.getHeldItem(event.getHand());

//                 // Перевіряємо, чи гравець тримає пустий phial (meta == 0)
//                 if (!heldItem.isEmpty() && heldItem.getItem() == ItemsTC.phial && heldItem.getMetadata() == 0) {
//                     System.out.println("Player is holding an empty phial (meta 0)");

//                     // Створюємо новий предмет
//                     ItemStack auraPhial = new ItemStack(ModItems.AuraPhial);
//                     NBTTagCompound nbt = new NBTTagCompound();
//                     nbt.setFloat("storedAura", MAX_AURA); // Додаємо ауру в NBT
//                     auraPhial.setTagCompound(nbt);

//                     // Замінюємо предмет у руці
//                     player.setHeldItem(event.getHand(), auraPhial);

//                     // Видаляємо сутність
//                     if (event.getTarget() instanceof AuraNodeEntity) {
//                         ((AuraNodeEntity) event.getTarget()).setDead();
//                     }

//                     event.setCanceled(true); // Скасовуємо стандартну взаємодію
//                 } else {
//                     System.out.println("Player is not holding an empty phial");
//                 }
//             }
//         }
//     }
// }
