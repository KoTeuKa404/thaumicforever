package com.koteuka404.thaumicforever;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCrimsonBook extends GuiScreen {

    private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation("thaumicforever", "textures/gui/crims_book.png");
    private static final ResourceLocation BOOK_TEXTURE1 = new ResourceLocation("thaumicforever", "textures/gui/crims_book1.png");
    
    private ResourceLocation currentTexture = BOOK_TEXTURE;
    private final int textureWidth = 255; 
    private final int textureHeight = 250;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(currentTexture);
        int centerX = (this.width - textureWidth) / 2;
        int centerY = (this.height - textureHeight) / 2 ;
        this.drawTexturedModalRect(centerX, centerY, 0, 0, textureWidth, textureHeight);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        int centerX = (this.width - textureWidth) / 2;
        int rightX = centerX + textureWidth / 2;
        int centerY = (this.height - textureHeight) / 2 - 40;
    
        if (mouseX > rightX) {
            currentTexture = BOOK_TEXTURE1;
        } else if (mouseX < rightX) {
            currentTexture = BOOK_TEXTURE;
        }
    }
    

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
