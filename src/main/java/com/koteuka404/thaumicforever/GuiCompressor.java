package com.koteuka404.thaumicforever;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiCompressor extends GuiContainer {

    private static final ResourceLocation TEXTURES = new ResourceLocation("thaumicforever", "textures/gui/compressor.png");
    private final TileEntityCompressor tileEntity;

    public GuiCompressor(InventoryPlayer player, TileEntityCompressor tileEntity) {
        super(new ContainerCompressor(player, tileEntity));
        this.tileEntity = tileEntity;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        List<ItemStack> plateOptions = tileEntity.getPlateOptions();
        if (plateOptions != null && !plateOptions.isEmpty()) {
            for (int i = 0; i < plateOptions.size(); i++) {
                ItemStack plate = plateOptions.get(i);
                this.itemRender.renderItemAndEffectIntoGUI(plate, 40 + (i * 20), 40);
                if (tileEntity.getSelectedPlate().isItemEqual(plate)) {
                    this.drawRect(40 + (i * 20) - 1, 40 - 1, 40 + (i * 20) + 17, 40 + 17, 0x80FF0000);
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURES);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    
        List<ItemStack> plateOptions = tileEntity.getPlateOptions();
        if (plateOptions != null && !plateOptions.isEmpty()) {
            for (int i = 0; i < plateOptions.size(); i++) {
                int xPos = this.guiLeft + 40 + (i * 20);
                int yPos = this.guiTop + 40;
    
                if (mouseX >= xPos && mouseX <= xPos + 16 && mouseY >= yPos && mouseY <= yPos + 16) {
                    ItemStack selectedPlate = plateOptions.get(i);
                    tileEntity.setSelectedPlate(selectedPlate);
    
                    PacketSelectPlate packet = new PacketSelectPlate(selectedPlate, tileEntity.getPos());
                    ThaumicForever.network.sendToServer(packet);
                    break;
                }
            }
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
}
