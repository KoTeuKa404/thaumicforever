package com.koteuka404.thaumicforever.client.gui;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.container.ContainerVoidChest;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiVoidChest extends GuiContainer {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ThaumicForever.MODID, "textures/gui/guivoidchest.png");
    private static final int GUI_Y_OFFSET = 0;

    public GuiVoidChest(InventoryPlayer playerInventory, IInventory chestInventory) {
        super(new ContainerVoidChest(playerInventory, chestInventory));
        this.xSize = 176;
        this.ySize = 256;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiTop += GUI_Y_OFFSET;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int left = this.guiLeft;
        int top = this.guiTop;
        this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);
    }
}
