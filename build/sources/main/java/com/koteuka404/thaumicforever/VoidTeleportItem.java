package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
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
        this.setMaxStackSize(1); // Старий спосіб встановлення розміру стека
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            WorldServer worldServer = (WorldServer) world;
            BlockPos spawnPos;

            if (player.dimension != ModDimensions.VOID_DIMENSION.getId()) {
                spawnPos = worldServer.getSpawnPoint();
                player.changeDimension(ModDimensions.VOID_DIMENSION.getId(), new CustomTeleporter(worldServer, spawnPos));
            } else {
                spawnPos = world.getSpawnPoint(); // Повертаємося до Overworld
                player.changeDimension(0, new CustomTeleporter(worldServer, spawnPos));
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
