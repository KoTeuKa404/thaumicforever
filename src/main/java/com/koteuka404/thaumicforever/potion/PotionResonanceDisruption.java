package com.koteuka404.thaumicforever.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionResonanceDisruption extends Potion {

    public static final PotionResonanceDisruption INSTANCE = new PotionResonanceDisruption();
    private static final ResourceLocation ICON = new ResourceLocation("thaumicforever", "textures/misc/2particles.png");

    private PotionResonanceDisruption() {
        super(true, 0x7A3FD1);
        setPotionName("effect.resonance_disruption");
        setRegistryName(new ResourceLocation("thaumicforever", "resonance_disruption"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        mc.getTextureManager().bindTexture(ICON);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x + 7, y + 7, 176, 0, 16, 16, 256, 256);
        GlStateManager.disableBlend();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        mc.getTextureManager().bindTexture(ICON);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x + 4, y + 4, 176, 0, 16, 16, 256, 256);
        GlStateManager.disableBlend();
    }
}
