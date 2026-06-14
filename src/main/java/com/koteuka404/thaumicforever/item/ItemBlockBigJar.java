package com.koteuka404.thaumicforever.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockBigJar extends ItemBlock {

    public ItemBlockBigJar(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            int xp = stack.getTagCompound().hasKey("XP")
                    ? stack.getTagCompound().getInteger("XP")
                    : stack.getTagCompound().getInteger("xp");
            if (xp > 0) {
                tooltip.add(TextFormatting.GREEN + String.valueOf(xp) + " xp");
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
