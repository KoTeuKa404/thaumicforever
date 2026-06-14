package com.example.coremod;

import java.util.UUID;

import com.koteuka404.thaumicforever.api.golemcore.GolemCoreHelper;
import com.koteuka404.thaumicforever.api.golemcore.GolemCoreRegistry;
import com.koteuka404.thaumicforever.api.golemcore.IGolemCore;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.golems.EntityThaumcraftGolem;

public final class GolemRenderScalePatch {
    private static final UUID GOLIATH_HEALTH_MODIFIER_ID = UUID.fromString("5f0ed87a-3cc5-43d1-a9a8-3b93356c5450");
    private static final UUID IRON_SKIN_ARMOR_MODIFIER_ID = UUID.fromString("d2ed5321-2f5c-482f-a996-9f2a27f1f6ad");
    private static final ResourceLocation IRON_SKIN_CORE_ID = new ResourceLocation("thaumicforever", "iron_skin");
    private static final ResourceLocation GOLEM_PLUG_TEXTURE = new ResourceLocation("thaumicforever", "textures/misc/golem_plug.png");

    private GolemRenderScalePatch() {
    }

    public static void applyScale(EntityThaumcraftGolem golem) {
        if (golem == null) return;
        IAttributeInstance maxHealth = golem.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(GOLIATH_HEALTH_MODIFIER_ID) != null) {
            GlStateManager.scale(1.5F, 1.5F, 1.5F);
        }
    }

    public static void renderCorePlug(EntityThaumcraftGolem golem) {
        if (hasIronSkinCore(golem)) {
            return;
        }

        IGolemCore core = getRenderedCore(golem);
        if (core == null) return;

        Minecraft.getMinecraft().getTextureManager().bindTexture(GOLEM_PLUG_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableCull();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(-2.0F, -2.0F);
        applyColor(core.getPlugColor());
        GlStateManager.translate(0.0F, 0.61F, -0.127F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float plugScale = core.getPlugScale();
        float halfWidth = 0.0625F * plugScale;
        float halfHeight = 0.046875F * plugScale;
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-halfWidth, -halfHeight, 0.0D).tex(0.0D, 1.0D).endVertex();
        buffer.pos(halfWidth, -halfHeight, 0.0D).tex(1.0D, 1.0D).endVertex();
        buffer.pos(halfWidth, halfHeight, 0.0D).tex(1.0D, 0.0D).endVertex();
        buffer.pos(-halfWidth, halfHeight, 0.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static boolean beginIronSkinOverlay(EntityThaumcraftGolem golem) {
        if (!hasIronSkinCore(golem)) {
            return false;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableCull();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(-2.0F, -2.0F);
        GlStateManager.depthMask(false);
        GlStateManager.color(0.70F, 0.76F, 0.80F, 0.64F);
        return true;
    }

    public static void endIronSkinOverlay() {
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void renderIronSkinPlugOverlay(EntityThaumcraftGolem golem) {
        if (!hasIronSkinCore(golem)) {
            return;
        }

        IGolemCore core = getRenderedCore(golem);
        float plugScale = core == null ? 1.0F : core.getPlugScale();
        float halfWidth = 0.0625F * plugScale;
        float halfHeight = 0.046875F * plugScale;

        GlStateManager.pushMatrix();
        GlStateManager.color(0.70F, 0.76F, 0.80F, 0.64F);
        GlStateManager.translate(0.0F, 0.61F, -0.127F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION);
        buffer.pos(-halfWidth, -halfHeight, 0.0D).endVertex();
        buffer.pos(halfWidth, -halfHeight, 0.0D).endVertex();
        buffer.pos(halfWidth, halfHeight, 0.0D).endVertex();
        buffer.pos(-halfWidth, halfHeight, 0.0D).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
    }

    private static IGolemCore getRenderedCore(EntityThaumcraftGolem golem) {
        IGolemCore core = GolemCoreRegistry.get(GolemCoreHelper.getActiveCoreId(golem));
        if (core != null) {
            return core;
        }

        for (IGolemCore registeredCore : GolemCoreRegistry.getValues()) {
            if (registeredCore.isActiveOnClient(golem)) {
                return registeredCore;
            }
        }
        return null;
    }

    private static void applyColor(int argb) {
        float alpha = ((argb >> 24) & 255) / 255.0F;
        float red = ((argb >> 16) & 255) / 255.0F;
        float green = ((argb >> 8) & 255) / 255.0F;
        float blue = (argb & 255) / 255.0F;
        GlStateManager.color(red, green, blue, alpha);
    }

    private static boolean hasIronSkinCore(EntityThaumcraftGolem golem) {
        if (golem == null) return false;
        ResourceLocation coreId = GolemCoreHelper.getActiveCoreId(golem);
        if (IRON_SKIN_CORE_ID.equals(coreId)) {
            return true;
        }

        IAttributeInstance armor = golem.getEntityAttribute(SharedMonsterAttributes.ARMOR);
        return armor != null && armor.getModifier(IRON_SKIN_ARMOR_MODIFIER_ID) != null;
    }

    private static boolean hasGoliathCore(EntityThaumcraftGolem golem) {
        if (golem == null) return false;
        IAttributeInstance maxHealth = golem.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        return maxHealth != null && maxHealth.getModifier(GOLIATH_HEALTH_MODIFIER_ID) != null;
    }
}
