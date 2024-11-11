package com.koteuka404.thaumicforever;

import java.util.List;

import org.lwjgl.opengl.GL11;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
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
        this.setUnlocalizedName("aquareia_goggles");
        this.setRegistryName("aquareia_goggles");
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.TOOLS);
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
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "thaumicforever:textures/items/aquareia_goggles_e.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack itemStack, EntityPlayer entityPlayer, RenderType renderType, float v) {
        if (renderType == RenderType.HEAD) {
            boolean armor = entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null;

            // Підключення текстури для баубла
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("thaumicforever", "textures/items/aquareia_goggles_b.png"));

            IRenderBauble.Helper.translateToHeadLevel(entityPlayer);
            IRenderBauble.Helper.translateToFace();
            IRenderBauble.Helper.defaultTransforms();

            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5D, -0.5D, 0.12D);
            UtilsFX.renderTextureIn3D(0.0F, 0.0F, 1.0F, 1.0F, 16, 26, 0.1F);

            // Підключення текстури для анімації
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("thaumicforever", "textures/items/aquareia_goggles.png"));

            // Параметри анімації
            int frameCount = 8;
            int frameHeight = 16;
            int textureHeight = 128;
            int currentFrame = (int)(Minecraft.getMinecraft().world.getTotalWorldTime() / 5 % frameCount);
            float minV = (float)(currentFrame * frameHeight) / (float)textureHeight;
            float maxV = (float)((currentFrame + 1) * frameHeight) / (float)textureHeight;

            // Додавання 2D анімації поверх 3D для шолома і баубла
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5F, 1.0F, 0.02F); // Налаштування позиції анімації
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            buffer.pos(-0.5D, 0.0D, 0.0D).tex(0.0F, minV).endVertex();
            buffer.pos(0.5D, 0.0D, 0.0D).tex(1.0F, minV).endVertex();
            buffer.pos(0.5D, 1.0D, 0.0D).tex(1.0F, maxV).endVertex();
            buffer.pos(-0.5D, 1.0D, 0.0D).tex(0.0F, maxV).endVertex();
            tessellator.draw();

            GlStateManager.popMatrix();
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("\u00A79" + "\u00A7o" + "Special goggles that reveal hidden things...");
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static boolean shouldRenderHud(EntityPlayer player) {
        return player.inventory.armorItemInSlot(3).getItem() instanceof ItemAquareiaGoggles
                || BaublesApi.isBaubleEquipped(player, ModItems.ItemAquareiaGoggles) != -1;
    }
}
