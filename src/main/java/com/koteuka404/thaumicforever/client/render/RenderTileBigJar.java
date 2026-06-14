package com.koteuka404.thaumicforever.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.renderers.models.block.ModelBrain;
import com.koteuka404.thaumicforever.client.model.ModelJar;
import com.koteuka404.thaumicforever.client.render.item.BigJarItemRenderer;
import com.koteuka404.thaumicforever.tile.TileBigJar;

@SideOnly(Side.CLIENT)
public class RenderTileBigJar extends TileEntitySpecialRenderer<TileBigJar> {
    private static final ResourceLocation JAR_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/jar.png");
    private static final ResourceLocation BRAIN_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/brain2.png");
    private static final ResourceLocation BRINE_TEXTURE =
            new ResourceLocation("minecraft", "textures/blocks/slime.png");

    private final ModelJar jarModel = new ModelJar();
    private final ModelBrain brainModel = new ModelBrain();
    private final BigJarItemRenderer.ModelFluidCube fluidModel = new BigJarItemRenderer.ModelFluidCube();
    private static final int BRAIN_COUNT = TileBigJar.MAX_BRAINS;
    private static final float BRAIN_MAX_XZ = 0.20F;
    // Global Y bounds for the jar (keep wide enough so BRAIN_REST actually affects height).
    private static final float BRAIN_MIN_Y = 0.0F;
    private static final float BRAIN_MAX_Y = 2.F;
    private static final float BRAIN_TRACK_RANGE = 20.0F;
    private static final float BRAIN_IDLE_XZ_AMPLITUDE = 0.12F;
    private static final float BRAIN_IDLE_Y_AMPLITUDE = 0.10F;
    private static final float[][] BRAIN_REST = {
        {  0.00F,  0.14F,  0.00F },
        { -0.16F,  0.20F, -0.16F },
        {  0.16F,  0.20F, -0.16F },
        { -0.16F, -0.18F,  0.16F },
        {  0.16F, -0.18F,  0.16F },
        { -0.06F,  0.12F, -0.04F },
        {  0.06F,  0.12F, -0.04F },
        {  0.00F, -0.24F,  0.00F }
    };

    @Override
    public void render(TileBigJar te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        float scaleX = 3.2F;
        float scaleY = 2.3F;
        float scaleZ = 3.2F;
        GlStateManager.translate(x + 1.0, y + scaleY, z + 1.0);
        GlStateManager.scale(scaleX, -scaleY, -scaleZ);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();

        GlStateManager.depthMask(true);
        mc.getTextureManager().bindTexture(BRAIN_TEXTURE);
        renderTrackingBrains(te, 0.0f, 0.30f, 0.0f, 0.35f, scaleX, scaleY, scaleZ);

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

    private void renderTrackingBrains(TileBigJar te, float x, float y, float z, float scale,
                                      float jarScaleX, float jarScaleY, float jarScaleZ) {
        int activeBrains = te != null ? MathHelper.clamp(te.getBrainCount(), 1, BRAIN_COUNT) : 1;
        EntityPlayer player = Minecraft.getMinecraft().player;
        float[] targetX = new float[BRAIN_COUNT];
        float[] targetY = new float[BRAIN_COUNT];
        float[] targetZ = new float[BRAIN_COUNT];

        double cx = te.getPos().getX() + 1.0D;
        double cz = te.getPos().getZ() + 1.0D;
        double originY = te.getPos().getY() + jarScaleY;
        boolean hasTrackTarget = false;
        double trackX = 0.0D;
        double trackY = 0.0D;
        double trackZ = 0.0D;
        boolean incitingGolem = te != null && te.hasInciteTarget();
        if (incitingGolem) {
            hasTrackTarget = true;
            trackX = te.getInciteTargetX();
            trackY = te.getInciteTargetY();
            trackZ = te.getInciteTargetZ();
        } else if (player != null) {
            hasTrackTarget = true;
            trackX = player.posX;
            trackY = player.posY + player.getEyeHeight();
            trackZ = player.posZ;
        }

        for (int i = 0; i < BRAIN_COUNT; i++) {
            float cxOff = te != null ? te.getBrainOffsetX(i) : 0.0F;
            float cyOff = te != null ? te.getBrainOffsetY(i) : 0.0F;
            float czOff = te != null ? te.getBrainOffsetZ(i) : 0.0F;
            // First render/load fallback: start from rest positions instead of center (0,0,0).
            if (Float.isNaN(cxOff) || Float.isNaN(cyOff) || Float.isNaN(czOff)
                    || Math.abs(cxOff) > 1.0F || Math.abs(cyOff) > 1.0F || Math.abs(czOff) > 1.0F
                    || (Math.abs(cxOff) < 1.0E-5F && Math.abs(cyOff) < 1.0E-5F && Math.abs(czOff) < 1.0E-5F)) {
                cxOff = BRAIN_REST[i][0];
                cyOff = BRAIN_REST[i][1];
                czOff = BRAIN_REST[i][2];
                if (te != null) {
                    te.setBrainOffsets(i, cxOff, cyOff, czOff);
                }
            }
            targetX[i] = cxOff;
            targetY[i] = cyOff;
            targetZ[i] = czOff;
        }

        if (hasTrackTarget && te != null && te.getWorld() != null) {
            double dxCenter = trackX - cx;
            double dzCenter = trackZ - cz;
            double distXZ = MathHelper.sqrt(dxCenter * dxCenter + dzCenter * dzCenter);
            if (distXZ <= BRAIN_TRACK_RANGE && distXZ > 1.0E-4D) {
                float t = (float) ((BRAIN_TRACK_RANGE - distXZ) / BRAIN_TRACK_RANGE);
                t = t * t * (3.0F - 2.0F * t);
                double nx = dxCenter / distXZ;
                double nz = dzCenter / distXZ;
                double sideX = -nz;
                double sideZ = nx;
                float push = (incitingGolem ? 0.14F : 0.05F) * t;
                float sidePush = (incitingGolem ? 0.03F : 0.06F) * t;
                float[] sideLane = { -1.75F, -1.25F, -0.75F, -0.25F, 0.25F, 0.75F, 1.25F, 1.75F };

                float yPush = 0.0F;
                for (int i = 0; i < activeBrains; i++) {
                    float lane = sideLane[i % sideLane.length];
                    targetX[i] += (float) (nx * push + sideX * sidePush * lane);
                    // Local Z axis is mirrored by parent scale(-Z), so we invert world contribution.
                    targetZ[i] += (float) (-(nz * push + sideZ * sidePush * lane));
                    targetY[i] += yPush;
                }
            }
        }

        if (te != null && te.getWorld() != null) {
            float time = te.getWorld().getTotalWorldTime() + Minecraft.getMinecraft().getRenderPartialTicks();
            for (int i = 0; i < activeBrains; i++) {
                // Give each brain its own autonomous path so it keeps drifting even with no player nearby.
                float idleX = BRAIN_REST[i][0]
                        + MathHelper.sin(time * 0.024F + i * 1.37F) * BRAIN_IDLE_XZ_AMPLITUDE
                        + MathHelper.cos(time * 0.041F + i * 0.63F) * 0.03F;
                float idleY = BRAIN_REST[i][1]
                        + MathHelper.sin(time * 0.031F + i * 1.11F) * BRAIN_IDLE_Y_AMPLITUDE
                        + MathHelper.cos(time * 0.018F + i * 0.57F) * 0.02F;
                float idleZ = BRAIN_REST[i][2]
                        + MathHelper.cos(time * 0.027F + i * 1.53F) * BRAIN_IDLE_XZ_AMPLITUDE
                        + MathHelper.sin(time * 0.039F + i * 0.79F) * 0.03F;

                targetX[i] = targetX[i] * 0.55F + idleX * 0.45F;
                targetY[i] = targetY[i] * 0.45F + idleY * 0.55F;
                targetZ[i] = targetZ[i] * 0.55F + idleZ * 0.45F;
            }
        }

        separateBrains(activeBrains, targetX, targetY, targetZ, 0.11F, 0.00F, 0.09F);

        float[] moveX = new float[BRAIN_COUNT];
        float[] moveY = new float[BRAIN_COUNT];
        float[] moveZ = new float[BRAIN_COUNT];

        if (te != null) {
            final float smoothing = 0.07F;
            for (int i = 0; i < activeBrains; i++) {
                moveX[i] = te.getBrainOffsetX(i) + (targetX[i] - te.getBrainOffsetX(i)) * smoothing;
                moveY[i] = te.getBrainOffsetY(i) + (targetY[i] - te.getBrainOffsetY(i)) * smoothing;
                moveZ[i] = te.getBrainOffsetZ(i) + (targetZ[i] - te.getBrainOffsetZ(i)) * smoothing;

                moveX[i] = MathHelper.clamp(moveX[i], -BRAIN_MAX_XZ, BRAIN_MAX_XZ);
                moveY[i] = MathHelper.clamp(moveY[i], BRAIN_MIN_Y, BRAIN_MAX_Y);
                moveZ[i] = MathHelper.clamp(moveZ[i], -BRAIN_MAX_XZ, BRAIN_MAX_XZ);
            }
            separateBrains(activeBrains, moveX, moveY, moveZ, 0.10F, 0.00F, 0.085F);
            for (int i = 0; i < activeBrains; i++) {
                moveX[i] = MathHelper.clamp(moveX[i], -BRAIN_MAX_XZ, BRAIN_MAX_XZ);
                moveY[i] = MathHelper.clamp(moveY[i], BRAIN_MIN_Y, BRAIN_MAX_Y);
                moveZ[i] = MathHelper.clamp(moveZ[i], -BRAIN_MAX_XZ, BRAIN_MAX_XZ);
                te.setBrainOffsets(i, moveX[i], moveY[i], moveZ[i]);
            }
        } else {
            for (int i = 0; i < activeBrains; i++) {
                moveX[i] = targetX[i];
                moveY[i] = targetY[i];
                moveZ[i] = targetZ[i];
            }
        }

        boolean shouldTrackTarget = false;
        if (hasTrackTarget && te != null) {
            double dxTrack = trackX - cx;
            double dzTrack = trackZ - cz;
            double distXZTrack = MathHelper.sqrt(dxTrack * dxTrack + dzTrack * dzTrack);
            shouldTrackTarget = distXZTrack <= BRAIN_TRACK_RANGE;
        }

        for (int i = 0; i < activeBrains; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + moveX[i], y + moveY[i], z + moveZ[i]);
            GlStateManager.scale(scale / jarScaleX, scale / jarScaleY, scale / jarScaleZ);

            if (shouldTrackTarget && te != null && te.getWorld() != null) {
                double bx = cx + moveX[i] * jarScaleX;
                double by = originY - (y + moveY[i]) * jarScaleY;
                double bz = cz - moveZ[i] * jarScaleZ;
                double dx = trackX - bx;
                double dy = trackY - by;
                double dz = trackZ - bz;
                float yaw = (float)(MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
                float pitch = (float)(-MathHelper.atan2(dy, MathHelper.sqrt(dx * dx + dz * dz)) * (180D / Math.PI));
                pitch = MathHelper.clamp(pitch, -25.0F, 15.0F);
                GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
            }
            brainModel.render();
            GlStateManager.popMatrix();
        }
    }

    private static void separateBrains(int count, float[] x, float[] y, float[] z,
                                       float minDistX, float minDistY, float minDistZ) {
        final float separationStrength = 0.35F;
        for (int iter = 0; iter < 2; iter++) {
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    float dx = x[i] - x[j];
                    float dz = z[i] - z[j];
                    float overlapX = minDistX - Math.abs(dx);
                    float overlapZ = minDistZ - Math.abs(dz);
                    if (overlapX <= 0.0F || overlapZ <= 0.0F) continue;

                    // Resolve only in X/Z plane to avoid vertical stacking.
                    if (overlapX <= overlapZ) {
                        float s = dx >= 0.0F ? 1.0F : -1.0F;
                        float push = overlapX * 0.5F * separationStrength;
                        x[i] += s * push;
                        x[j] -= s * push;
                    } else {
                        float s = dz >= 0.0F ? 1.0F : -1.0F;
                        float push = overlapZ * 0.5F * separationStrength;
                        z[i] += s * push;
                        z[j] -= s * push;
                    }
                }
            }
        }
    }
}
