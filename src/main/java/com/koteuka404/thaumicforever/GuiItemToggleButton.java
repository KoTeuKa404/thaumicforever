package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;


public class GuiItemToggleButton extends GuiButton {
    private final GuiContainer parent;
    private final ItemStack icon;

    /**
     * @param buttonId 
     * @param parent    GUI (GuiInventory / GuiMysticTabExample)
     * @param xOffset  
     * @param icon     icon
     */
    public GuiItemToggleButton(int buttonId, GuiContainer parent, int xOffset, ItemStack icon) {
        super(
            buttonId,
            parent.getGuiLeft() + xOffset - 2,
            parent.getGuiTop()  + 5,
            18,
            18,
            ""
        );
        this.parent = parent;
        this.icon   = icon;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        GlStateManager.color(1f, 1f, 1f, 1f);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(icon, x, y);
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (!visible || !enabled) return false;
        if (mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height) {
            if (parent instanceof GuiMysticTabExample) {
                mc.displayGuiScreen(new GuiInventory(mc.player));
                NetworkHandler.INSTANCE.sendToServer(new PacketOpenNormalInventory());
            } else if (parent instanceof GuiInventory) {
                NetworkHandler.INSTANCE.sendToServer(new PacketOpenMysticTab());
            }
            return true;
        }
        return false;
    }
}
