package com.koteuka404.thaumicforever.client.render.item;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.renderers.models.block.ModelBrain;
import com.koteuka404.thaumicforever.client.model.ModelJar;

@SideOnly(Side.CLIENT)
public class BigJarItemRenderer extends TileEntityItemStackRenderer {
    private static final ResourceLocation JAR_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/jar.png");
    private static final ResourceLocation BRAIN_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/brain2.png");
    private static final ResourceLocation BRINE_TEXTURE =
            new ResourceLocation("minecraft", "textures/blocks/slime.png");

    private final ModelJar jarModel = new ModelJar();
    private final ModelBrain brainModel = new ModelBrain();
    private final ModelFluidCube fluidModel = new ModelFluidCube();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();

        GlStateManager.translate(0.5, 1.05, 0.5);
        GlStateManager.scale(1.55F, -1.55F, -1.55F);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();

        GlStateManager.depthMask(true);
        mc.getTextureManager().bindTexture(BRAIN_TEXTURE);
        renderBrain(0.0f, 0.25f, 0.0f, 0.35f);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0, 0.62, 0.0);
        GlStateManager.scale(0.78f, 0.82f, 0.78f);
        mc.getTextureManager().bindTexture(BRINE_TEXTURE);
        GlStateManager.depthMask(true);
        GlStateManager.color(0.2f, 0.45f, 0.2f, 0.82f);
        fluidModel.render(0.0625f);
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.popMatrix();

        mc.getTextureManager().bindTexture(JAR_TEXTURE);
        GlStateManager.depthMask(false);
        GlStateManager.color(1f, 1f, 1f, 0.75f);
        jarModel.renderCore(0.0625f);
        GlStateManager.color(1f, 1f, 1f, 1f);

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        jarModel.renderLid(0.0625f);
        GlStateManager.enableBlend();

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderBrain(float x, float y, float z, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.scale(scale, scale, scale);
        brainModel.render();
        GlStateManager.popMatrix();
    }

    public static final class ModelFluidCube extends ModelBase {
        public void render(float scale) {
            float min = -6.0F * scale;
            float max = 6.0F * scale;
            float u0 = 0.0F;
            float u1 = 1.0F;
            float v0 = 0.0F;
            float v1 = 1.0F;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            // North
            buffer.pos(min, max, -max).tex(u0, v0).endVertex();
            buffer.pos(max, max, -max).tex(u1, v0).endVertex();
            buffer.pos(max, min, -max).tex(u1, v1).endVertex();
            buffer.pos(min, min, -max).tex(u0, v1).endVertex();

            // South
            buffer.pos(max, max, max).tex(u0, v0).endVertex();
            buffer.pos(min, max, max).tex(u1, v0).endVertex();
            buffer.pos(min, min, max).tex(u1, v1).endVertex();
            buffer.pos(max, min, max).tex(u0, v1).endVertex();

            // West
            buffer.pos(min, max, max).tex(u0, v0).endVertex();
            buffer.pos(min, max, -max).tex(u1, v0).endVertex();
            buffer.pos(min, min, -max).tex(u1, v1).endVertex();
            buffer.pos(min, min, max).tex(u0, v1).endVertex();

            // East
            buffer.pos(max, max, -max).tex(u0, v0).endVertex();
            buffer.pos(max, max, max).tex(u1, v0).endVertex();
            buffer.pos(max, min, max).tex(u1, v1).endVertex();
            buffer.pos(max, min, -max).tex(u0, v1).endVertex();

            // Up
            buffer.pos(min, max, max).tex(u0, v0).endVertex();
            buffer.pos(max, max, max).tex(u1, v0).endVertex();
            buffer.pos(max, max, -max).tex(u1, v1).endVertex();
            buffer.pos(min, max, -max).tex(u0, v1).endVertex();

            // Down
            buffer.pos(min, min, -max).tex(u0, v0).endVertex();
            buffer.pos(max, min, -max).tex(u1, v0).endVertex();
            buffer.pos(max, min, max).tex(u1, v1).endVertex();
            buffer.pos(min, min, max).tex(u0, v1).endVertex();

            tessellator.draw();

        }
    }
}
