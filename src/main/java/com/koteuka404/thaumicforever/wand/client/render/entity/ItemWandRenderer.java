package com.koteuka404.thaumicforever.wand.client.render.entity;

import com.koteuka404.thaumicforever.EntityAuraNode;
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
        if (mc.player != null && mc.player.getHeldItemMainhand() == item) {
            wielder = mc.player;
        } else if (mc.player != null && mc.player.getHeldItemOffhand() == item) {
            wielder = mc.player;
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
            boolean using = Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown();
            ItemStack main = player.getHeldItemMainhand();
            ItemStack off = player.getHeldItemOffhand();
            boolean holding = (!main.isEmpty() && main.getItem() == item.getItem())
                    || (!off.isEmpty() && off.getItem() == item.getItem());
            if (using && holding) {
                EntityAuraNode node = WandHelper.findNodeAlongLook(player, WandHelper.getNodeReach(player));
                if (node != null) {
                    float t = (wielder.ticksExisted + partialTicks) * 0.35f;
                    float orbitX = MathHelper.sin(t) * 12.0f;
                    float orbitY = MathHelper.cos(t) * 10.0f;
                    float roll = MathHelper.sin(t * 1.3f) * 18.0f;
                    float orbit = staff ? 0.08f : 0.06f;
                    float pivot = staff ? 0.9f : 0.75f;

                    GlStateManager.translate(0.0, pivot, 0.0);
                    GlStateManager.translate(MathHelper.sin(t) * orbit, MathHelper.cos(t) * orbit, 0.0f);
                    GlStateManager.rotate(orbitX, 1.0f, 0.0f, 0.0f);
                    GlStateManager.rotate(orbitY, 0.0f, 1.0f, 0.0f);
                    GlStateManager.rotate(roll, 0.0f, 0.0f, 1.0f);
                    GlStateManager.translate(0.0, -pivot, 0.0);
                }
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
