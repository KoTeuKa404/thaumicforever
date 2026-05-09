package com.koteuka404.thaumicforever.wand.client.render.entity;

import com.koteuka404.thaumicforever.entity.EntityAuraNode;
import com.koteuka404.thaumicforever.wand.api.item.wand.IStaff;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;
import com.koteuka404.thaumicforever.wand.client.model.ModelWand;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.GUI;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ItemWandRenderer extends TileEntityItemStackRenderer {

    private static final ModelWand model = new ModelWand();
    public static ItemCameraTransforms.TransformType transform = GUI;
    private static float mainAnimStrength = 0.0f;
    private static float offAnimStrength = 0.0f;
    private static long lastMainNodeTick = -200L;
    private static long lastOffNodeTick = -200L;
    private static final int ANIM_GRACE_TICKS = 8;
    private static final float ANIM_BLEND_UP = 0.20f;
    private static final float ANIM_BLEND_DOWN = 0.08f;
    private static long lastAnimNano = 0L;
    private static float mainPhase = 0.0f;
    private static float offPhase = 0.0f;
    private static boolean externalAnimActive = false;
    private static float externalPhase = 0.0f;
    private static float externalStrength = 0.0f;

    public static void beginExternalAnimation(float phase, float strength) {
        externalAnimActive = true;
        externalPhase = phase;
        externalStrength = MathHelper.clamp(strength, 0.0f, 1.0f);
    }

    public static void endExternalAnimation() {
        externalAnimActive = false;
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

    @Override
    public void renderByItem(ItemStack item, float partialTicks) {
        if (item.isEmpty() || !(item.getItem() instanceof IWand)) {
            return;
        }

        final IWand wand = (IWand) item.getItem();
        //final ItemStack focusStack = wand.getFocusItem(item);
        final boolean staff = wand instanceof IStaff;

        EntityLivingBase wielder = null;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null) {
            ItemStack main = mc.player.getHeldItemMainhand();
            ItemStack off = mc.player.getHeldItemOffhand();
            if (sameWandCore(main, item) || sameWandCore(off, item)) {
                wielder = mc.player;
            }
        }

        GlStateManager.pushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        if (staff) {
            GlStateManager.translate(0.0, 0.5, 0.0);
        }

        GlStateManager.translate(0.5, 1.5, 0.5);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);

        if (wielder instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) wielder;
            float t = 0.0f;
            float strength = 0.0f;

            if (externalAnimActive) {
                t = externalPhase;
                strength = externalStrength;
            } else {
                boolean using = Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown();
                ItemStack main = player.getHeldItemMainhand();
                ItemStack off = player.getHeldItemOffhand();
                boolean renderMain = sameWandCore(main, item);
                boolean renderOff = sameWandCore(off, item);
                long nowTick = player.world != null ? player.world.getTotalWorldTime() : 0L;

                if (using && (renderMain || renderOff)) {
                    EntityAuraNode node = WandHelper.findNodeAlongLook(player, WandHelper.getNodeReach(player));
                    if (node != null) {
                        if (renderMain) lastMainNodeTick = nowTick;
                        if (renderOff) lastOffNodeTick = nowTick;
                    }
                }

                boolean mainActive = renderMain && nowTick - lastMainNodeTick <= ANIM_GRACE_TICKS;
                boolean offActive = renderOff && nowTick - lastOffNodeTick <= ANIM_GRACE_TICKS;

                if (renderMain) {
                    mainAnimStrength += (mainActive ? ANIM_BLEND_UP : -ANIM_BLEND_DOWN);
                    mainAnimStrength = MathHelper.clamp(mainAnimStrength, 0.0f, 1.0f);
                }
                if (renderOff) {
                    offAnimStrength += (offActive ? ANIM_BLEND_UP : -ANIM_BLEND_DOWN);
                    offAnimStrength = MathHelper.clamp(offAnimStrength, 0.0f, 1.0f);
                }

                boolean preferMain = renderMain && (!renderOff || mainAnimStrength >= offAnimStrength);
                strength = preferMain ? mainAnimStrength : (renderOff ? offAnimStrength : 0.0f);
                if (strength > 0.001f) {
                    long nowNano = System.nanoTime();
                    if (lastAnimNano == 0L) lastAnimNano = nowNano;
                    float dt = (nowNano - lastAnimNano) / 1000000000.0f;
                    if (dt < 0.0f) dt = 0.0f;
                    if (dt > 0.1f) dt = 0.1f;
                    lastAnimNano = nowNano;

                    if (renderMain) mainPhase += dt * 2.2f;
                    if (renderOff) offPhase += dt * 2.2f;
                    t = preferMain ? mainPhase : offPhase;
                }
            }

            if (strength > 0.001f) {
                // Rotate around a low pivot so the wand base stays mostly still
                // and the tip is the part that visually moves.
                float orbitX = MathHelper.sin(t * 1.05f) * 7.5f * strength;
                float orbitY = MathHelper.cos(t * 1.15f) * 6.0f * strength;
                float roll = MathHelper.sin(t * 1.35f) * 10.0f * strength;
                float pivot = staff ? 1.05f : 0.78f;

                GlStateManager.translate(0.0, pivot, 0.0);
                GlStateManager.rotate(orbitX, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(orbitY, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(roll, 0.0f, 0.0f, 1.0f);
                GlStateManager.translate(0.0, -pivot, 0.0);
            }
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        model.render(item);

        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GL11.glPopAttrib();
    }
}
