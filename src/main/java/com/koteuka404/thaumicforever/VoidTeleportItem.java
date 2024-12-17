package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class VoidTeleportItem extends Item {

    public VoidTeleportItem() {
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;

            // Якщо гравець не у VOID_DIMENSION, телепортуємо його
            if (playerMP.dimension != ModDimensions.VOID_DIMENSION.getId()) {
                WorldServer voidWorld = playerMP.getServer().getWorld(ModDimensions.VOID_DIMENSION.getId());

                if (voidWorld != null) {
                    // Отримуємо або створюємо позицію для структури
                    BlockPos spawnPosition = PlayerStructureManager.getOrCreateStructureForPlayer(playerMP, voidWorld);
                    if (spawnPosition != null) {
                        playerMP.changeDimension(ModDimensions.VOID_DIMENSION.getId(), new CustomTeleporter(spawnPosition));
                    } else {
                        System.err.println("Помилка: Не вдалося знайти позицію для структури!");
                    }
                } else {
                    System.err.println("Помилка: Вимір VOID_DIMENSION не знайдено!");
                }
            } 
            // Якщо гравець вже у VOID_DIMENSION, повертаємо в Overworld
            else {
                WorldServer overworld = playerMP.getServer().getWorld(0); // ID 0 для Overworld

                if (overworld != null) {
                    BlockPos overworldSpawn = overworld.getSpawnPoint();
                    playerMP.changeDimension(0, new CustomTeleporter(overworldSpawn));
                } else {
                    System.err.println("Помилка: Overworld не знайдено!");
                }
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    
    // Перевірка, чи гравець знаходиться в структурі
    private boolean isPlayerInStructure(EntityPlayerMP player, World world) {
        BlockPos pos = player.getPosition();
        return world.isBlockLoaded(pos) && !world.isAirBlock(pos.down());
    }
    
}
