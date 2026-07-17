package com.koteuka404.thaumicforever.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;

public class ItemKnowledgeEpiphany extends Item {
    private static final String RESEARCH_TAG = "research";

    public ItemKnowledgeEpiphany() {
        this.setRegistryName("knowledge_epiphany");
        this.setUnlocalizedName("knowledge_epiphany");
        this.setMaxStackSize(1);
    }

    public static ItemStack create(Item item, String research) {
        ItemStack stack = new ItemStack(item);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(RESEARCH_TAG, research);
        stack.setTagCompound(tag);
        return stack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        String research = getResearch(stack);

        if (!world.isRemote && player instanceof EntityPlayerMP && !research.isEmpty()) {
            ThaumcraftApi.internalMethods.completeResearch((EntityPlayerMP) player, research);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }

        return new ActionResult<>(research.isEmpty() ? EnumActionResult.FAIL : EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        String research = getResearch(stack);
        if (!research.isEmpty()) {
            tooltip.add(TextFormatting.GOLD + localizeResearchName(research));
        } else {
            tooltip.add(TextFormatting.DARK_GRAY + I18n.format("tooltip.knowledge_epiphany.empty"));
        }
    }

    private static String getResearch(ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(RESEARCH_TAG)) {
            return "";
        }
        return stack.getTagCompound().getString(RESEARCH_TAG);
    }

    @SideOnly(Side.CLIENT)
    private static String localizeResearchName(String research) {
        String key = research.startsWith("research.") && research.endsWith(".title")
            ? research
            : "research." + research + ".title";
        String translated = I18n.format(key);
        return translated.equals(key) ? research : translated;
    }
}
