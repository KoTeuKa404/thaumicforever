package com.koteuka404.thaumicforever;

import java.util.List;

import org.lwjgl.opengl.GL11;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import baubles.api.render.IRenderBauble.RenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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

public class ItemAquareiaGoggles extends ItemArmor implements IRevealer, IGoggles, IVisDiscountGear, IBauble, IRenderBauble {

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
        return 15;
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
        return "thaumicforever:textures/items/aquareia_goggles_e.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack itemStack, EntityPlayer entityPlayer,
                                     RenderType renderType, float v) {
        if (renderType != RenderType.HEAD) return;

        Minecraft mc = Minecraft.getMinecraft();

        mc.renderEngine.bindTexture(new ResourceLocation("thaumicforever",
                "textures/items/aquareia_goggles_b.png"));
        IRenderBauble.Helper.translateToHeadLevel(entityPlayer);
        IRenderBauble.Helper.translateToFace();
        IRenderBauble.Helper.defaultTransforms();
        GlStateManager.rotate(180F, 0F, 1F, 0F);
        GlStateManager.translate(-0.5D, -0.5D, 0.12D);
        UtilsFX.renderTextureIn3D(0F, 0F, 1F, 1F, 16, 26, 0.1F);

        mc.renderEngine.bindTexture(new ResourceLocation("thaumicforever","textures/items/aquareia_goggles.png"));
        int frameCount = 8;
        int frameHeight = 16;
        int textureHeight = 128;
        long time = mc.world.getTotalWorldTime();
        int currentFrame = (int)(time / 5 % frameCount);
        float minV = (currentFrame * frameHeight) / (float)textureHeight;
        float maxV = ((currentFrame + 1) * frameHeight) / (float)textureHeight;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5F, 1.0F, 0.02F);
        GlStateManager.rotate(180F, 1F, 0F, 0F);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buf.pos(-0.5D, 0.0D, 0.0D).tex(0.0F, minV).endVertex();
        buf.pos( 0.5D, 0.0D, 0.0D).tex(1.0F, minV).endVertex();
        buf.pos( 0.5D, 1.0D, 0.0D).tex(1.0F, maxV).endVertex();
        buf.pos(-0.5D, 1.0D, 0.0D).tex(0.0F, maxV).endVertex();
        tess.draw();

        GlStateManager.popMatrix();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn,
                               List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("\u00A79\u00A7oSpecial goggles that reveal hidden things...");
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static boolean shouldRenderHud(EntityPlayer player) {
        ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (head.getItem() instanceof ItemAquareiaGoggles) {
            return true;
        }
        return BaublesApi.isBaubleEquipped(player, ModItems.ItemAquareiaGoggles) != -1;
    }
}
