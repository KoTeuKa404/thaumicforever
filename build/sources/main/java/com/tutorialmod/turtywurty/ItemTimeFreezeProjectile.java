package com.tutorialmod.turtywurty;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemTimeFreezeProjectile extends Item {

    public ItemTimeFreezeProjectile() {
        super();
        setMaxStackSize(64);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!player.capabilities.isCreativeMode) {
            player.getHeldItem(hand).shrink(1);
        }
        player.playSound(SoundEvents.ENTITY_EGG_THROW, 0.3f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
        if (!world.isRemote) {
            EntityTimeFreezeProjectile projectile = new EntityTimeFreezeProjectile(world, player);
            projectile.shoot(player, player.rotationPitch, player.rotationYaw, -5.0f, 0.4f, 2.0f);
            world.spawnEntity(projectile);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}