package com.koteuka404.thaumicforever;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class DoubleTableGui extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ThaumicForever.MODID, "textures/gui/guiresearchtable.png");

    public DoubleTableGui(Container container) {
        super(container);
        this.xSize = 256; // Width of the GUI
        this.ySize = 240; // Height of the GUI
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
