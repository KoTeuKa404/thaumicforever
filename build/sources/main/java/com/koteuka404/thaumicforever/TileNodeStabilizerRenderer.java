package com.koteuka404.thaumicforever;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import thaumcraft.client.lib.obj.IModelCustom;

@SideOnly(value=Side.CLIENT)
public class TileNodeStabilizerRenderer extends TileEntitySpecialRenderer<TileNodeStabilizer> {
    private final IModelCustom model = AdvancedModelLoader.loadModel(MODEL);
    private static final ResourceLocation MODEL = new ResourceLocation("thaumicforever", "models/block/node_stabilizer.obj");
    private static final ResourceLocation TEX = new ResourceLocation("thaumicforever", "textures/blocks/node_stabilizer.png");
    private static final ResourceLocation OVER = new ResourceLocation("thaumicforever", "textures/blocks/node_stabilizer_over.png");
    private static final ResourceLocation BUBBLE = new ResourceLocation("thaumicforever", "textures/misc/aura_lock.png");

    public void renderTileEntityAt(TileNodeStabilizer tile, double par2, double par4, double par6, float par8) {
        int bright = 20;
        if (tile.getWorld() != null) {
            bright = tile.getBlockType().getLightValue(tile.getBlockType().getDefaultState(), tile.getWorld(), tile.getPos());
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2 + 0.5f, (float)par4, (float)par6 + 0.5f);
        GL11.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(TEX);
        this.model.renderPart("lock");
        for (int a = 0; a < 4; ++a) {
            GL11.glPushMatrix();
            if (tile.getWorld() != null) {
                int j = bright;
                int k = j % 65536;
                int l = j / 65536;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) k, (float) l);
            }
            GL11.glRotatef((float)(90 * a), 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            GL11.glTranslatef(0.0f, 0.0f, (float)tile.count / 100.0f);
            this.bindTexture(TEX);
            this.model.renderPart("piston");
            if (tile.getWorld() != null) {
                float scale = MathHelper.sin((float)(Minecraft.getMinecraft().getRenderViewEntity().ticksExisted + a * 5) / 3.0f) * 0.1f + 0.9f;
                int j = 50 + (int)(170.0f * ((float)tile.count / 37.0f * scale));
                int k = j % 65536;
                int l = j / 65536;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) k, (float) l);
            }
            this.bindTexture(OVER);
            this.model.renderPart("piston");
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        if (tile.count > 0) {
            GL11.glPushMatrix();
            GL11.glAlphaFunc(516, 0.003921569f);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 1);
            GL11.glDepthMask(false);
            float alpha = MathHelper.sin((float)Minecraft.getMinecraft().getRenderViewEntity().ticksExisted / 8.0f) * 0.1f + 0.5f;
            this.bindTexture(BUBBLE);
            UtilsFX.renderFacingQuad((double)tile.getPos().getX() + 0.5, (double)tile.getPos().getY() + 1.5, (double)tile.getPos().getZ() + 0.5, 1, 1, 0, 0.9f, 0xFFFFFF, (float)tile.count / 37.0f * alpha, 771, par8);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glAlphaFunc(516, 0.1f);
            GL11.glPopMatrix();
        }
    }

    @Override
    public void render(TileNodeStabilizer par1TileEntity, double par2, double par4, double par6, float par8, int q, float a) {
        this.renderTileEntityAt(par1TileEntity, par2, par4, par6, par8);
    }
}
