package com.koteuka404.thaumicforever.client.render;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.koteuka404.thaumicforever.client.ClientFutureDropCache;
import com.koteuka404.thaumicforever.item.ItemBrokenFutureLens;
import com.koteuka404.thaumicforever.network.NetworkHandler;
import com.koteuka404.thaumicforever.network.PacketRequestFutureDrops;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BrokenFutureLensRenderHandler {

    private static final double MAX_DISTANCE = 18.0D;
    private static final int MAX_ICONS = 12;

    private static final float ICON_SIZE = 0.28F;
    private static final float ICON_SPACING = 0.33F;

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        ClientFutureDropCache.clear();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;

        if (player == null || mc.world == null) {
            return;
        }

        if (!ItemBrokenFutureLens.isEquipped(player)) {
            return;
        }

        float partialTicks = event.getPartialTicks();
        Object[] entities = mc.world.loadedEntityList.toArray();

        for (Object entityObject : entities) {
            if (!(entityObject instanceof EntityLivingBase)) {
                continue;
            }

            EntityLivingBase mob = (EntityLivingBase) entityObject;

            if (mob == player || mob instanceof EntityPlayer || mob.isDead || mob.getHealth() <= 0.0F) {
                continue;
            }

            if (mob.getDistance(player) > MAX_DISTANCE) {
                continue;
            }

            int entityId = mob.getEntityId();

            if (!ClientFutureDropCache.isFresh(entityId) && ClientFutureDropCache.canRequest(entityId)) {
                ClientFutureDropCache.markRequested(entityId);
                NetworkHandler.INSTANCE.sendToServer(new PacketRequestFutureDrops(entityId));
            }

            List<ItemStack> drops = ClientFutureDropCache.getDrops(entityId);

            if (drops.isEmpty()) {
                continue;
            }

            double x = interpolate(mob.lastTickPosX, mob.posX, partialTicks);
            double y = interpolate(mob.lastTickPosY, mob.posY, partialTicks) + mob.height + 0.75D;
            double z = interpolate(mob.lastTickPosZ, mob.posZ, partialTicks);

            renderDrops(x, y, z, drops, partialTicks);
        }
    }

    private void renderDrops(double x, double y, double z, List<ItemStack> drops, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity view = mc.getRenderViewEntity();

        if (view == null) {
            return;
        }

        RenderManager renderManager = mc.getRenderManager();

        double viewX = interpolate(view.lastTickPosX, view.posX, partialTicks);
        double viewY = interpolate(view.lastTickPosY, view.posY, partialTicks);
        double viewZ = interpolate(view.lastTickPosZ, view.posZ, partialTicks);

        int count = Math.min(drops.size(), MAX_ICONS);

        if (count <= 0) {
            return;
        }

        GlStateManager.pushMatrix();

        try {
            GlStateManager.translate(x - viewX, y - viewY, z - viewZ);

            GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(
                    (mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F) * renderManager.playerViewX,
                    1.0F,
                    0.0F,
                    0.0F
            );

            // GlStateManager.scale(-1.0F, -1.0F, 1.0F);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.disableLighting();
            GlStateManager.disableCull();

            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                    GL11.GL_SRC_ALPHA,
                    GL11.GL_ONE_MINUS_SRC_ALPHA,
                    GL11.GL_ONE,
                    GL11.GL_ZERO
            );

            RenderHelper.disableStandardItemLighting();

            // renderBackground(count);

            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.enableTexture2D();

            float startX = -((count - 1) * ICON_SPACING) / 2.0F;

            for (int i = 0; i < count; i++) {
                ItemStack stack = drops.get(i);

                if (stack.isEmpty()) {
                    continue;
                }

                renderStackSprite(stack, startX + i * ICON_SPACING, 0.0F, ICON_SIZE);
            }

        } finally {
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableCull();
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.popMatrix();
        }
    }

    private void renderBackground(int count) {
        float width = count * ICON_SPACING + 0.18F;
        float height = 0.42F;

        float minX = -width / 2.0F;
        float maxX = width / 2.0F;
        float minY = -height / 2.0F;
        float maxY = height / 2.0F;

        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(minX, maxY, -0.01D).color(0.12F, 0.02F, 0.20F, 0.42F).endVertex();
        buffer.pos(maxX, maxY, -0.01D).color(0.12F, 0.02F, 0.20F, 0.42F).endVertex();
        buffer.pos(maxX, minY, -0.01D).color(0.02F, 0.00F, 0.04F, 0.42F).endVertex();
        buffer.pos(minX, minY, -0.01D).color(0.02F, 0.00F, 0.04F, 0.42F).endVertex();

        tessellator.draw();

        GlStateManager.enableTexture2D();
    }

    private void renderStackSprite(ItemStack stack, float x, float y, float size) {
        Minecraft mc = Minecraft.getMinecraft();

        IBakedModel model = mc.getRenderItem().getItemModelWithOverrides(stack, mc.world, null);

        if (model == null) {
            return;
        }

        TextureAtlasSprite sprite = model.getParticleTexture();

        if (sprite == null) {
            return;
        }

        float half = size / 2.0F;

        float minX = x - half;
        float maxX = x + half;
        float minY = y - half;
        float maxY = y + half;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        buffer.pos(minX, maxY, 0.0D)
                .tex(sprite.getMinU(), sprite.getMinV())
                .color(1.0F, 1.0F, 1.0F, 0.95F)
                .endVertex();

        buffer.pos(maxX, maxY, 0.0D)
                .tex(sprite.getMaxU(), sprite.getMinV())
                .color(1.0F, 1.0F, 1.0F, 0.95F)
                .endVertex();

        buffer.pos(maxX, minY, 0.0D)
                .tex(sprite.getMaxU(), sprite.getMaxV())
                .color(1.0F, 1.0F, 1.0F, 0.95F)
                .endVertex();

        buffer.pos(minX, minY, 0.0D)
                .tex(sprite.getMinU(), sprite.getMaxV())
                .color(1.0F, 1.0F, 1.0F, 0.95F)
                .endVertex();

        tessellator.draw();
    }

    private static double interpolate(double previous, double current, float partialTicks) {
        return previous + (current - previous) * partialTicks;
    }
}