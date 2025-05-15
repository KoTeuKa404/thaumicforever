package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class PotionFlight extends Potion {

    public static final PotionFlight INSTANCE = new PotionFlight();
    private static final ResourceLocation ICON = new ResourceLocation("thaumicforever", "textures/gui/flight_effect.png");

    private PotionFlight() {
        super(false, 0x00FFFF); 
        setPotionName("effect.flight");
        setRegistryName(new ResourceLocation("thaumicforever", "flight"));
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
            player.capabilities.setFlySpeed(amplifier == 0 ? 0.02f : 0.05f); 
            player.sendPlayerAbilities();
        }
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap attributes, int amplifier) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            player.capabilities.setFlySpeed(0.05f); 
            player.sendPlayerAbilities();
        }
        super.removeAttributesModifiersFromEntity(entity, attributes, amplifier);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true; 
    }

    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        mc.getTextureManager().bindTexture(ICON);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
        GlStateManager.disableBlend();
    }

    @Override
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        mc.getTextureManager().bindTexture(ICON);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
        GlStateManager.disableBlend();
    }
}
