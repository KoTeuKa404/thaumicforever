package com.koteuka404.thaumicforever;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiMatteryDuplicator extends GuiContainer {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thaumicforever", "textures/gui/mattery_duplicator.png");
    private final InventoryPlayer playerInventory;
    private final TileEntityMatteryDuplicator tileEntity;

    public GuiMatteryDuplicator(InventoryPlayer playerInventory, TileEntityMatteryDuplicator tileEntity) {
        super(new ContainerMatteryDuplicator(playerInventory, tileEntity));
        this.playerInventory = playerInventory;
        this.tileEntity = tileEntity;
        this.xSize = 220; // Стандартна ширина GUI
        this.ySize = 220; // Стандартна висота GUI
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
}
