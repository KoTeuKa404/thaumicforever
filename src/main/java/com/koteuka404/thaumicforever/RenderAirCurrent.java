// package com.koteuka404.thaumicforever;

// import org.lwjgl.opengl.GL11;

// import net.minecraft.client.Minecraft;
// import net.minecraft.client.renderer.BufferBuilder;
// import net.minecraft.client.renderer.GlStateManager;
// import net.minecraft.client.renderer.Tessellator;
// import net.minecraft.client.renderer.entity.Render;
// import net.minecraft.client.renderer.entity.RenderManager;
// import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
// import net.minecraft.util.ResourceLocation;
// import net.minecraft.util.math.Vec3d;

// public class RenderAirCurrent extends Render<EntityAirCurrent> {

//     private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/misc/wispy.png");

//     public RenderAirCurrent(RenderManager renderManager) {
//         super(renderManager);
//     }

//     @Override
//     public void doRender(EntityAirCurrent entity, double x, double y, double z, float entityYaw, float partialTicks) {
//         float scale = 0.2f;
//         float alpha = 0.4f;

//         GlStateManager.pushMatrix();
//         GlStateManager.translate(x, y, z);
//         GlStateManager.disableLighting();
//         GlStateManager.enableBlend();
//         GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//         GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
//         GlStateManager.depthMask(false);

//         Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

//         Tessellator tessellator = Tessellator.getInstance();
//         BufferBuilder buffer = tessellator.getBuffer();

//         Vec3d[] quad = new Vec3d[]{
//                 new Vec3d(-scale, -scale, 0),
//                 new Vec3d(-scale, scale, 0),
//                 new Vec3d(scale, scale, 0),
//                 new Vec3d(scale, -scale, 0)
//         };

//         float rot = (entity.ticksExisted + partialTicks) * 4.0f;

//         buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//         for (Vec3d vec : quad) {
//             Vec3d rotated = vec.rotateYaw((float) Math.toRadians(rot));
//             buffer.pos(rotated.x, rotated.y, rotated.z).tex((rotated.x > 0 ? 1 : 0), (rotated.y > 0 ? 1 : 0)).endVertex();
//         }

//         tessellator.draw();

//         GlStateManager.depthMask(true);
//         GlStateManager.disableBlend();
//         GlStateManager.enableLighting();
//         GlStateManager.popMatrix();
//     }

//     @Override
//     protected ResourceLocation getEntityTexture(EntityAirCurrent entity) {
//         return TEXTURE;
//     }
// }
