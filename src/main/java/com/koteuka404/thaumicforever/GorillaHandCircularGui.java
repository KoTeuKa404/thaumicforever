package com.koteuka404.thaumicforever;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GorillaHandCircularGui extends GuiScreen {
    private static final ResourceLocation WHEEL_TEXTURE = new ResourceLocation("thaumicforever:textures/gui/circular_menu.png");
    private final List<ResourceLocation> icons = new ArrayList<>();
    private int selectedAction = -1;

    public GorillaHandCircularGui() {
        icons.add(new ResourceLocation("thaumicforever:textures/icons/action1.png"));
        icons.add(new ResourceLocation("thaumicforever:textures/icons/action2.png"));
        icons.add(new ResourceLocation("thaumicforever:textures/icons/action3.png"));
        icons.add(new ResourceLocation("thaumicforever:textures/icons/action4.png"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        Minecraft.getMinecraft().getTextureManager().bindTexture(WHEEL_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawModalRectWithCustomSizedTexture(centerX - 64, centerY - 64, 0, 0, 128, 128, 128, 128);

        int radius = 50;
        for (int i = 0; i < icons.size(); i++) {
            double angle = (2 * Math.PI / icons.size()) * i - Math.PI / 2;
            int iconX = (int) (centerX + radius * Math.cos(angle)) - 8;
            int iconY = (int) (centerY + radius * Math.sin(angle)) - 8;

            Minecraft.getMinecraft().getTextureManager().bindTexture(icons.get(i));
            this.drawModalRectWithCustomSizedTexture(iconX, iconY, 0, 0, 16, 16, 16, 16);

            if (mouseX >= iconX && mouseX <= iconX + 16 && mouseY >= iconY && mouseY <= iconY + 16) {
                selectedAction = i;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            if (selectedAction != -1) {
                System.out.println("Selected Action: " + selectedAction);
                this.mc.player.closeScreen(); 
            }
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }


    @Override
    public boolean doesGuiPauseGame() {
        return false; 
    }
}
