package com.koteuka404.thaumicforever;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import thaumcraft.client.lib.obj.IModelCustom;

public class FXRubyRunes extends Particle {
    private static final ResourceLocation MODEL = new ResourceLocation("thaumcraft", "models/obj/hemis.obj");
    private static final ResourceLocation[] FRAMES = new ResourceLocation[15];
    private final Entity target;
    private final float yaw;
    private final float pitch;
    private IModelCustom model;

    static {
        for (int a = 0; a < 15; ++a) {
            FRAMES[a] = new ResourceLocation("thaumcraft", "textures/models/hemis" + (a + 1) + ".png");
        }
    }

    public FXRubyRunes(World world, double x, double y, double z, Entity target, int age, float yaw, float pitch) {
        super(world, x, y, z, 0, 0, 0);
        this.target = target;
        this.yaw = yaw;
        this.pitch = pitch;
        this.particleMaxAge = age + rand.nextInt(age / 2);
        this.setSize(0.01f, 0.01f);
        this.particleScale = 1.0f;
        this.particleGravity = 0.0f;

        this.particleRed = 1.0f;
        this.particleGreen = 0.1f;
        this.particleBlue = 0.1f;

        if (target != null) {
            this.posX = target.posX;
            this.posY = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0;
            this.posZ = target.posZ;
        }
    }

    @Override
    public void renderParticle(BufferBuilder wr, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        if (model == null) model = AdvancedModelLoader.loadModel(MODEL);

        float fade = (particleAge + partialTicks) / particleMaxAge;
        float xx = (float)(prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
        float yy = (float)(prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
        float zz = (float)(prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

        GL11.glTranslated(xx, yy, zz);
        Minecraft.getMinecraft().renderEngine.bindTexture(FRAMES[Math.min(14, (int)(14.0f * fade))]);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        GL11.glRotatef(180.0f - yaw, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-pitch, 1.0f, 0.0f, 0.0f);

        float scale = (target == null) ? 0.3f : target.height * 0.2f;
        GL11.glScalef(scale, scale, scale);
        GL11.glColor4f(particleRed, particleGreen, particleBlue, Math.min(1.0f, (1.0f - fade) * 3.0f));

        model.renderAll();

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();

        Minecraft.getMinecraft().renderEngine.bindTexture(ParticleManager.PARTICLE_TEXTURES);
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (target != null) {
            this.posX = target.posX;
            this.posY = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0;
            this.posZ = target.posZ;
        }
    }
}
