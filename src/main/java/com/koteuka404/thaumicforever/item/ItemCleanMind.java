package com.koteuka404.thaumicforever.item;

import com.wonginnovations.oldresearch.OldResearch;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemCleanMind extends Item {

    public ItemCleanMind() {
        this.setMaxStackSize(16);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        if (!(entityLiving instanceof EntityPlayer)) {
            return stack;
        }

        EntityPlayer player = (EntityPlayer) entityLiving;
        applyToPlayer(player);

        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
            if (!player.inventory.addItemStackToInventory(emptyBottle)) {
                player.dropItem(emptyBottle, false);
            }
        }

        return stack;
    }

    public static void applyToPlayer(EntityPlayer player) {
        if (player == null || player.world.isRemote) return;
        if (OldResearch.proxy != null && OldResearch.proxy.getPlayerKnowledge() != null) {
            String username = player.getGameProfile().getName();
            int current = OldResearch.proxy.getPlayerKnowledge().getResearchCompleted(username);
            OldResearch.proxy.getPlayerKnowledge().setResearchCompleted(username, Math.max(0, current - 1));
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }
}
