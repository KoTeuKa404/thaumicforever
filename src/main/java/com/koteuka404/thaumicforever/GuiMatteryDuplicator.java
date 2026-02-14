package com.koteuka404.thaumicforever;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;

public class GuiMatteryDuplicator extends GuiContainer {

    private static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation("thaumicforever", "textures/gui/mattery_duplicator.png");

    private final InventoryPlayer playerInventory;
    private final TileEntityMatteryDuplicator tileEntity;
    // ---- POSITION OFFSETS ----
    private static final int VIS_OFFSET_X = 124;
    private static final int VIS_OFFSET_Y = 2;

    private static final int MATTERYA_OFFSET_X = 126;
    private static final int MATTERYA_OFFSET_Y = 58;

    // ---- SETTINGS ----
    private static final float ASPECT_SCALE = 0.95f;
    private static final float MATTERYA_SCALE = 1.0f;
    private static final float VIS_TEXT_SCALE = 0.5f;
    private static final int VIS_COLOR = 0x7EA5AD;
    private static final int ICON_SIZE = 16;

    public GuiMatteryDuplicator(InventoryPlayer playerInventory, TileEntityMatteryDuplicator tileEntity) {
        super(new ContainerMatteryDuplicator(playerInventory, tileEntity));
        this.playerInventory = playerInventory;
        this.tileEntity = tileEntity;
        this.xSize = 220;
        this.ySize = 220;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

        int essentia = tileEntity.getField(0);
        int visX100 = tileEntity.getField(1);

        int ignisX100 = tileEntity.getField(2);
        int terraX100 = tileEntity.getField(3);
        int aerX100 = tileEntity.getField(4);
        int aquaX100 = tileEntity.getField(5);
        int ordoX100 = tileEntity.getField(6);
        int perditioX100 = tileEntity.getField(7);

        int baseX = 6;
        int baseY = 8;

        // ---- VIS ----
        drawScaledString(
            formatX100(visX100) + " vis",
            baseX + VIS_OFFSET_X,
            baseY + VIS_OFFSET_Y,
            VIS_COLOR,
            VIS_TEXT_SCALE
        );

        // ---- MATTERYA ----
        Aspect matterya = Aspect.getAspect("matterya");
        if (matterya != null) {
        drawAspectIconWithCountScaled(
                baseX + MATTERYA_OFFSET_X,
                baseY + MATTERYA_OFFSET_Y,
                matterya,
                essentia * 100,
                MATTERYA_SCALE
        );
        }


        // ---- ASPECT ROW ----
        int rowY = baseY + 64;
        int rowX = baseX + 16;
        int step = 14;

        drawAspectIconWithCount(rowX + step * 0, rowY, Aspect.FIRE, ignisX100);
        drawAspectIconWithCount(rowX + step * 1, rowY, Aspect.EARTH, terraX100);
        drawAspectIconWithCount(rowX + step * 2, rowY, Aspect.AIR, aerX100);
        drawAspectIconWithCount(rowX + step * 3, rowY, Aspect.WATER, aquaX100);
        drawAspectIconWithCount(rowX + step * 4, rowY, Aspect.ORDER, ordoX100);
        drawAspectIconWithCount(rowX + step * 5, rowY, Aspect.ENTROPY, perditioX100);
    }

    // =========================================================

    private void drawAspectIconWithCount(int x, int y, Aspect aspect, int amountX100) {
        drawAspectIconWithCountScaled(x, y, aspect, amountX100, ASPECT_SCALE);
    }

    private void drawAspectIconWithCountScaled(int x, int y, Aspect aspect, int amountX100, float scale) {
        if (aspect == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1f);

        drawAspectIconTinted(0, 0, aspect);

        int count = x100ToInt(amountX100);
        if (count > 0) {
            String s = String.valueOf(count);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 200);
            GlStateManager.scale(0.5f, 0.5f, 1f);

            int sx = (int) ((ICON_SIZE - 1) / 0.5f);
            int sy = (int) ((ICON_SIZE - 1) / 0.5f);

            fontRenderer.drawStringWithShadow(
                    s,
                    sx - fontRenderer.getStringWidth(s),
                    sy - fontRenderer.FONT_HEIGHT,
                    0xFFFFFF
            );

            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }

    private void drawAspectIconTinted(int x, int y, Aspect aspect) {
        mc.getTextureManager().bindTexture(aspect.getImage());

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();

        int col = aspect.getColor();
        GlStateManager.color(
                ((col >> 16) & 255) / 255f,
                ((col >> 8) & 255) / 255f,
                (col & 255) / 255f,
                1f
        );

        net.minecraft.client.renderer.Tessellator tes = net.minecraft.client.renderer.Tessellator.getInstance();
        net.minecraft.client.renderer.BufferBuilder buf = tes.getBuffer();

        buf.begin(org.lwjgl.opengl.GL11.GL_QUADS,
                net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);

        buf.pos(x, y + 16, zLevel).tex(0, 1).endVertex();
        buf.pos(x + 16, y + 16, zLevel).tex(1, 1).endVertex();
        buf.pos(x + 16, y, zLevel).tex(1, 0).endVertex();
        buf.pos(x, y, zLevel).tex(0, 0).endVertex();

        tes.draw();

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }

    private void drawScaledString(String text, int x, int y, int color, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1f);
        fontRenderer.drawStringWithShadow(text, 0, 0, color);
        GlStateManager.popMatrix();
    }

    private static int x100ToInt(int v) {
        return v <= 0 ? 0 : (v + 50) / 100;
    }

    private static String formatX100(int v) {
        float value = v / 100.0f;
        return String.format(java.util.Locale.US, "%.0f", value);
    }
    
}
