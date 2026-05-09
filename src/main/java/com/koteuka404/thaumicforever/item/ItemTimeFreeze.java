package com.koteuka404.thaumicforever.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import com.koteuka404.thaumicforever.entity.EntityTimeFreezeProjectile;

public class ItemTimeFreeze extends Item {

    public ItemTimeFreeze() {
        setUnlocalizedName("time_freeze_item");
        setRegistryName("time_freeze_item");
        setMaxStackSize(8);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            EntityTimeFreezeProjectile projectile = new EntityTimeFreezeProjectile(world, player);
            projectile.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            
            if (world.spawnEntity(projectile)) {
                player.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 1.0F, 1.0F);
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
