package com.koteuka404.thaumicforever;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

@SideOnly(Side.CLIENT)
public class RenderTileJarredNode extends TileEntitySpecialRenderer<TileEntityJarredNode> {
    private final ModelJar model = new ModelJar();

    @Override
    public void render(TileEntityJarredNode te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 1, z + 0.5);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);

        Minecraft.getMinecraft().getTextureManager().bindTexture(
            new ResourceLocation("thaumcraft", "textures/models/jar.png")
        );
        model.render(null, 0, 0, 0, 0, 0, 0.0625f);

        NBTTagCompound nodeNBT = te.getTrueNodeNBT();
        if (nodeNBT != null && nodeNBT.hasKey("nodeAspects", 10)) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0, 0.6, 0.0);
            GlStateManager.scale(0.55, 0.55, 0.55);

            GlStateManager.disableLighting();
            GlStateManager.disableCull();

            RenderManager rm = Minecraft.getMinecraft().getRenderManager();
            GlStateManager.rotate(rm.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(rm.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

            Minecraft.getMinecraft().getTextureManager().bindTexture(
                new ResourceLocation("thaumcraft", "textures/misc/auranodes.png")
            );

            int ticks = (int)((te.getWorld().getTotalWorldTime() + partialTicks) % 10000);
            float baseSize = 0.9f;
            float breath = 1.0f - (float)Math.sin((ticks + partialTicks) / 8f) / 5f;

            // ---- FRAME, UV ----
            int frame = ticks % 32;

            // Main aspect
            Aspect main = null;
            NBTTagCompound aspectsNBT = nodeNBT.getCompoundTag("nodeAspects");
            AspectList aspectList = new AspectList();
            aspectList.readFromNBT(aspectsNBT);
            Aspect[] aspects = aspectList.getAspectsSortedByAmount();
            if (aspects.length > 0) {
                main = aspects[0];
            }
            if (main == null) main = Aspect.ORDER;

            int color = main.getColor();
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.getInstance();
            net.minecraft.client.renderer.BufferBuilder buffer = tess.getBuffer();

            // ----------- GLOW BASE -----------
            float minU = frame / 32.0f;
            float maxU = minU + 1f / 32f;
            float minV = 0f;
            float maxV = 1f / 32f;

            GlStateManager.depthMask(false);
            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE); 

            float size = baseSize;
            buffer.begin(GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(-size, -size, 0.0).tex(minU, maxV).color(r, g, b, 255).endVertex();
            buffer.pos(size, -size, 0.0).tex(maxU, maxV).color(r, g, b, 255).endVertex();
            buffer.pos(size, size, 0.0).tex(maxU, minV).color(r, g, b, 255).endVertex();
            buffer.pos(-size, size, 0.0).tex(minU, minV).color(r, g, b, 255).endVertex();
            tess.draw();

            // ---------- PULSE LAYER ----------
            minV = 31f / 32f;
            maxV = 32f / 32f;
            float pulseSize = baseSize * 0.7f * breath;
            buffer.begin(GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(-pulseSize, -pulseSize, 0.0).tex(minU, maxV).color(r, g, b, 220).endVertex();
            buffer.pos(pulseSize, -pulseSize, 0.0).tex(maxU, maxV).color(r, g, b, 220).endVertex();
            buffer.pos(pulseSize, pulseSize, 0.0).tex(maxU, minV).color(r, g, b, 220).endVertex();
            buffer.pos(-pulseSize, pulseSize, 0.0).tex(minU, minV).color(r, g, b, 220).endVertex();
            tess.draw();

            // ------------- CORE ---------------
            minV = 1f / 32f;
            maxV = 2f / 32f;
            float coreSize = baseSize * 0.35f;
            int coreR = (int)(r * 0.45 + 255 * 0.55);
            int coreG = (int)(g * 0.45 + 255 * 0.55);
            int coreB = (int)(b * 0.45 + 255 * 0.55);
            int coreA = 140;
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); 
            buffer.begin(GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(-coreSize, -coreSize, 0.0).tex(minU, maxV).color(coreR, coreG, coreB, coreA).endVertex();
            buffer.pos(coreSize, -coreSize, 0.0).tex(maxU, maxV).color(coreR, coreG, coreB, coreA).endVertex();
            buffer.pos(coreSize, coreSize, 0.0).tex(maxU, minV).color(coreR, coreG, coreB, coreA).endVertex();
            buffer.pos(-coreSize, coreSize, 0.0).tex(minU, minV).color(coreR, coreG, coreB, coreA).endVertex();
            tess.draw();

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.depthMask(true);
            GlStateManager.enableCull();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
