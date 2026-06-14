package com.example.coremod;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.golems.EntityThaumcraftGolem;

public final class GolemHeldItemRenderPatch {
    private GolemHeldItemRenderPatch() {}

    public static boolean drawHeldCasterItem(EntityThaumcraftGolem entity) {
        ItemStack itemstack = entity.getHeldItemMainhand();
        if (itemstack == null || itemstack.isEmpty()) {
            return false;
        }

        Item item = itemstack.getItem();

        if (item instanceof IWand) {
            renderWand(entity, itemstack);
            return true;
        }

        if (item instanceof ICaster) {
            renderCaster(entity, itemstack);
            return true;
        }

        return false;
    }

    private static void renderWand(EntityThaumcraftGolem entity, ItemStack itemstack) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.375D, 0.375D, 0.375D);
        GlStateManager.translate(0.5F, 0.1F, -1.1F);
        Minecraft.getMinecraft().getItemRenderer().renderItem(entity, itemstack, ItemCameraTransforms.TransformType.HEAD);
        GlStateManager.popMatrix();
    }

    private static void renderCaster(EntityThaumcraftGolem entity, ItemStack itemstack) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.375D, 0.375D, 0.375D);
        GlStateManager.translate(0.0F, 0.3F, -0.4F);
        Minecraft.getMinecraft().getItemRenderer().renderItem(entity, itemstack, ItemCameraTransforms.TransformType.HEAD);
        GlStateManager.popMatrix();
    }
}
