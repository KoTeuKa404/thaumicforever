package com.koteuka404.thaumicforever;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemCelestialNotes extends Item {

    public static final String[] VARIANTS = new String[]{"blood_moon"};

    public ItemCelestialNotes() {
        setRegistryName("celestial_notes");
        setUnlocalizedName("celestial_notes");
        setHasSubtypes(true);
        setCreativeTab(CreativeTabs.MISC);
        setMaxStackSize(1);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getMetadata();
        return super.getUnlocalizedName() + "." + VARIANTS[meta % VARIANTS.length];
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < VARIANTS.length; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int meta = stack.getMetadata();
        String variantName = VARIANTS[meta % VARIANTS.length];

        String formattedName = formatMetaName(variantName);

        tooltip.add(TextFormatting.AQUA + formattedName);
    }

    private String formatMetaName(String name) {
        String[] words = name.split("_"); 
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            if (formatted.length() > 0) {
                formatted.append(" ");
            }
            formatted.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase()); 
        }
        return formatted.toString();
    }
}
