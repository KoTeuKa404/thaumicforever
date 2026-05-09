package com.koteuka404.thaumicforever.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import com.koteuka404.thaumicforever.tile.TileRechargePedestal;

@SideOnly(Side.CLIENT)
public class TileRechargePedestalRenderer extends TileEntitySpecialRenderer<TileRechargePedestal> {

    @Override
    public void render(TileRechargePedestal te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null) {
            return;
        }

        ItemStack wand = te.getWand();
        if (wand == null || wand.isEmpty()) {
            return;
        }

        EntityItem entityItem = new EntityItem(Minecraft.getMinecraft().world, 0.0D, 0.0D, 0.0D, wand.copy());
        entityItem.hoverStart = 0.0F;
        entityItem.getItem().setCount(1);

        float ticks = (Minecraft.getMinecraft().getRenderViewEntity() == null)
            ? 0.0F
            : (Minecraft.getMinecraft().getRenderViewEntity().ticksExisted + partialTicks);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
        GL11.glScaled(1.5D, 1.5D, 1.5D);
        GL11.glRotatef(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        renderManager.renderEntity(entityItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
        GL11.glPopMatrix();
    }
}
