package com.koteuka404.thaumicforever.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class PotionVampirism extends Potion {

    public static final PotionVampirism INSTANCE = new PotionVampirism();
    private static final ResourceLocation ICON = new ResourceLocation("thaumicforever", "textures/misc/2particles.png");

    private PotionVampirism() {
        super(false, 0xAA0000);
        setPotionName("effect.vampirism");
        setRegistryName(new ResourceLocation("thaumicforever", "vampirism"));
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        mc.getTextureManager().bindTexture(ICON);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x + 7, y + 7, 160, 0, 16, 16, 256, 256);
        GlStateManager.disableBlend();
    }

    @Override
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        mc.getTextureManager().bindTexture(ICON);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x + 4, y + 4, 160, 0, 16, 16, 256, 256);
        GlStateManager.disableBlend();
    }
}
