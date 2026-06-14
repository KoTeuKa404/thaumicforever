package com.koteuka404.thaumicforever.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockPort extends ItemBlock {
    public ItemBlockPort(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_AQUA + I18n.format("tooltip.thaumicforever.port.cv"));
        tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.thaumicforever.port.link"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
