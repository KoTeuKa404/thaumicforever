package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;

public interface ITab extends Comparable<ITab> {
    int getId();
    int getWidth();
    int getHeight();

    boolean shouldAddToList(GuiContainer gui);

    void init(GuiContainer gui);

    void draw(GuiContainer gui, int mouseX, int mouseY, float partialTicks);

    void onClick(Minecraft mc);
}
