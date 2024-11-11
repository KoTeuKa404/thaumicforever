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
    
    private ResourceLocation currentTexture = BOOK_TEXTURE; // Set initial texture
    private final int textureWidth = 255; // Adjust according to actual texture size
    private final int textureHeight = 250;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Render the book GUI
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
    
        // Check if the click is on the right side or left side of the book
        if (mouseX > rightX) {
            // Clicked on the right side, switch to BOOK_TEXTURE1
            currentTexture = BOOK_TEXTURE1;
        } else if (mouseX < rightX) {
            // Clicked on the left side, switch back to BOOK_TEXTURE
            currentTexture = BOOK_TEXTURE;
        }
    }
    

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
