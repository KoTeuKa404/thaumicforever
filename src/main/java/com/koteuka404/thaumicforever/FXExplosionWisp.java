package com.koteuka404.thaumicforever;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXExplosionWisp extends Particle {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation("thaumicforever", "textures/misc/4particles.png");

    private final int grid;
    private final int frameStart;
    private final int frameCount;

    public FXExplosionWisp(World world, double x, double y, double z,
                           double mx, double my, double mz,
                           int grid, int frameStart, int frameCount) {
        super(world, x, y, z, mx, my, mz);
        this.grid = Math.max(1, grid);
        this.frameStart = Math.max(0, frameStart);
        this.frameCount = Math.max(1, frameCount);

        this.particleMaxAge = 8 + rand.nextInt(6);
        this.particleScale = 0.6f + rand.nextFloat() * 0.4f;
        this.particleGravity = 0.0f;
        this.particleAlpha = 0.95f;
        this.canCollide = false;

        this.particleRed = 1.0f;
        this.particleGreen = 0.7f;
        this.particleBlue = 0.2f;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.motionX *= 0.9;
        this.motionY *= 0.9;
        this.motionZ *= 0.9;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        float age = (particleAge + partialTicks) / (float) particleMaxAge;
        int frame = frameStart + Math.min(frameCount - 1, (int) (age * frameCount));

        float inv = 1.0f / grid;
        float u0 = (frame % grid) * inv;
        float v0 = (frame / grid) * inv;
        float u1 = u0 + inv;
        float v1 = v0 + inv;

        float size = 0.1f * particleScale;
        float x = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
        float y = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
        float z = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

        int light = getBrightnessForRender(partialTicks);
        int lightU = (light >> 16) & 65535;
        int lightV = light & 65535;

        float alpha = particleAlpha * (1.0f - age);

        Tessellator tess = Tessellator.getInstance();
        tess.draw();

        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

        buffer.pos(x - rotationX * size - rotationXY * size, y - rotationZ * size, z - rotationYZ * size - rotationXZ * size)
            .tex(u1, v1).color(particleRed, particleGreen, particleBlue, alpha).lightmap(lightU, lightV).endVertex();
        buffer.pos(x - rotationX * size + rotationXY * size, y + rotationZ * size, z - rotationYZ * size + rotationXZ * size)
            .tex(u1, v0).color(particleRed, particleGreen, particleBlue, alpha).lightmap(lightU, lightV).endVertex();
        buffer.pos(x + rotationX * size + rotationXY * size, y + rotationZ * size, z + rotationYZ * size + rotationXZ * size)
            .tex(u0, v0).color(particleRed, particleGreen, particleBlue, alpha).lightmap(lightU, lightV).endVertex();
        buffer.pos(x + rotationX * size - rotationXY * size, y - rotationZ * size, z + rotationYZ * size - rotationXZ * size)
            .tex(u0, v1).color(particleRed, particleGreen, particleBlue, alpha).lightmap(lightU, lightV).endVertex();

        tess.draw();

        Minecraft.getMinecraft().getTextureManager().bindTexture(ParticleManager.PARTICLE_TEXTURES);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
}
