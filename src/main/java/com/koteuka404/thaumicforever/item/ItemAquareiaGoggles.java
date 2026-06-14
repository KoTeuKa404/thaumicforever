// ItemAquareiaGoggles.java
package com.koteuka404.thaumicforever.item;

import com.koteuka404.thaumicforever.registry.ModItems;

import java.util.List;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.client.lib.UtilsFX;

public class ItemAquareiaGoggles extends ItemArmor
        implements IRevealer, IGoggles, IVisDiscountGear, IBauble, IRenderBauble {

    public ItemAquareiaGoggles(String name, ArmorMaterial mat) {
        super(mat, 3, EntityEquipmentSlot.HEAD);
        setUnlocalizedName(name);
        setRegistryName(name);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        return 10;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.HEAD;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        long time = 0L;
        if (entity != null && entity.world != null) {
            time = entity.world.getTotalWorldTime();
        }
        int frame = (int) ((time / 5L) % 8L);
        return "thaumicforever:textures/items/aquareia_goggles/aquareia_goggles_e_" + frame + ".png";
    }

    // =========================
    // Shared render (Baubles + Armor)
    // =========================
    @SideOnly(Side.CLIENT)
    public static void renderOnHeadCommon(EntityPlayer player) {
        Minecraft mc = Minecraft.getMinecraft();
        long time = mc.world != null ? mc.world.getTotalWorldTime() : 0L;
        int frame = (int) ((time / 5L) % 8L);

        mc.renderEngine.bindTexture(new ResourceLocation("thaumicforever", "textures/items/aquareia_goggles/aquareia_goggles_b_" + frame + ".png"));
        GlStateManager.rotate(180F, 0F, 1F, 0F);
        GlStateManager.translate(-0.5D, -0.5D, 0.12D);
        UtilsFX.renderTextureIn3D(0F, 0F, 1F, 1F, 16, 26, 0.1F);
    }

    // =========================
    // Baubles render
    // =========================
    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack itemStack, EntityPlayer player, RenderType renderType, float partialTicks) {
        if (renderType != RenderType.HEAD) return;

        GlStateManager.pushMatrix();
        IRenderBauble.Helper.translateToHeadLevel(player);
        IRenderBauble.Helper.translateToFace();
        IRenderBauble.Helper.defaultTransforms();

        renderOnHeadCommon(player);

        GlStateManager.popMatrix();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("\u00A79\u00A7oSpecial goggles that reveal hidden things...");
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static boolean shouldRenderHud(EntityPlayer player) {
        ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (!head.isEmpty() && head.getItem() instanceof ItemAquareiaGoggles) return true;
        return BaublesApi.isBaubleEquipped(player, ModItems.ItemAquareiaGoggles) != -1;
    }
}
