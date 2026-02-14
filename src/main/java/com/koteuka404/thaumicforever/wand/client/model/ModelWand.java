package com.koteuka404.thaumicforever.wand.client.model;

import com.koteuka404.thaumicforever.wand.api.item.wand.IScepter;
import com.koteuka404.thaumicforever.wand.api.item.wand.IStaff;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;
import com.koteuka404.thaumicforever.wand.item.ItemStaff;
import com.koteuka404.thaumicforever.wand.item.ItemWand;
import com.koteuka404.thaumicforever.wand.main.ThaumicWands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.items.casters.ItemFocus;

import java.awt.*;

public class ModelWand extends ModelBase {
    ModelRenderer Rod;
    ModelRenderer Focus;
    ModelRenderer Cap;
    ModelRenderer CapBottom;

    public ModelWand() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        (this.Cap = new ModelRenderer(this, 0, 0)).addBox(-1.0f, -1.0f, -1.0f, 2, 2, 2);
        this.Cap.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Cap.setTextureSize(64, 32);
        this.Cap.mirror = true;
        this.setRotation(this.Cap, 0.0f, 0.0f, 0.0f);
        (this.CapBottom = new ModelRenderer(this, 0, 0)).addBox(-1.0f, -1.0f, -1.0f, 2, 2, 2);
        this.CapBottom.setRotationPoint(0.0f, 20.0f, 0.0f);
        this.CapBottom.setTextureSize(64, 32);
        this.CapBottom.mirror = true;
        this.setRotation(this.CapBottom, 0.0f, 0.0f, 0.0f);
        (this.Rod = new ModelRenderer(this, 0, 8)).addBox(-1.0f, -1.0f, -1.0f, 2, 18, 2);
        this.Rod.setRotationPoint(0.0f, 2.0f, 0.0f);
        this.Rod.setTextureSize(64, 32);
        this.Rod.mirror = true;
        this.setRotation(this.Rod, 0.0f, 0.0f, 0.0f);
        (this.Focus = new ModelRenderer(this, 0, 0)).addBox(-3.0f, -6.0f, -3.0f, 6, 6, 6);
        this.Focus.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.Focus.setTextureSize(64, 32);
        this.Focus.mirror = true;
        this.setRotation(this.Focus, 0.0f, 0.0f, 0.0f);
    }

    public void render(final ItemStack wandStack) {
        if (Minecraft.getMinecraft().getRenderManager() == null) {
            return;
        }
        final IWand wand = (IWand) wandStack.getItem();
        final ItemStack focusStack = wand instanceof ItemWand ? ((ItemWand) wand).getFocusStack(wandStack) : wand instanceof ItemStaff ? ((ItemStaff) wand).getFocusStack(wandStack) : ItemStack.EMPTY;
        final ItemFocus focus = wand instanceof ItemWand ? ((ItemWand) wand).getFocus(wandStack) : wand instanceof ItemStaff ? ((ItemStaff) wand).getFocus(wandStack) : null;
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final boolean staff = wand instanceof IStaff;
        final boolean runes = /*wand.hasRunes(wandStack)*/true;
        Minecraft.getMinecraft().renderEngine.bindTexture(wand.getRod(wandStack).getTexture());
        GlStateManager.pushMatrix();
        if (staff) {
            GlStateManager.translate(0.0, 0.2, 0.0);
        }
        GlStateManager.pushMatrix();
       /* if (wand.getRod(wandStack).isGlowing()) {
            final int j = (int) (200.0f + MathHelper.sin((float) player.ticksExisted) * 5.0f + 5.0f);
            final int k = j % 65536;
            final int l = j / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k, l);
        }*/
        if (staff) {
            GlStateManager.translate(0.0, -0.1, 0.0);
            GlStateManager.scale(1.2, 2.0, 1.2);
        }
        this.Rod.render(0.0625f);
      /*  if (wand.getRod(wandStack).isGlowing()) {
            final int i = player.getBrightnessForRender();
            final int m = i % 65536;
            final int k2 = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, m, k2);
        }*/
        GlStateManager.popMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(wand.getCap(wandStack).getTexture());
        GlStateManager.pushMatrix();
        if (staff) {
            GlStateManager.scale(1.3, 1.1, 1.3);
        } else {
            GlStateManager.scale(1.2, 1.0, 1.2);
        }
        if (wand instanceof IScepter) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.3, 1.3, 1.3);
            this.Cap.render(0.0625f);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0, 0.3, 0.0);
            GlStateManager.scale(1.0, 0.66, 1.0);
            this.Cap.render(0.0625f);
            GlStateManager.popMatrix();
        } else {
            this.Cap.render(0.0625f);
        }
        if (staff) {
            GlStateManager.translate(0.0, 0.225, 0.0);
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0, 0.66, 1.0);
            this.Cap.render(0.0625f);
            GlStateManager.popMatrix();
            GlStateManager.translate(0.0, 0.65, 0.0);
        }
        this.CapBottom.render(0.0625f);
        GlStateManager.popMatrix();
       /* if (focusStack != null && focusStack != ItemStack.EMPTY) {
            float alpha = 0.95f;
            if (focusStack.getFocusDepthLayerIcon(focusStack) != null) {
                GlStateManager.pushMatrix();
                if (staff) {
                    GL11.glTranslatef(0.0f, -0.15f, 0.0f);
                    GlStateManager.scale(0.165, 0.1765, 0.165);
                } else {
                    GL11.glTranslatef(0.0f, -0.09f, 0.0f);
                    GlStateManager.scale(0.16, 0.16, 0.16);
                }
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.field_110576_c);
                this.renderBlocks.func_147775_a(Blocks.STONE);
                BlockRenderer.drawFaces(this.renderBlocks, null, wand.getFocus(wandStack).getFocusDepthLayerIcon(focusStack), false);
                GlStateManager.popMatrix();
                alpha = 0.6f;
            }
            if (Thaumcraft.isHalloween) {
                UtilsFX.bindTexture("textures/models/spec_h.png");
            } else {
                UtilsFX.bindTexture("textures/models/wand.png");
            }
            GlStateManager.pushMatrix();
            if (staff) {
                GL11.glTranslatef(0.0f, -0.0475f, 0.0f);
                GlStateManager.scale(0.525, 0.5525, 0.525);
            } else {
                GlStateManager.scale(0.5, 0.5, 0.5);
            }
            final Color c = new Color(wand.getFocus(wandStack).getFocusColor(focusStack));
            GL11.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, alpha);
            final int j2 = (int) (195.0f + MathHelper.sin(player.ticksExisted / 3.0f) * 10.0f + 10.0f);
            final int k3 = j2 % 65536;
            final int l2 = j2 / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k3, l2);
            this.Focus.render(0.0625f);
            GlStateManager.popMatrix();
        }*/
        if (focus != null) {
            Color c = new Color(focus.getFocusColor(focusStack));
            GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);
            if (staff) {
                GlStateManager.translate(0.0f, -0.0475f, 0.0f);
                GlStateManager.scale(0.525, 0.5525, 0.525);
            } else {
                GlStateManager.scale(0.5, 0.5, 0.5);
            }
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(ThaumicWands.modID, "textures/items/wand/models/wand_focus.png"));
            this.Focus.render(0.0625f);
            GlStateManager.color(1F, 1F, 1F);
        }
        if (wand instanceof IScepter) {
            GlStateManager.pushMatrix();
            final int j = 200;
            final int k = j % 65536;
            final int l = j / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k, l);
            GlStateManager.blendFunc(770, 1);
            for (int rot = 0; rot < 10; ++rot) {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(36 * rot + player.ticksExisted, 0.0f, 1.0f, 0.0f);
                this.drawRune(0.16, -0.009999999776482582, -0.125, rot, player);
                GlStateManager.popMatrix();
            }
            GlStateManager.blendFunc(770, 771);
            GlStateManager.popMatrix();
        }
        /*if (runes) {
            GlStateManager.pushMatrix();
            final int j = 200;
            final int k = j % 65536;
            final int l = j / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k, l);
            GlStateManager.blendFunc(770, 1);
            for (int rot = 0; rot < 4; ++rot) {
                GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
                for (int a = 0; a < 14; ++a) {
                    final int rune = (a + rot * 3) % 16;
                    this.drawRune(0.36 + a * 0.14, -0.009999999776482582, -0.08, rune, player);
                }
            }
            GlStateManager.blendFunc(770, 771);
            GlStateManager.popMatrix();
        }*/
        GlStateManager.popMatrix();
    }

    private void drawRune(double x, double y, double z, int rune, EntityPlayer player) {
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(ThaumicWands.modID, "textures/misc/r_wand.png"));

        float r = MathHelper.sin((player.ticksExisted + rune * 5) / 5.0F) * 0.1F + 0.88F;
        float g = MathHelper.sin((player.ticksExisted + rune * 5) / 7.0F) * 0.1F + 0.63F;
        float alpha = MathHelper.sin((player.ticksExisted + rune * 5) / 10.0F) * 0.2F;

        GlStateManager.color(r, g, 0.2F, alpha + 0.6F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(x, y, z);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float minU = 0.0625F * rune;
        float maxU = minU + 0.0625F;
        float minV = 0.0F;
        float maxV = 1.0F;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-0.06 - alpha / 40.0, 0.06 + alpha / 40.0, 0.0).tex(maxU, maxV).color(r, g, 0.2F, alpha + 0.6F).endVertex();
        buffer.pos(0.06 + alpha / 40.0, 0.06 + alpha / 40.0, 0.0).tex(maxU, minV).color(r, g, 0.2F, alpha + 0.6F).endVertex();
        buffer.pos(0.06 + alpha / 40.0, -0.06 - alpha / 40.0, 0.0).tex(minU, minV).color(r, g, 0.2F, alpha + 0.6F).endVertex();
        buffer.pos(-0.06 - alpha / 40.0, -0.06 - alpha / 40.0, 0.0).tex(minU, maxV).color(r, g, 0.2F, alpha + 0.6F).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
    }

    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, null);
    }
}
