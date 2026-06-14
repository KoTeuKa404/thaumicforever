package com.koteuka404.thaumicforever.item;

import java.util.List;

import com.koteuka404.thaumicforever.api.golemcore.IGolemCoreItem;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.api.golems.IGolemAPI;

public class ItemGolemCore extends Item implements IGolemCoreItem {
    private final ResourceLocation coreId;

    public ItemGolemCore(String name, ResourceLocation coreId) {
        this.coreId = coreId;
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        this.setMaxStackSize(1);
    }

    @Override
    public ResourceLocation getGolemCoreId(ItemStack stack) {
        return this.coreId;
    }

    @Override
    public boolean canInstallCore(EntityPlayer player, IGolemAPI golem, ItemStack stack) {
        return golem != null && golem.getGolemEntity() != null;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String key = this.getUnlocalizedName() + ".desc";
        if (I18n.canTranslate(key)) {
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocal(key));
        }
    }
}
