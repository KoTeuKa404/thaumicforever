package com.koteuka404.thaumicforever;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class DeconstructionTableGui extends GuiContainer {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ThaumicForever.MODID, "textures/gui/deconstruction_table.png");
    private final InventoryPlayer playerInventory;
    private final DeconstructionTableTileEntity tileEntity;

    private static final int MAX_PROGRESS_HEIGHT = 36;

    public DeconstructionTableGui(InventoryPlayer playerInventory, DeconstructionTableTileEntity tileEntity) {
        super(new DeconstructionTableContainer(playerInventory, tileEntity));
        this.playerInventory = playerInventory;
        this.tileEntity = tileEntity;
        this.xSize = 176;
        this.ySize = 166;
    }

    private int getProgressScaled(int pixels) {
        return (int) ((float) tileEntity.burnTime / 80.0F * pixels);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {}

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        if (tileEntity.getInputHandler().getStackInSlot(0).isEmpty()) {
            tileEntity.burnTime = 0;
        }

        if (tileEntity.isProcessing()) {
            int progress = getProgressScaled(MAX_PROGRESS_HEIGHT);
            this.drawTexturedModalRect(i + 93, j + 14 + (MAX_PROGRESS_HEIGHT - progress), 176, (MAX_PROGRESS_HEIGHT - progress), 8, progress + 12);

        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
