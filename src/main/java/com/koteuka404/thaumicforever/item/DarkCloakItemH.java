package com.koteuka404.thaumicforever.item;

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
import net.minecraft.util.math.BlockPos;
import com.koteuka404.thaumicforever.client.model.DarkCloakModel;
import org.lwjgl.opengl.GL11;

public class DarkCloakItemH extends Item implements IBauble, IRenderBauble {

    private static final int HIDE_LIGHT_LEVEL = 6;
    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumicforever:textures/entity/dark_cloak3.png");
    private final DarkCloakModel model;

    public DarkCloakItemH() {
        setRegistryName("dark_cloak_bauble_head");
        setUnlocalizedName("dark_cloak_bauble_head");
        this.model = new DarkCloakModel();
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.HEAD;
    }


    @Override
    public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
        if (type != RenderType.BODY) return;
        if (player.isInvisible()) return;

        RenderLivingBase<?> render = (RenderLivingBase<?>) Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(player);
        if (render == null || !(render.getMainModel() instanceof ModelPlayer)) return;

        ModelPlayer modelPlayer = (ModelPlayer) render.getMainModel();

        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(TEXTURE);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();
        if (player.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        model.renderAttached(player, modelPlayer, 0.0625F, partialTicks);
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
    @Override
public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
    if (player instanceof EntityPlayer) {
        EntityPlayer entityPlayer = (EntityPlayer) player;

        if (isDarkAtPlayer(entityPlayer)) {
            if (entityPlayer.isPotionActive(MobEffects.INVISIBILITY)) {
                PotionEffect invisibility = entityPlayer.getActivePotionEffect(MobEffects.INVISIBILITY);
                if (invisibility != null && invisibility.getDuration() <= 210) { 
                    entityPlayer.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 40, 0, true, false));
                }
            } else {
                entityPlayer.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 40, 0, true, false));
            }
        } else {
            if (entityPlayer.isPotionActive(MobEffects.INVISIBILITY)) {
                entityPlayer.removePotionEffect(MobEffects.INVISIBILITY);
            }
        }
    }
}

    private boolean isDarkAtPlayer(EntityPlayer player) {
        return getPlayerLight(player) < HIDE_LIGHT_LEVEL;
    }

    private int getPlayerLight(EntityPlayer player) {
        BlockPos feet = player.getPosition();
        BlockPos body = new BlockPos(player.posX, player.posY + player.height * 0.5D, player.posZ);
        BlockPos eyes = new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ);

        return Math.max(
            player.world.getLight(feet),
            Math.max(player.world.getLight(body), player.world.getLight(eyes))
        );
    }
} 
    
