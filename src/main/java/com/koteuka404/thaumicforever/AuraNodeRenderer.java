package com.koteuka404.thaumicforever;

import org.lwjgl.opengl.GL11;

import baubles.api.BaublesApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.lib.UtilsFX;

public class AuraNodeRenderer extends Render<AuraNodeEntity> {
    private static final ResourceLocation[] NODE_TEXTURES = {
        new ResourceLocation("thaumicforever", "textures/misc/aura_1.png"),
        new ResourceLocation("thaumicforever", "textures/misc/aura_2.png"),
        new ResourceLocation("thaumicforever", "textures/misc/aura_3.png")
    };

    public AuraNodeRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
public void doRender(AuraNodeEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
    EntityPlayer player = Minecraft.getMinecraft().player;
    boolean isWearingAquareiaGoggles = player.inventory.armorInventory.get(3).getItem() instanceof ItemAquareiaGoggles 
                                       || (BaublesApi.getBaublesHandler(player) != null 
                                       && BaublesApi.getBaublesHandler(player).getStackInSlot(4).getItem() instanceof ItemAquareiaGoggles);

    if (!isWearingAquareiaGoggles) {
        return;
    }

    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y, z);

    GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

    float scale = 3.0F; 
    GlStateManager.scale(scale, scale, scale);

    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    GlStateManager.disableLighting();

    float time = Minecraft.getMinecraft().world.getTotalWorldTime() + partialTicks;
    float cycleDuration = 30.0F;
    float blendFactor = (time % cycleDuration) / cycleDuration;

    int currentTextureIndex = (int) (time / cycleDuration) % NODE_TEXTURES.length;
    int nextTextureIndex = (currentTextureIndex + 1) % NODE_TEXTURES.length;

    ResourceLocation currentTexture = NODE_TEXTURES[currentTextureIndex];
    Minecraft.getMinecraft().getTextureManager().bindTexture(currentTexture);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F - blendFactor);
    UtilsFX.renderTextureIn3D(0.0F, 0.0F, 1.0F, 1.0F, 16, 16, 0.1F);

    ResourceLocation nextTexture = NODE_TEXTURES[nextTextureIndex];
    Minecraft.getMinecraft().getTextureManager().bindTexture(nextTexture);
    GlStateManager.color(1.0F, 1.0F, 1.0F, blendFactor);
    UtilsFX.renderTextureIn3D(0.0F, 0.0F, 1.0F, 1.0F, 16, 16, 0.1F);

    GlStateManager.enableLighting();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
}


    private boolean hasAquareiaGoggles(EntityPlayer player) {
        if (player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ItemsTC.goggles) {
            return true;
        }

        if (BaublesApi.isBaubleEquipped(player, ItemsTC.goggles) != -1) {
            return true;
        }
        return false;
    }

    @Override
    protected ResourceLocation getEntityTexture(AuraNodeEntity entity) {
        return NODE_TEXTURES[0];
    }
}
