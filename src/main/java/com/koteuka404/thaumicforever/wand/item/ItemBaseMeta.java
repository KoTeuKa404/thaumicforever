package com.koteuka404.thaumicforever.wand.item;

import java.util.List;

import javax.annotation.Nullable;

import com.koteuka404.thaumicforever.wand.api.ThaumicWandsAPI;
import com.koteuka404.thaumicforever.wand.api.item.wand.IStaffCore;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWandCap;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWandRod;
import com.koteuka404.thaumicforever.wand.util.WandHelper;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ItemBaseMeta extends ItemBase {

    protected final String baseName;
    protected String[] variants;

    public ItemBaseMeta(String name, String... variants) {
        super(name);
        this.setHasSubtypes(variants.length > 0);
        baseName = name;
        this.variants = variants;

    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == this.getCreativeTab() || tab == CreativeTabs.SEARCH)
            for (int i = 0; !(i == variants.length); i++)
                items.add(new ItemStack(this, 1, i));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (stack.getMetadata() > variants.length)
            stack.setItemDamage(0);
        return baseName + "_" + variants[stack.getItemDamage()];
    }

    public String getBaseName() {
        return baseName;
    }

    public String[] getVariants() {
        return variants;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        IWandCap cap = findCap(stack);
        if (cap != null) {
            int discount = Math.round((1.0F - cap.getDiscount()) * 100.0F);
            tooltip.add(TextFormatting.AQUA + I18n.format("tooltip.thaumicforever.wand.vis_discount", discount));
            addAspectDiscount(WandHelper.decomposeToPrimals(cap.getAspectDiscount()), tooltip);
            return;
        }

        IWandRod rod = findRod(stack);
        if (rod != null) {
            tooltip.add(TextFormatting.AQUA + I18n.format("tooltip.thaumicforever.wand.capacity", rod.getCapacity()));
        }
    }

    private static IWandCap findCap(ItemStack stack) {
        for (IWandCap cap : ThaumicWandsAPI.wandCaps.values()) {
            if (cap != null && ItemStack.areItemsEqual(stack, cap.getItemStack())) {
                return cap;
            }
        }
        return null;
    }

    private static IWandRod findRod(ItemStack stack) {
        for (IWandRod rod : ThaumicWandsAPI.wandRods.values()) {
            if (rod != null && ItemStack.areItemsEqual(stack, rod.getItemStack())) {
                return rod;
            }
        }
        for (IStaffCore core : ThaumicWandsAPI.staffCore.values()) {
            if (core != null && ItemStack.areItemsEqual(stack, core.getItemStack())) {
                return core;
            }
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private static void addAspectDiscount(AspectList discounts, List<String> tooltip) {
        if (discounts == null || discounts.size() == 0) {
            return;
        }

        StringBuilder values = new StringBuilder();
        for (Aspect aspect : discounts.getAspectsSortedByName()) {
            if (values.length() > 0) {
                values.append(", ");
            }
            values.append(discounts.getAmount(aspect)).append(' ').append(aspect.getName());
        }
        tooltip.add(TextFormatting.DARK_AQUA
            + I18n.format("tooltip.thaumicforever.wand.aspect_discount", values.toString()));
    }
}
