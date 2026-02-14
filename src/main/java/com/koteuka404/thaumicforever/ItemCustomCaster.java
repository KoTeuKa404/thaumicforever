package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;

public class ItemCustomCaster extends ItemCaster {

    public ItemCustomCaster(String name, int area) {
        super(name, area);
        this.setMaxStackSize(1);
        this.addPropertyOverride(
            new ResourceLocation("thaumicforever", "has_focus"),
            (stack, world, entity) -> getFocus(stack) != null ? 1.0f : 0.0f
        );
    }

    @Override
    public boolean consumeVis(ItemStack is, EntityPlayer player, float amount, boolean crafting, boolean sim) {
        float modifiedAmount = amount * 0.85f;
        return super.consumeVis(is, player, modifiedAmount, crafting, sim);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ActionResult<ItemStack> result = super.onItemRightClick(world, player, hand);

        if (!world.isRemote && result.getType() == EnumActionResult.SUCCESS) {
            ItemFocus focus = getFocus(stack);
            if (focus != null) {
                int baseCooldown = focus.getActivationTime(getFocusStack(stack));
                int reducedCooldown = Math.max(1, (int) (baseCooldown * 0.95));
                CasterManager.setCooldown(player, reducedCooldown);
            }
        }

        return result;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.AQUA + "Vis cost reduced by 15%");
    }
}
