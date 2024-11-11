package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemCrimsonBook extends Item {
    public static final int CRIMSON_BOOK_GUI = 3;

    public ItemCrimsonBook() {
        this.setMaxStackSize(1); // Only one book in a stack
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            // Server-side logic, like syncing NBT or player data (if needed)
        } else {
            world.playSound(player.posX, player.posY, player.posZ, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN , SoundCategory.PLAYERS, 1.0f, 1.0f, false);
        }
        // Ensure you're passing the correct GUI ID
        player.openGui(ThaumicForever.instance, ModGuiHandler.CRIMSON_BOOK_GUI, world, 0, 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    

}

