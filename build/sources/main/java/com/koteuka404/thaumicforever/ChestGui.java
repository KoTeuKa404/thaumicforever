package com.koteuka404.thaumicforever;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class ChestGui extends GuiContainer {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ThaumicForever.MODID, "textures/gui/abandoned_chest.png");
    private final InventoryPlayer playerInventory;
    private final IInventory tileEntity;

    public ChestGui(InventoryPlayer playerInventory, IInventory tileEntity) {
        super(new ContainerAbandonedChest(playerInventory, tileEntity));
        this.playerInventory = playerInventory;
        this.tileEntity = tileEntity;
        this.xSize = 256;  // Ширина вашого GUI
        this.ySize = 220;  // Висота вашого GUI
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Малюємо назву контейнера в центрі верхньої частини GUI
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(ThaumicForever.MODID, "textures/gui/abandoned_chest.png"));

        int guiLeft = (this.width - this.xSize) / 2 + 20;
        int guiTop = (this.height - this.ySize) / 2;

        // Adjust the guiTop to move the texture down (increase this number slightly, such as guiTop + 10)
        this.drawTexturedModalRect(guiLeft, guiTop + 5, 0, 0, this.xSize, this.ySize);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

}
