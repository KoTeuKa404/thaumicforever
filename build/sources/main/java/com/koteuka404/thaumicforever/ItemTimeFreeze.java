package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemTimeFreeze extends Item {

    public ItemTimeFreeze() {
        setUnlocalizedName("time_freeze_item");
        setRegistryName("time_freeze_item");
        setMaxStackSize(8); // Максимум вісім предметів у руці
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            // Створюємо снаряд і запускаємо його
            EntityTimeFreezeProjectile projectile = new EntityTimeFreezeProjectile(world, player);
            projectile.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            
            // Відтворюємо звук стрільби
            world.spawnEntity(projectile);
            player.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 1.0F, 1.0F);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
