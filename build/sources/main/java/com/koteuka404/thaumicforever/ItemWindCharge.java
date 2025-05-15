package com.koteuka404.thaumicforever;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemWindCharge extends Item {
    public ItemWindCharge() {
        setUnlocalizedName("wind_charge");
        setRegistryName("wind_charge");
        setCreativeTab(CreativeTabs.COMBAT);
        setMaxStackSize(16);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            EntityWindCharge charge = new EntityWindCharge(world, player);
            world.spawnEntity(charge);

            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
