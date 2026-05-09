package com.koteuka404.thaumicforever.wand.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.koteuka404.thaumicforever.entity.EntityAuraNode;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;
import com.koteuka404.thaumicforever.wand.util.WandHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aspects.Aspect;

public class WandHandChargeRenderer {

    private static final float ANIM_BLEND_UP = 0.18f;
    private static final float ANIM_BLEND_DOWN = 0.10f;
    private static final float ANIM_SPEED = 2.2f;
    private static final int NODE_GRACE_TICKS = 8;
    private static final ResourceLocation WISPY_TEX = new ResourceLocation("thaumcraft", "textures/misc/wispy.png");

    private static final HandAnimState MAIN = new HandAnimState();
    private static final HandAnimState OFF = new HandAnimState();
    private static final BeamState BEAM = new BeamState();

    private static final class HandAnimState {
        float strength;
        float phase;
        long lastNano;
        long lastNodeTick = -200L;
        boolean wasCharging;
        ItemStack cachedRenderStack = ItemStack.EMPTY;
    }

    private static final class BeamState {
        EntityAuraNode node;
        EnumHandSide handSide;
        float strength;
        float phase;
        long lastTick;
    }

    @SubscribeEvent
    public void onRenderSpecificHand(RenderSpecificHandEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.player == null || mc.world == null) return;

        EntityPlayer player = mc.player;
        EnumHand hand = event.getHand();
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof IWand)) return;

        ItemStack held = player.getHeldItem(hand);
        if (!sameWandCore(held, stack)) return;

        boolean useDown = mc.gameSettings != null && mc.gameSettings.keyBindUseItem.isKeyDown();
        boolean activeThisHand = player.isHandActive()
                && player.getActiveHand() == hand
                && sameWandCore(player.getActiveItemStack(), held);
        HandAnimState state = hand == EnumHand.MAIN_HAND ? MAIN : OFF;
        long nowTick = player.world != null ? player.world.getTotalWorldTime() : 0L;
        boolean full = WandHelper.isWandFullyCharged(held, player);
        if (useDown && !full) {
            EntityAuraNode node = WandHelper.findNodeAlongLook(player, WandHelper.getNodeReach(player));
            if (node != null) {
                state.lastNodeTick = nowTick;
            }
        }
        boolean nodeRecentlySeen = nowTick - state.lastNodeTick <= NODE_GRACE_TICKS;
        boolean charging = !full && nodeRecentlySeen && (activeThisHand || useDown);

        updateAnimState(state, charging, held);
        if (state.strength <= 0.001f) return;

        event.setCanceled(true);
        EnumHandSide handSide = hand == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        ItemCameraTransforms.TransformType transform = handSide == EnumHandSide.RIGHT
                ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
        boolean leftHand = handSide == EnumHandSide.LEFT;
        ItemStack renderStack = state.cachedRenderStack.isEmpty() ? held : state.cachedRenderStack;

        if (charging) {
            EntityAuraNode node = WandHelper.findNodeAlongLook(player, WandHelper.getNodeReach(player));
            if (node != null) {
                BEAM.node = node;
                BEAM.handSide = handSide;
                BEAM.strength = state.strength;
                BEAM.phase = state.phase;
                BEAM.lastTick = nowTick;
            }
        }

        GlStateManager.pushMatrix();
        // Do not depend on vanilla hand equip/swing progress here:
        // those values can jump when held stack NBT is synced each tick.
        transformSideFirstPerson(0.0F, handSide);
        transformFirstPerson(handSide, 0.0F);
        float side = handSide == EnumHandSide.RIGHT ? 1.0f : -1.0f;
        float strength = state.strength;
        // Pull the wand tip more toward the looked-at node while keeping handle stable.
        GlStateManager.rotate(-20.0f * strength, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(10.0f * side * strength, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-6.0f * side * strength, 0.0f, 0.0f, 1.0f);
        ItemWandRenderer.beginExternalAnimation(state.phase, state.strength);
        try {
            mc.getItemRenderer().renderItemSide(player, renderStack, transform, leftHand);
        } finally {
            ItemWandRenderer.endExternalAnimation();
            GlStateManager.popMatrix();
        }
    }

    private static void updateAnimState(HandAnimState state, boolean charging, ItemStack held) {
        long now = System.nanoTime();
        if (state.lastNano == 0L) state.lastNano = now;
        float dt = (now - state.lastNano) / 1000000000.0f;
        state.lastNano = now;
        if (dt < 0.0f) dt = 0.0f;
        if (dt > 0.1f) dt = 0.1f;

        if (charging) {
            if (!state.wasCharging || state.cachedRenderStack.isEmpty() || !sameWandCore(state.cachedRenderStack, held)) {
                state.cachedRenderStack = held.copy();
            }
            state.phase += dt * ANIM_SPEED;
            state.strength = MathHelper.clamp(state.strength + ANIM_BLEND_UP, 0.0f, 1.0f);
        } else {
            state.strength = MathHelper.clamp(state.strength - ANIM_BLEND_DOWN, 0.0f, 1.0f);
            if (state.strength <= 0.001f) {
                state.cachedRenderStack = ItemStack.EMPTY;
            }
        }
        state.wasCharging = charging;
    }

    private static boolean sameWandCore(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) return false;
        if (a.getItem() != b.getItem()) return false;
        String aCap = a.hasTagCompound() ? a.getTagCompound().getString("cap") : "";
        String bCap = b.hasTagCompound() ? b.getTagCompound().getString("cap") : "";
        if (!aCap.equals(bCap)) return false;
        String aRod = a.hasTagCompound() ? a.getTagCompound().getString("rod") : "";
        String bRod = b.hasTagCompound() ? b.getTagCompound().getString("rod") : "";
        return aRod.equals(bRod);
    }

    private static void transformSideFirstPerson(float equipProgress, EnumHandSide handSide) {
        int dir = handSide == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(dir * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
    }

    private static void transformFirstPerson(EnumHandSide handSide, float swingProgress) {
        int dir = handSide == EnumHandSide.RIGHT ? 1 : -1;
        float swing1 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float swing2 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(dir * (45.0F + swing1 * -20.0F), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(dir * swing2 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(swing2 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(dir * -45.0F, 0.0F, 1.0F, 0.0F);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.player == null || mc.world == null) return;
        EntityPlayer player = mc.player;
        long nowTick = player.world.getTotalWorldTime();
        if (BEAM.node == null || BEAM.node.isDead) return;
        if (nowTick - BEAM.lastTick > 1) return;
        renderChargeBeam(mc, player, BEAM.handSide, BEAM.node, BEAM.strength, BEAM.phase, event.getPartialTicks());
    }

    private static void renderChargeBeam(Minecraft mc, EntityPlayer player, EnumHandSide handSide, EntityAuraNode node, float strength, float phase, float partialTicks) {
        if (mc == null || node == null) return;

        Vec3d eye = player.getPositionEyes(partialTicks);
        Vec3d look = player.getLook(partialTicks).normalize();
        Vec3d up = new Vec3d(0.0D, 1.0D, 0.0D);
        Vec3d right = look.crossProduct(up).normalize();
        if (right.lengthSquared() < 1.0E-4D) {
            right = new Vec3d(1.0D, 0.0D, 0.0D);
        }
        Vec3d upOrtho = right.crossProduct(look).normalize();
        // Apply the same rotation used for the wand animation to align the beam start.
        float side = handSide == EnumHandSide.RIGHT ? 1.0f : -1.0f;
        float angX = (float) Math.toRadians(-20.0f * strength);
        float angY = (float) Math.toRadians(10.0f * side * strength);
        float angZ = (float) Math.toRadians(-6.0f * side * strength);
        // rotate around right (X)
        look = rotateAroundAxis(look, right, angX);
        upOrtho = rotateAroundAxis(upOrtho, right, angX);
        // rotate around up (Y)
        look = rotateAroundAxis(look, upOrtho, angY);
        right = rotateAroundAxis(right, upOrtho, angY);
        // rotate around forward (Z)
        right = rotateAroundAxis(right, look, angZ);
        upOrtho = rotateAroundAxis(upOrtho, look, angZ);
        double sideD = handSide == EnumHandSide.RIGHT ? 1.0D : -1.0D;
        double bob = Math.sin(phase * 1.3) * 0.022 * strength;
        Vec3d start = eye
                .add(look.scale(0.36D))
                .add(right.scale(0.30D * sideD))
                .add(upOrtho.scale(-0.0D + bob));

        Vec3d end = new Vec3d(
                node.prevPosX + (node.posX - node.prevPosX) * partialTicks,
                node.prevPosY + (node.posY - node.prevPosY) * partialTicks + 0.08D,
                node.prevPosZ + (node.posZ - node.prevPosZ) * partialTicks
        );

        Aspect asp = node.getMainAspect();
        if (asp == null) {
            try {
                Aspect[] sorted = node.getOriginalAspects().getAspectsSortedByAmount();
                if (sorted != null && sorted.length > 0) asp = sorted[0];
            } catch (Throwable ignored) {}
        }
        if (asp == null) {
            asp = node.getAspect();
        }
        int color = asp != null ? asp.getColor() : 0xFFFFFF;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = 0.9f * MathHelper.clamp(strength, 0.0f, 1.0f);
        float beamWidth = 0.035f + 0.02f * strength;

        float time = (mc.world != null ? mc.world.getTotalWorldTime() : 0) + partialTicks;
        renderBeamWorld(mc, start, end, r, g, b, a, beamWidth, time);
    }

    private static void renderBeamWorld(Minecraft mc, Vec3d start, Vec3d end, float r, float g, float b, float a, float beamWidth, float time) {
        double sx = start.x;
        double sy = start.y;
        double sz = start.z;
        double ex = end.x;
        double ey = end.y;
        double ez = end.z;
        double dx = ex - sx, dy = ey - sy, dz = ez - sz;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 0.0001) return;

        double cx = mc.getRenderManager().viewerPosX;
        double cy = mc.getRenderManager().viewerPosY;
        double cz = mc.getRenderManager().viewerPosZ;
        double midx = (sx + ex) / 2.0, midy = (sy + ey) / 2.0, midz = (sz + ez) / 2.0;
        double vx = cx - midx, vy = cy - midy, vz = cz - midz;
        double bx = dx / dist, by = dy / dist, bz = dz / dist;
        double wx = vy * bz - vz * by;
        double wy = vz * bx - vx * bz;
        double wz = vx * by - vy * bx;
        double wlen = Math.sqrt(wx * wx + wy * wy + wz * wz);
        if (wlen > 0.0001) { wx /= wlen; wy /= wlen; wz /= wlen; }
        wx *= beamWidth; wy *= beamWidth; wz *= beamWidth;

        double hx = -dz / dist * beamWidth;
        double hy = 0;
        double hz = dx / dist * beamWidth;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-cx, -cy, -cz);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.depthMask(false);

        mc.getTextureManager().bindTexture(WISPY_TEX);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        // Wispy ribbon: loose 3D wave around the axis (TC4-like).
        int segments = 22;
        double dirX = dx / dist;
        double dirY = dy / dist;
        double dirZ = dz / dist;
        // Build an orthonormal basis around the beam direction.
        double ax = 0.0, ay = 1.0, az = 0.0;
        if (Math.abs(dirY) > 0.95) {
            ax = 1.0; ay = 0.0; az = 0.0;
        }
        double v1x = dirY * az - dirZ * ay;
        double v1y = dirZ * ax - dirX * az;
        double v1z = dirX * ay - dirY * ax;
        double v1len = Math.sqrt(v1x * v1x + v1y * v1y + v1z * v1z);
        if (v1len > 0.0001) { v1x /= v1len; v1y /= v1len; v1z /= v1len; }
        double v2x = dirY * v1z - dirZ * v1y;
        double v2y = dirZ * v1x - dirX * v1z;
        double v2z = dirX * v1y - dirY * v1x;
        double v2len = Math.sqrt(v2x * v2x + v2y * v2y + v2z * v2z);
        if (v2len > 0.0001) { v2x /= v2len; v2y /= v2len; v2z /= v2len; }

        double amp = beamWidth * 3.6;
        double freq1 = 4.0;
        double freq2 = 6.5;
        double speed1 = 0.16;
        double speed2 = -0.11;
        double jitterSpeed = 0.35;

        for (int i = 0; i < segments; i++) {
            double t0 = (double) i / segments;
            double t1 = (double) (i + 1) / segments;

            double taper0 = Math.sin(t0 * Math.PI);
            double taper1 = Math.sin(t1 * Math.PI);
            double segSeed0 = (i * 31.1);
            double segSeed1 = ((i + 1) * 31.1);
            double jitter0 = Math.sin(time * jitterSpeed + segSeed0) * amp * 0.35;
            double jitter1 = Math.sin(time * jitterSpeed + segSeed1) * amp * 0.35;

            double offA0 = Math.sin(t0 * freq1 + time * speed1 + jitter0) * amp * taper0;
            double offB0 = Math.cos(t0 * freq2 + time * speed2 + jitter0 * 0.7) * amp * 0.65 * taper0;
            double offA1 = Math.sin(t1 * freq1 + time * speed1 + jitter1) * amp * taper1;
            double offB1 = Math.cos(t1 * freq2 + time * speed2 + jitter1 * 0.7) * amp * 0.65 * taper1;

            double s0x = sx + dx * t0 + v1x * offA0 + v2x * offB0;
            double s0y = sy + dy * t0 + v1y * offA0 + v2y * offB0;
            double s0z = sz + dz * t0 + v1z * offA0 + v2z * offB0;
            double s1x = sx + dx * t1 + v1x * offA1 + v2x * offB1;
            double s1y = sy + dy * t1 + v1y * offA1 + v2y * offB1;
            double s1z = sz + dz * t1 + v1z * offA1 + v2z * offB1;

            // Face the camera for each segment.
            double midx2 = (s0x + s1x) * 0.5;
            double midy2 = (s0y + s1y) * 0.5;
            double midz2 = (s0z + s1z) * 0.5;
            double cvx = cx - midx2, cvy = cy - midy2, cvz = cz - midz2;
            double sdx = s1x - s0x, sdy = s1y - s0y, sdz = s1z - s0z;
            double sdist = Math.sqrt(sdx * sdx + sdy * sdy + sdz * sdz);
            if (sdist < 0.0001) continue;
            sdx /= sdist; sdy /= sdist; sdz /= sdist;
            double rx = cvy * sdz - cvz * sdy;
            double ry = cvz * sdx - cvx * sdz;
            double rz = cvx * sdy - cvy * sdx;
            double rlen = Math.sqrt(rx * rx + ry * ry + rz * rz);
            if (rlen > 0.0001) { rx /= rlen; ry /= rlen; rz /= rlen; }
            rx *= beamWidth * 1.4;
            ry *= beamWidth * 1.4;
            rz *= beamWidth * 1.4;

            float uScroll = (float) (time * 0.03);
            float u0 = (float) (t0 * 2.5) + uScroll;
            float u1 = (float) (t1 * 2.5) + uScroll;

            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buf.pos(s0x - rx, s0y - ry, s0z - rz).tex(u0, 0).color(r, g, b, a).endVertex();
            buf.pos(s1x - rx, s1y - ry, s1z - rz).tex(u1, 0).color(r, g, b, a).endVertex();
            buf.pos(s1x + rx, s1y + ry, s1z + rz).tex(u1, 1).color(r, g, b, a).endVertex();
            buf.pos(s0x + rx, s0y + ry, s0z + rz).tex(u0, 1).color(r, g, b, a).endVertex();
            tess.draw();
        }

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    private static Vec3d rotateAroundAxis(Vec3d v, Vec3d axis, float angle) {
        double x = v.x;
        double y = v.y;
        double z = v.z;
        double ux = axis.x;
        double uy = axis.y;
        double uz = axis.z;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double dot = ux * x + uy * y + uz * z;

        double rx = ux * dot * (1 - cos) + x * cos + (-uz * y + uy * z) * sin;
        double ry = uy * dot * (1 - cos) + y * cos + (uz * x - ux * z) * sin;
        double rz = uz * dot * (1 - cos) + z * cos + (-uy * x + ux * y) * sin;
        return new Vec3d(rx, ry, rz);
    }
}
