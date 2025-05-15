package com.koteuka404.thaumicforever;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;

public class ItemCustomCaster extends ItemCaster {

    public ItemCustomCaster(String name, int area) {
        super(name, area);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean consumeVis(ItemStack is, EntityPlayer player, float amount, boolean crafting, boolean sim) {
        float modifiedAmount = amount * 0.9f;
        return super.consumeVis(is, player, modifiedAmount, crafting, sim);
    }

    private boolean isOnCooldown(EntityPlayer player) {
        try {
            Method method = CasterManager.class.getDeclaredMethod("isOnCooldown", EntityPlayer.class);
            method.setAccessible(true);
            return (boolean) method.invoke(null, player);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ItemFocus focus = getFocus(stack);

        if (focus == null || isOnCooldown(player)) {
            return super.onItemRightClick(world, player, hand);
        }

        FocusPackage core = ItemFocus.getPackage(getFocusStack(stack));
        int baseCooldown = focus.getActivationTime(getFocusStack(stack));

        int reducedCooldown = (int) (baseCooldown * 0.95); 

        CasterManager.setCooldown(player, reducedCooldown);

        if (!world.isRemote) {
            player.sendMessage(new TextComponentString("Custom Caster Activated! Cooldown: " + reducedCooldown));
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
