package com.koteuka404.thaumicforever;

import java.util.Map;
import java.util.WeakHashMap;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AquareiaGogglesRenderLayerHandler {

    private static final Map<RenderPlayer, Boolean> PATCHED = new WeakHashMap<>();

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        RenderPlayer renderer = event.getRenderer();
        if (PATCHED.containsKey(renderer)) return;

        renderer.addLayer(new LayerAquareiaGoggles(renderer));
        PATCHED.put(renderer, Boolean.TRUE);
    }

    private static class LayerAquareiaGoggles implements LayerRenderer<EntityPlayer> {

        private final RenderPlayer renderPlayer;

        private LayerAquareiaGoggles(RenderPlayer renderPlayer) {
            this.renderPlayer = renderPlayer;
        }

        @Override
        public void doRenderLayer(EntityPlayer player,
                                    float limbSwing, float limbSwingAmount,
                                    float partialTicks, float ageInTicks,
                                    float netHeadYaw, float headPitch,
                                    float scale) {

            ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (head.isEmpty() || !(head.getItem() instanceof ItemAquareiaGoggles)) return;

            if (!(renderPlayer.getMainModel() instanceof ModelPlayer)) return;
            ModelPlayer model = (ModelPlayer) renderPlayer.getMainModel();

            GlStateManager.pushMatrix();

            model.bipedHead.postRender(0.0625F);

            GlStateManager.translate(0.0F, -0.6F, -0.31F);
            GlStateManager.rotate(-180F, 0F, 1F, 0F);
            GlStateManager.scale(0.65F, 0.65F, 0.65F);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            GlStateManager.depthMask(false);

            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(-2.0F, -2.0F);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, -0.001F);
            ItemAquareiaGoggles.renderLensOnly(player);
            GlStateManager.popMatrix();

            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();

            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            // ====================================

            GlStateManager.popMatrix();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
