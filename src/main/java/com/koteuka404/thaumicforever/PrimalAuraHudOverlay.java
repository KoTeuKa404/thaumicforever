package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.ItemsTC;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = ThaumicForever.MODID, value = Side.CLIENT)
public final class PrimalAuraHudOverlay {

    private static final ResourceLocation HUD = new ResourceLocation("thaumcraft", "textures/gui/hud.png");

    private static final int REQUEST_EVERY_TICKS = 10;

    private static final int HUD_X = 20;
    private static final int HUD_Y = 20;

    private static final int CAPSULE_U = 72;
    private static final int CAPSULE_V = 48;
    private static final int CAPSULE_W = 16;
    private static final int CAPSULE_H = 78;

    private static final int NO_DATA_GRACE_TICKS = 10;
    private static final int RETRY_ON_MISS_EVERY_TICKS = 2;

    private static float[] lastGoodVis = null;
    private static long lastGoodTick = -1;

    private static long lastRequestTick = -9999;
    private static int lastReqDim = Integer.MIN_VALUE;
    private static int lastReqCx = Integer.MIN_VALUE;
    private static int lastReqCz = Integer.MIN_VALUE;

    private static final int LIQ_U = 88, LIQ_V = 52, LIQ_W = 8, LIQ_H = 72;

    private PrimalAuraHudOverlay() {}

    @SubscribeEvent
    public static void onOverlay(RenderGameOverlayEvent.Post e) {
        if (e.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.player == null) return;

        if (mc.gameSettings.hideGUI) return;
        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) return;

        EntityPlayer p = mc.player;
        if (!isHoldingThaumometer(p)) return;

        ScaledResolution sr = e.getResolution();
        int sw = sr.getScaledWidth();
        int sh = sr.getScaledHeight();

        requestIfNeeded(p);

        // ===== FIX: do NOT use chunkCoordX/Z (can desync on MP) =====
        int dim = p.dimension;
        int cx = MathHelper.floor(p.posX) >> 4;
        int cz = MathHelper.floor(p.posZ) >> 4;

        float[] vis = ClientPrimalAuraCache.get(dim, cx, cz);

        long nowTick = p.world.getTotalWorldTime();

        if (vis != null) {
            lastGoodVis = vis.clone();
            lastGoodTick = nowTick;
        } else if (lastGoodVis != null && lastGoodTick >= 0 && (nowTick - lastGoodTick) <= NO_DATA_GRACE_TICKS) {
            vis = lastGoodVis;
        }

        int HUD_OFFSET_Y = -15;
        int x = HUD_X;
        int y = HUD_Y + HUD_OFFSET_Y;

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + CAPSULE_W > sw) x = sw - CAPSULE_W;
        if (y + CAPSULE_H > sh) y = sh - CAPSULE_H;

        GlStateManager.pushMatrix();

        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        );
        GlStateManager.color(1f, 1f, 1f, 1f);

        mc.getTextureManager().bindTexture(HUD);

        int liqX = x + 1;
        int liqY = y;

        if (vis != null) {
            float sum = clamp0(vis[0]) + clamp0(vis[1]) + clamp0(vis[2]) + clamp0(vis[3]) + clamp0(vis[4]) + clamp0(vis[5]);

            final float MAX_SUM = 100f;
            float pctTotal = (MAX_SUM <= 0f) ? 0f : (sum / MAX_SUM);
            pctTotal = clamp01(pctTotal);

            int filledH = Math.round(LIQ_H * pctTotal);
            if (filledH < 0) filledH = 0;
            if (filledH > LIQ_H) filledH = LIQ_H;

            if (filledH > 0 && sum > 0.0001f) {
                int[] colors = new int[] {
                    Aspect.FIRE.getColor(),
                    Aspect.EARTH.getColor(),
                    Aspect.AIR.getColor(),
                    Aspect.WATER.getColor(),
                    Aspect.ORDER.getColor(),
                    Aspect.ENTROPY.getColor()
                };

                drawStackedLiquid256(
                    liqX, liqY,
                    LIQ_U, LIQ_V,
                    LIQ_W, LIQ_H,
                    filledH,
                    vis,
                    colors
                );

                GlStateManager.color(1f, 1f, 1f, 1f);
            }
        }

        drawTexturedRect256(x - 3, y - 4, CAPSULE_U, CAPSULE_V, CAPSULE_W, CAPSULE_H);

        if (vis != null && isShiftDown()) {
            int textX = x + CAPSULE_W + 2;
            int textY = y;
            drawVisNumbersColored(mc, vis, textX, textY, LIQ_H);
        }

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private static boolean isHoldingThaumometer(EntityPlayer p) {
        ItemStack main = p.getHeldItemMainhand();
        ItemStack off  = p.getHeldItemOffhand();
        return (main != null && main.getItem() == ItemsTC.thaumometer) ||
                (off != null && off.getItem() == ItemsTC.thaumometer);
    }

    private static void requestIfNeeded(EntityPlayer p) {
        long nowTick = p.world.getTotalWorldTime();

        // ===== FIX: do NOT use chunkCoordX/Z (can desync on MP) =====
        int dim = p.dimension;
        int cx = MathHelper.floor(p.posX) >> 4;
        int cz = MathHelper.floor(p.posZ) >> 4;

        boolean chunkChanged = (dim != lastReqDim) || (cx != lastReqCx) || (cz != lastReqCz);

        int period = REQUEST_EVERY_TICKS;
        if (chunkChanged) period = 0;

        if (lastGoodTick >= 0 && (nowTick - lastGoodTick) > NO_DATA_GRACE_TICKS) {
            period = Math.min(period, RETRY_ON_MISS_EVERY_TICKS);
        }

        if (period > 0 && (nowTick - lastRequestTick) < period) return;

        lastRequestTick = nowTick;
        lastReqDim = dim;
        lastReqCx = cx;
        lastReqCz = cz;

        if (ThaumicForever.network != null) {
            ThaumicForever.network.sendToServer(new PacketPrimalAuraRequest(dim, cx, cz));
        }
    }

    private static void drawTexturedRect256(int x, int y, int u, int v, int w, int h) {
        net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, 256f, 256f);
    }

    private static void drawStackedLiquid256(
        int x, int y,
        int u, int v,
        int w, int fullH,
        int filledH,
        float[] vis,
        int[] colors
    ) {
        int[] segH = new int[6];
        float sum = clamp0(vis[0]) + clamp0(vis[1]) + clamp0(vis[2]) + clamp0(vis[3]) + clamp0(vis[4]) + clamp0(vis[5]);
        if (sum <= 0.0001f) return;

        int sumH = 0;
        for (int i = 0; i < 6; i++) {
            float part = clamp0(vis[i]) / sum;
            int h = Math.round(filledH * part);
            if (h < 0) h = 0;
            segH[i] = h;
            sumH += h;
        }

        int diff = filledH - sumH;
        while (diff != 0) {
            int best = 0;
            float bestVal = -1f;
            for (int i = 0; i < 6; i++) {
                float val = clamp0(vis[i]);
                if (val > bestVal) { bestVal = val; best = i; }
            }

            if (diff > 0) {
                segH[best] += 1;
                diff -= 1;
            } else {
                int take = -1;
                for (int i = 0; i < 6; i++) {
                    if (segH[i] > 0) { take = i; break; }
                }
                if (take < 0) break;
                segH[take] -= 1;
                diff += 1;
            }
        }

        int filledTopY = y + (fullH - filledH);
        int drawnFromBottom = 0;

        for (int i = 5; i >= 0; i--) {
            int h = segH[i];
            if (h <= 0) continue;
        
            int segBottom = y + fullH - drawnFromBottom;
            int segTop = segBottom - h;
        
            if (segTop < filledTopY) {
                int cut = filledTopY - segTop;
                segTop += cut;
                h -= cut;
                if (h <= 0) { drawnFromBottom += segH[i]; continue; }
            }
        
            int uvTop = v + (segTop - y);
        
            int rgb = colors[i];
            float rr = ((rgb >> 16) & 0xFF) / 255f;
            float gg = ((rgb >> 8) & 0xFF) / 255f;
            float bb = (rgb & 0xFF) / 255f;
        
            GlStateManager.color(rr, gg, bb, 1f);
            drawTexturedRect256(x, segTop, u, uvTop, w, h);
        
            drawnFromBottom += segH[i];
        }
        

        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    private static float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }

    private static float clamp0(float v) {
        return v < 0f ? 0f : v;
    }

    private static boolean isShiftDown() {
        return org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_LSHIFT)
            || org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_RSHIFT);
    }

    private static void drawVisNumbersColored(Minecraft mc, float[] vis, int x, int y, int liqH) {
        Aspect[] aspects = new Aspect[] {
            Aspect.FIRE, Aspect.EARTH, Aspect.AIR, Aspect.WATER, Aspect.ORDER, Aspect.ENTROPY
        };

        final float SCALE = 0.65f;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(SCALE, SCALE, 1f);

        float span = (liqH - 1) / SCALE;

        final float TOP_PAD = 5f;
        final float BOTTOM_PAD = 2f;

        float usable = span - TOP_PAD - BOTTOM_PAD;
        if (usable < 1f) usable = 1f;

        float step = usable / 5.5f;

        for (int i = 0; i < 6; i++) {
            int drawIdx = i; // топ = FIRE, низ = ENTROPY
            float amt = clamp0(vis[drawIdx]);
            String s = format1(amt);
            int color = aspects[drawIdx].getColor() | 0xFF000000;
        
            int yy = Math.round(TOP_PAD + i * step);
            mc.fontRenderer.drawStringWithShadow(s, 0, yy, color);
        }
        

        GlStateManager.popMatrix();
    }

    private static String format1(float v) {
        return String.format(java.util.Locale.ROOT, "%.1f", v);
    }
}
