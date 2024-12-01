package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class RavenCloakItem extends Item implements IBauble, IRenderBauble {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever:textures/entity/raven_cloak3.png");
    private final RavenCloakModel model;

    public RavenCloakItem() {
        setRegistryName("raven_cloak_bauble");
        setUnlocalizedName("raven_cloak_bauble");
        this.model = new RavenCloakModel();
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.BODY;
    }

    @Override
    public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
        if (type == RenderType.BODY) {
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(TEXTURE);
    
            RenderLivingBase<?> render = (RenderLivingBase<?>) Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(player);
            if (render != null && render.getMainModel() instanceof ModelPlayer) {
                ModelPlayer modelPlayer = (ModelPlayer) render.getMainModel();
    
                boolean isSneaking = player.isSneaking();
                float yOffset = isSneaking ? -0.0F : -0.25F; 
                float zOffset = isSneaking ? -0.0F : 0.0F;  
                float zOffsetch = isSneaking ? -0.25F : -0.1F;  
                float zOffsetchb = isSneaking ? -0.23F : -0.1F;  
    
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.0F); 
    
                model.rotateAngleY = modelPlayer.bipedHead.rotateAngleY;
                model.rotateAngleX = modelPlayer.bipedHead.rotateAngleX;
    
                // Рендер голови 
                GlStateManager.pushMatrix();
                float headYOffset = -4.5F + (float) Math.sin(modelPlayer.bipedHead.rotateAngleX) * 1.5F;
                float headZOffset = (float) Math.cos(modelPlayer.bipedHead.rotateAngleX) * -0.5F;
                GlStateManager.translate(0.0F, headYOffset, headZOffset);
                GlStateManager.rotate((float) Math.toDegrees(modelPlayer.bipedHead.rotateAngleY), 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate((float) Math.toDegrees(modelPlayer.bipedHead.rotateAngleX), 1.0F, 0.0F, 0.0F);
                GlStateManager.scale(0.06F, 0.06F, 0.06F);
                model.renderHead(player, 1.0F);
                GlStateManager.popMatrix();
    
                // Повертаємо прозорість 
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    
                // Рендер тулуба
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, yOffset,zOffsetchb);
                GlStateManager.scale(0.06F, 0.06F, 0.06F);
                model.renderChest(player, 1.0F);
                GlStateManager.popMatrix();
    
                model.setToPlayerAngles(modelPlayer, player);
    
                // Рендер всього тіла 
                // GlStateManager.pushMatrix(); // HE PYXATU KOD
                GlStateManager.translate(0.0F, yOffset, zOffset);
                GlStateManager.scale(0.06F, 0.06F, 0.06F);
                model.renderBody(player, 1.0F);
                // GlStateManager.popMatrix(); // HE PYXATU KOD
    
                // Рендер правої руки 
                // GlStateManager.pushMatrix(); // HE PYXATU KOD
                GlStateManager.translate(0.0F, yOffset, zOffsetch);
                GlStateManager.scale(0.06F, 0.06F, 0.06F);
                // model.renderRightArm(player, 1.0F);/ HE PYXATU KOD
                // GlStateManager.popMatrix(); // HE PYXATU KOD
    
                // Рендер лівої руки 
                // GlStateManager.pushMatrix(); // HE PYXATU KOD
                GlStateManager.translate(0.0F, yOffset, zOffsetch -0.1F);
                GlStateManager.scale(0.06F, 0.06F, 0.06F);
                // model.renderLeftArm(player, 1.0F);/ HE PYXATU KOD
                // GlStateManager.popMatrix(); // HE PYXATU KOD
    
                GlStateManager.popMatrix(); // HE PYXATU KOD
            }
    
            GlStateManager.popMatrix();
        }
    }
    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
    if (player instanceof EntityPlayer) {
        EntityPlayer entityPlayer = (EntityPlayer) player;

        int lightLevel = player.world.getLight(player.getPosition());

        if (lightLevel < 6) {
            if (!entityPlayer.isPotionActive(MobEffects.INVISIBILITY)) {
                entityPlayer.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 200, 0, true, false));
            }
        } else {
            if (entityPlayer.isPotionActive(MobEffects.INVISIBILITY)) {
                entityPlayer.removePotionEffect(MobEffects.INVISIBILITY);
            }
        }
        }
    }    
} 
    
