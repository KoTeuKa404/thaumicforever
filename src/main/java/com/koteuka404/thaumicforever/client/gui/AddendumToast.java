package com.koteuka404.thaumicforever.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.gui.GuiResearchBrowser;
import thaumcraft.api.research.ResearchEntry;

@SideOnly(Side.CLIENT)
public class AddendumToast implements IToast {
    private static final ResourceLocation HUD_TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/hud.png");
    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation("thaumicforever", "textures/misc/4particles.png");
    private static final int ATLAS_SIZE = 1024;
    private static final int EXCLAMATION_SIZE = 16;
    private static final int EXCLAMATION_X = 15;
    private static final int EXCLAMATION_Y = 0;
    private static final long DISPLAY_TIME = 5000L;

    private final ResearchEntry entry;
    private long firstDrawTime;
    private boolean newDisplay = true;

    public AddendumToast(ResearchEntry entry) {
        this.entry = entry;
    }

    @Override
    public IToast.Visibility draw(GuiToast toastGui, long delta) {
        if (newDisplay) {
            firstDrawTime = delta;
            newDisplay = false;
        }

        Minecraft mc = toastGui.getMinecraft();
        mc.getTextureManager().bindTexture(HUD_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ZERO
        );
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        toastGui.drawTexturedModalRect(0, 0, 0, 224, 160, 32);

        GuiResearchBrowser.drawResearchIcon(entry, 6, 8, 0.0F, false);
        drawExclamation(mc, 17.0F, 6.0F);
        mc.fontRenderer.drawString(I18n.translateToLocal("tc.research.newpage"), 30, 7, 10631665);

        String title = entry.getLocalizedName();
        float width = mc.fontRenderer.getStringWidth(title);
        if (width > 124.0F) {
            float scale = 124.0F / width;
            GlStateManager.pushMatrix();
            GlStateManager.translate(30.0F, 18.0F, 0.0F);
            GlStateManager.scale(scale, scale, scale);
            mc.fontRenderer.drawString(title, 0, 0, 16755465);
            GlStateManager.popMatrix();
        } else {
            mc.fontRenderer.drawString(title, 30, 18, 16755465);
        }

        return delta - firstDrawTime < DISPLAY_TIME ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
    }

    private static void drawExclamation(Minecraft mc, float x, float y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 0.0F, 0.0F, 1.0F);

        mc.getTextureManager().bindTexture(PARTICLE_TEXTURE);
        GlStateManager.translate(x, y, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 1.0F);
        drawExclamationSprite(0.0F, 0.0F, EXCLAMATION_SIZE, EXCLAMATION_SIZE);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private static void drawExclamationSprite(float x, float y, float width, float height) {
        float u0 = (EXCLAMATION_X * EXCLAMATION_SIZE) / (float) ATLAS_SIZE;
        float v0 = (EXCLAMATION_Y * EXCLAMATION_SIZE) / (float) ATLAS_SIZE;
        float u1 = ((EXCLAMATION_X + 1) * EXCLAMATION_SIZE) / (float) ATLAS_SIZE;
        float v1 = ((EXCLAMATION_Y + 1) * EXCLAMATION_SIZE) / (float) ATLAS_SIZE;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, 0.0D).tex(u0, v1).endVertex();
        buffer.pos(x + width, y + height, 0.0D).tex(u1, v1).endVertex();
        buffer.pos(x + width, y, 0.0D).tex(u1, v0).endVertex();
        buffer.pos(x, y, 0.0D).tex(u0, v0).endVertex();
        tessellator.draw();
    }
}
