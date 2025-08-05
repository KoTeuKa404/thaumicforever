package com.koteuka404.thaumicforever;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class JarredNodeItemRenderer extends TileEntityItemStackRenderer {

    private static final ResourceLocation JAR_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/jar.png");
    private static final ResourceLocation NODE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/auranodes.png");
    private final ModelJar modelJar = new ModelJar();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();

        GlStateManager.translate(0.5, 0.9, 0.25);
        GlStateManager.scale(0.75F, -0.75F, 0.75F);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1f, 1f, 1f, 0.8f);
        mc.getTextureManager().bindTexture(JAR_TEXTURE);
        modelJar.render(null, 0, 0, 0, 0, 0, 0.0625f);
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.disableBlend();

        Aspect main = Aspect.ORDER;
        AspectList al = new AspectList();
        boolean hasNode = false;

        if (stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey("nodeData", 10)) {
                NBTTagCompound nodeData = tag.getCompoundTag("nodeData");
                if (nodeData.hasKey("nodeAspects", 10)) {
                    al.readFromNBT(nodeData.getCompoundTag("nodeAspects"));
                    Aspect[] sorted = al.getAspectsSortedByAmount();
                    if (sorted.length > 0) {
                        main = sorted[0];
                        hasNode = true;
                    }
                }
            }
        }
        if (!hasNode) {
            al.add(main, 1);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.5, 0); 
        GlStateManager.scale(0.55f, 0.55f, 0.55f);

        int col = main.getColor();
        int r = (col >> 16) & 0xFF, g = (col >> 8) & 0xFF, b = col & 0xFF;

        long ticks = mc.world.getTotalWorldTime();
        float t = (ticks + partialTicks) % 10000;
        float base = 0.35f;
        int frame = ((int) t) % 32;
        float u0 = frame / 32f, u1 = u0 + 1f / 32f;

        boolean isFirstPerson = mc.gameSettings.thirdPersonView == 0
                && (mc.player.getHeldItemMainhand() == stack || mc.player.getHeldItemOffhand() == stack)
                && mc.currentScreen == null;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        if (isFirstPerson) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);

            GlStateManager.disableCull();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            mc.getTextureManager().bindTexture(NODE_TEXTURE);

            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            float glowSize = base * 1.25f;
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buf.pos(-glowSize, -glowSize, 0).tex(u0, 1f/32f).color(r, g, b, 130).endVertex();
            buf.pos(glowSize, -glowSize, 0).tex(u1, 1f/32f).color(r, g, b, 130).endVertex();
            buf.pos(glowSize, glowSize, 0).tex(u1, 0f).color(r, g, b, 130).endVertex();
            buf.pos(-glowSize, glowSize, 0).tex(u0, 0f).color(r, g, b, 130).endVertex();
            tess.draw();

            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buf.pos(-base, -base, 0).tex(u0, 1f/32f).color(r, g, b, 220).endVertex();
            buf.pos(base, -base, 0).tex(u1, 1f/32f).color(r, g, b, 220).endVertex();
            buf.pos(base, base, 0).tex(u1, 0f).color(r, g, b, 220).endVertex();
            buf.pos(-base, base, 0).tex(u0, 0f).color(r, g, b, 220).endVertex();
            tess.draw();

            float coreSize = base * 0.45f;
            int cr = (int)(r * 0.45 + 255 * 0.55),
                    cg = (int)(g * 0.45 + 255 * 0.55),
                    cb = (int)(b * 0.45 + 255 * 0.55);
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buf.pos(-coreSize, -coreSize, 0).tex(u0, 2f/32f).color(cr, cg, cb, 110).endVertex();
            buf.pos(coreSize, -coreSize, 0).tex(u1, 2f/32f).color(cr, cg, cb, 110).endVertex();
            buf.pos(coreSize, coreSize, 0).tex(u1, 1f/32f).color(cr, cg, cb, 110).endVertex();
            buf.pos(-coreSize, coreSize, 0).tex(u0, 1f/32f).color(cr, cg, cb, 110).endVertex();
            tess.draw();

            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        } else {
            for (int i = 0; i < 3; i++) {
                GlStateManager.pushMatrix();
                if (!hasNode && i == 0) {
                    GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);
                } else if (i == 1) {
                    GlStateManager.rotate(90, 1.0F, 0.0F, 0.0F);
                } else if (i == 2) {
                    GlStateManager.rotate(90, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.disableCull();
                GlStateManager.disableDepth();
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                mc.getTextureManager().bindTexture(NODE_TEXTURE);

                GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
                float glowSize = base * 1.25f;
                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                buf.pos(-glowSize, -glowSize, 0).tex(u0, 1f/32f).color(r, g, b, 130).endVertex();
                buf.pos(glowSize, -glowSize, 0).tex(u1, 1f/32f).color(r, g, b, 130).endVertex();
                buf.pos(glowSize, glowSize, 0).tex(u1, 0f).color(r, g, b, 130).endVertex();
                buf.pos(-glowSize, glowSize, 0).tex(u0, 0f).color(r, g, b, 130).endVertex();
                tess.draw();

                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                buf.pos(-base, -base, 0).tex(u0, 1f/32f).color(r, g, b, 220).endVertex();
                buf.pos(base, -base, 0).tex(u1, 1f/32f).color(r, g, b, 220).endVertex();
                buf.pos(base, base, 0).tex(u1, 0f).color(r, g, b, 220).endVertex();
                buf.pos(-base, base, 0).tex(u0, 0f).color(r, g, b, 220).endVertex();
                tess.draw();

                float coreSize = base * 0.45f;
                int cr = (int)(r * 0.45 + 255 * 0.55),
                        cg = (int)(g * 0.45 + 255 * 0.55),
                        cb = (int)(b * 0.45 + 255 * 0.55);
                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                buf.pos(-coreSize, -coreSize, 0).tex(u0, 2f/32f).color(cr, cg, cb, 110).endVertex();
                buf.pos(coreSize, -coreSize, 0).tex(u1, 2f/32f).color(cr, cg, cb, 110).endVertex();
                buf.pos(coreSize, coreSize, 0).tex(u1, 1f/32f).color(cr, cg, cb, 110).endVertex();
                buf.pos(-coreSize, coreSize, 0).tex(u0, 1f/32f).color(cr, cg, cb, 110).endVertex();
                tess.draw();

                GlStateManager.enableCull();
                GlStateManager.disableBlend();
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }
}
