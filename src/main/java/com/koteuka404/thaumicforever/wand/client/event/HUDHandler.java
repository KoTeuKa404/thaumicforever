package com.koteuka404.thaumicforever.wand.client.event;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;
import com.koteuka404.thaumicforever.wand.util.WandHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.casters.ICaster;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ModConfig;

@EventBusSubscriber(modid = ThaumicForever.MODID, value = Side.CLIENT)
public class HUDHandler {

    public static final ResourceLocation TC_HUD = new ResourceLocation("thaumcraft", "textures/gui/hud.png");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderHUDLow(RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.START)
            if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity();
                if (player != null && Minecraft.getMinecraft().inGameHasFocus && Minecraft.isGuiEnabled())
                    if (!WandHelper.getHeldWand(player).isEmpty())
                        renderWandHUD(player, e.renderTickTime);
            }
    }

    private static void renderWandHUD(EntityPlayer player, float renderTickTime) {
        short short1 = 240;
        short short2 = 240;

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPushMatrix();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        int l = sr.getScaledHeight();

        int dialLocation = ModConfig.CONFIG_GRAPHICS.dialBottom ? l - 32 : 0;

        GL11.glTranslatef(0.0F, dialLocation, -2000.0F);

        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);

        Minecraft.getMinecraft().renderEngine.bindTexture(TC_HUD);

        GL11.glPushMatrix();
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        UtilsFX.drawTexturedQuad(0.0F, 0.0F, 0.0F, 0.0F, 64.0F, 64.0F, -90.0D);
        GL11.glPopMatrix();

        GL11.glTranslatef(16.0F, 16.0F, 0.0F);

        ItemStack held = WandHelper.getHeldWand(player);
        Item wand = held.getItem();

        AspectList charge = WandHelper.getPrimalCharge(held);
        int maxVis = 0;
        if (wand instanceof IWand) {
            maxVis = ((IWand) wand).getMaxCharge(held, player);
        }
        if (maxVis <= 0) maxVis = 1;

        ItemStack focusStack = ItemStack.EMPTY;
        if (wand instanceof ICaster) {
            ItemStack tmp = ((ICaster) wand).getFocusStack(held);
            if (tmp != null) {
                focusStack = tmp;
            }
        }
        if (!focusStack.isEmpty()) {
            GL11.glPushMatrix();
            GL11.glTranslatef(-24.0F, -24.0F, 90.0F);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(2896);
            GL11.glEnable(32826);
            GL11.glEnable(2903);
            GL11.glEnable(2896);
            try {
                Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(focusStack, 16, 16);
            } catch (Exception ignored) {
            }
            GL11.glDisable(2896);
            GL11.glPopMatrix();
            Minecraft.getMinecraft().renderEngine.bindTexture(TC_HUD);
            GL11.glColor4f(1f, 1f, 1f, 1f);
        }

        java.util.List<Aspect> primals = Aspect.getPrimalAspects();

        final int capsuleCount = 6;

        final float scale = 0.5F;

        final float gapPx = 25.0F;
        final float gap = gapPx / scale;

        final float capsuleHalfHeight = 21.0F;

        final float arcDegrees = 110.0F;
        final float arcStart = 80.0F;
        final float arcStep = (capsuleCount > 1) ? (arcDegrees / (capsuleCount - 1)) : 0.0F;

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        for (int i = 0; i < capsuleCount; i++) {
            Aspect aspect = primals.get(i % primals.size());
            Color ac = new Color(aspect.getColor());

            float have = charge.getAmount(aspect);
            int loc = Math.round(30.0F * clamp01(have / maxVis));

            GL11.glPushMatrix();

            float ang = arcStart + (arcStep * i);
            GL11.glRotatef(ang, 0.0F, 0.0F, 1.0F);

            GL11.glTranslatef(0.0F, -gap, 0.0F);

            GL11.glTranslatef(0.0F, -capsuleHalfHeight, 0.0F);

            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.0F);
            UtilsFX.drawTexturedQuad(-4.0F, 5F, 104.0F, 0.0F, 0F, 0F, -90.0D);
            GlStateManager.enableBlend();

            GL11.glColor4f(ac.getRed() / 255.0F, ac.getGreen() / 255.0F, ac.getBlue() / 255.0F, 0.8F);
            UtilsFX.drawTexturedQuad(-4.0F, 35 - loc, 104.0F, 0.0F, 8.0F, loc, -90.0D);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            UtilsFX.drawTexturedQuad(-8.0F, -3.0F, 72.0F, 0.0F, 16.0F, 42.0F, -90.0D);

            if (player.isSneaking()) {
                String txt = String.valueOf(Math.round(have));
                int tw = Minecraft.getMinecraft().fontRenderer.getStringWidth(txt);
                int th = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0F, -8.0F, 0.0F);
                GL11.glRotatef(-ang, 0.0F, 0.0F, 1.0F);
                Minecraft.getMinecraft().fontRenderer.drawString(txt, -tw / 2, -th / 2, 0xFFFFFF);
                GL11.glPopMatrix();
                Minecraft.getMinecraft().renderEngine.bindTexture(TC_HUD);
                GL11.glColor4f(1f, 1f, 1f, 1f);
            }

            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();

        GL11.glDisable(3042);

        GL11.glPopMatrix();
    }

    private static float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }
}
