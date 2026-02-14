package com.koteuka404.thaumicforever;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.items.tools.ItemThaumometer;
import thaumcraft.common.lib.utils.EntityUtils;

@SideOnly(Side.CLIENT)
public class RenderAuraNode extends Render<EntityAuraNode> {
    public static final ResourceLocation texture = new ResourceLocation("thaumcraft", "textures/misc/auranodes.png");

    private static final int MAX_VISUAL_VIS = 250;

    public RenderAuraNode(RenderManager rm) {
        super(rm);
        this.shadowSize = 0.0f;
    }

    @Override
    public void doRender(EntityAuraNode entity, double x, double y, double z, float fq, float pticks) {
        if (entity.isDead) return;

        double vr = 8000.0;
        boolean canSee = EntityUtils.hasRevealer(Minecraft.getMinecraft().getRenderViewEntity());
        if (!canSee) {
            canSee = Minecraft.getMinecraft().player.getHeldItemMainhand() != null
                && Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemThaumometer
                && EntityUtils.isVisibleTo(0.8f, Minecraft.getMinecraft().getRenderViewEntity(), entity, 16.0f);
            vr = 300.0;
        }
        if (!canSee) return;

        double d = entity.getDistance(Minecraft.getMinecraft().getRenderViewEntity());
        if (d > vr) return;

        float alpha = 1.0f - (float)Math.min(1.0, d / (vr * 0.9));
        int color = 0x888888;
        int blend = 1;
        int type  = 1;

        int visualSum = Math.min(entity.getNodeSize(), MAX_VISUAL_VIS);
        float size = 0.15f + visualSum / (100f * 1.5f);

        Aspect dom = entity.getMainAspect();
        if (dom != null) {
            color = dom.getColor();
            blend = dom.getBlend();
            type  = 1 + entity.getNodeType();
        }

        GlStateManager.pushMatrix();
        bindTexture(texture);
        GlStateManager.disableDepth();
        
        UtilsFX.renderFacingQuad(
            entity.posX, entity.posY, entity.posZ,
            32, 32,
            entity.ticksExisted % 32,
            size,
            color,
            0.75f * alpha,
            blend,
            pticks
        );
        
        float s = 1f - MathHelper.sin((entity.ticksExisted + pticks) / 8f) / 5f;
        
        UtilsFX.renderFacingQuad(
            entity.posX, entity.posY, entity.posZ,
            32, 32,
            800 + (entity.ticksExisted % 16),
            s * size * 0.7f,
            color,
            0.9f * alpha,
            blend,
            pticks
        );
        
        UtilsFX.renderFacingQuad(
            entity.posX, entity.posY, entity.posZ,
            32, 32,
            32 * type + (entity.ticksExisted % 32),
            size / 3f,
            0xFFFFFF,
            alpha,
            type == 2 ? 771 : 1,
            pticks
        );
        
        // ---------- TAINT OVERLAY ----------
        if (entity.getNodeType() == 4) {
            int taintFrame = 160 + (entity.ticksExisted % 32);

            UtilsFX.renderFacingQuad(
                entity.posX, entity.posY, entity.posZ,
                32, 32,
                taintFrame,
                size * 0.42f,
                0xFFFFFF,
                alpha,
                /* alpha blend */ 771,
                pticks
            );
        }


        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
        

        if (d < 30.0) {
            float sc = 1f - (float)Math.min(1.0, d / 25.0);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.scale(0.025 * sc, 0.025 * sc, 0.025 * sc);
            UtilsFX.rotateToPlayer();
            GL11.glRotatef(180f, 0f, 0f, 1f);
            GL11.glColor4f(1f, 1f, 1f, 1f);

            Aspect main = entity.getMainAspect();
            int mainAmount = entity.getMainAspectAmount();
            List<Aspect> secondaries = entity.getSecondaryAspects();

            int tagCount = 0;
            if (main != null) tagCount++;
            for (Aspect asp : secondaries)
                if (asp != null && !asp.equals(main)) tagCount++;

            int iconWidth = 16;
            int startX = -(tagCount * iconWidth) / 2 + (tagCount == 1 ? 0 : 0);

            int i = 0;
            if (main != null) {
                UtilsFX.drawTag(startX + i * iconWidth, -32, main, mainAmount, 0, 0.0);
                i++;
            }
            for (Aspect asp : secondaries) {
                if (asp == null || asp.equals(main)) continue;
                int amount = entity.getSecondaryAmount(asp);
                UtilsFX.drawTag(startX + i * iconWidth, -32, asp, amount, 0, 0.0);
                i++;
            }

            GlStateManager.scale(0.5, 0.5, 0.5);

            String typeText = I18n.format("nodetype." + entity.getNodeType());
            String suffix = "";
            int regenType = entity.getRegenType();

            if (regenType == 1) {
                suffix = I18n.format("node.regen.fast");
            } else if (regenType == 2) {
                suffix = I18n.format("node.regen.slow");
            } else if (regenType == 3) {
                suffix = I18n.format("node.regen.fading");
            }

            String fullText = typeText;
            if (!suffix.isEmpty())
                fullText += ", " + suffix;

            int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(fullText);
            Minecraft.getMinecraft().fontRenderer.drawString(
                fullText, -sw / 2f, -72f, 0x888888, false
            );

            GlStateManager.popMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAuraNode entity) {
        return texture;
    }
}
