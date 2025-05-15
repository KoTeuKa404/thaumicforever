package com.koteuka404.thaumicforever;

import java.util.List;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IVisDiscountGear;

public class RingMaster extends Item implements IVisDiscountGear, IBauble {

    public RingMaster() {
        this.setUnlocalizedName("ring_master");
        this.setRegistryName("ring_master");
        this.setMaxStackSize(1);
    }
    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        return 15;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("\u00A77" + "\u00A7o" + "This ring was worth it");
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

  
}
