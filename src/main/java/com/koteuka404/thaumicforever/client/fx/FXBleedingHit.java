package com.koteuka404.thaumicforever.client.fx;

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
public class FXBleedingHit extends Particle {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation("thaumicforever", "textures/misc/2particles.png");

    private static final float TEX_W = 704.0f;
    private static final float TEX_H = 352.0f;
    private static final float CELL = 16.0f;
    private static final int FRAMES = 4; // x: 0,16,32,48 at y=0

    public FXBleedingHit(World world, double x, double y, double z, double mx, double my, double mz) {
        super(world, x, y, z, mx, my, mz);
        this.particleMaxAge = 10 + world.rand.nextInt(6);
        this.particleScale = 0.8f + world.rand.nextFloat() * 0.4f;
        this.particleGravity = 0.0f;
        this.canCollide = false;
        this.particleAlpha = 0.95f;
        this.particleRed = 1.0f;
        this.particleGreen = 0.35f;
        this.particleBlue = 0.35f;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.motionX *= 0.92;
        this.motionY *= 0.92;
        this.motionZ *= 0.92;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        float age = (this.particleAge + partialTicks) / (float) this.particleMaxAge;
        int frame = Math.min(FRAMES - 1, Math.max(0, (int) (age * FRAMES)));

        float u0 = (frame * CELL) / TEX_W;
        float v0 = 0.0f;
        float u1 = ((frame + 1) * CELL) / TEX_W;
        float v1 = CELL / TEX_H;

        float size = 0.12f * this.particleScale;
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

        int light = this.getBrightnessForRender(partialTicks);
        int lightU = (light >> 16) & 65535;
        int lightV = light & 65535;

        float alpha = this.particleAlpha * (1.0f - age * 0.85f);

        Tessellator tess = Tessellator.getInstance();
        tess.draw();

        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

        buffer.pos(x - rotationX * size - rotationXY * size, y - rotationZ * size, z - rotationYZ * size - rotationXZ * size)
            .tex(u1, v1).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightU, lightV).endVertex();
        buffer.pos(x - rotationX * size + rotationXY * size, y + rotationZ * size, z - rotationYZ * size + rotationXZ * size)
            .tex(u1, v0).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightU, lightV).endVertex();
        buffer.pos(x + rotationX * size + rotationXY * size, y + rotationZ * size, z + rotationYZ * size + rotationXZ * size)
            .tex(u0, v0).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightU, lightV).endVertex();
        buffer.pos(x + rotationX * size - rotationXY * size, y - rotationZ * size, z + rotationYZ * size - rotationXZ * size)
            .tex(u0, v1).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightU, lightV).endVertex();

        tess.draw();

        Minecraft.getMinecraft().getTextureManager().bindTexture(ParticleManager.PARTICLE_TEXTURES);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
}
