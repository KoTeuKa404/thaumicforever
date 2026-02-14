package com.koteuka404.thaumicforever;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import thaumcraft.client.lib.obj.IModelCustom;

@SideOnly(Side.CLIENT)
public class TileNodeTransducerRenderer extends TileEntitySpecialRenderer<TileNodeTransducer> {
    private final IModelCustom model = AdvancedModelLoader.loadModel(MODEL);
    private static final ResourceLocation MODEL = new ResourceLocation("thaumicforever", "models/block/node_stabilizer.obj");
    private static final ResourceLocation TEX = new ResourceLocation("thaumicforever", "textures/blocks/node_stabilizer.png");
    private static final ResourceLocation OVER = new ResourceLocation("thaumicforever", "textures/blocks/node_stabilizer_over_2.png");

    public void renderTileEntityAt(TileNodeTransducer tile, double par2, double par4, double par6, float par8) {
        int bright = 20;
        if (tile.getWorld() != null) {
            bright = tile.getBlockType().getLightValue(tile.getBlockType().getDefaultState(), tile.getWorld(), tile.getPos());
        }
        GL11.glPushMatrix();

        GL11.glTranslatef((float)par2 + 0.5f, (float)par4 + 1.0f, (float)par6 + 0.5f);

        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);

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
    }

    @Override
    public void render(TileNodeTransducer tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        this.renderTileEntityAt(tile, x, y, z, partialTicks);
    }
}
