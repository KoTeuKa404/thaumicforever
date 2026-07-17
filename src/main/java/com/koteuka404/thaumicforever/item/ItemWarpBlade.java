package com.koteuka404.thaumicforever.item;

import java.util.List;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class ItemWarpBlade extends ItemSword {

    private static final float BASE_ATTACK_DAMAGE = 7.0F;
    private static final float ATTACK_SPEED = -2.4F;

    private static final float WARP_DAMAGE_SCALE = 0.397F;
    private static final double WARP_DAMAGE_POWER = 1.05D;
    private static final float MAX_BONUS_DAMAGE = 50.0F;

    private final Multimap<String, AttributeModifier> attributeModifiers;

    public ItemWarpBlade() {
        super(ToolMaterial.DIAMOND);

        setRegistryName("warp_blade");
        setUnlocalizedName("warp_blade");
        setMaxStackSize(1);

        ImmutableMultimap.Builder<String, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(
                SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                new AttributeModifier(
                        ATTACK_DAMAGE_MODIFIER,
                        "Weapon modifier",
                        BASE_ATTACK_DAMAGE,
                        0
                )
        );

        builder.put(
                SharedMonsterAttributes.ATTACK_SPEED.getName(),
                new AttributeModifier(
                        ATTACK_SPEED_MODIFIER,
                        "Weapon modifier",
                        ATTACK_SPEED,
                        0
                )
        );

        this.attributeModifiers = builder.build();
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return slot == EntityEquipmentSlot.MAINHAND
                ? this.attributeModifiers
                : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    public static int getTotalWarp(EntityPlayer player) {
        if (player == null) {
            return 0;
        }

        IPlayerWarp warp = ThaumcraftCapabilities.getWarp(player);

        if (warp == null) {
            return 0;
        }

        int temporary = warp.get(IPlayerWarp.EnumWarpType.TEMPORARY);
        int normal = warp.get(IPlayerWarp.EnumWarpType.NORMAL);
        int permanent = warp.get(IPlayerWarp.EnumWarpType.PERMANENT);

        return Math.max(0, temporary + normal + permanent);
    }

    public static float getWarpBonusDamage(EntityPlayer player) {
        int totalWarp = getTotalWarp(player);
    
        if (totalWarp <= 0) {
            return 0.0F;
        }
    
        float bonus = (float) (Math.pow(totalWarp, WARP_DAMAGE_POWER) * WARP_DAMAGE_SCALE);
    
        if (bonus > MAX_BONUS_DAMAGE) {
            return MAX_BONUS_DAMAGE;
        }
    
        return bonus;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        EntityPlayer player = Minecraft.getMinecraft().player;

        int warp = getTotalWarp(player);
        float bonus = getWarpBonusDamage(player);

        tooltip.add(
                TextFormatting.GRAY + "Deals bonus damage based on your "
                        + TextFormatting.DARK_PURPLE + "Warp"
        );

        tooltip.add(
                TextFormatting.GRAY + "Current Warp: "
                        + TextFormatting.LIGHT_PURPLE + warp
        );

        tooltip.add(
                TextFormatting.GRAY + "Current bonus damage: "
                        + TextFormatting.RED + String.format("%.1f", bonus)
        );

        tooltip.add(
                TextFormatting.DARK_GRAY + "The more corrupted you are, the stronger the blade becomes"
        );

        super.addInformation(stack, world, tooltip, flag);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return TextFormatting.DARK_PURPLE + "Warp Blade";
    }
}