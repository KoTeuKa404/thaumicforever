package com.koteuka404.thaumicforever.client.gui;

import com.koteuka404.thaumicforever.registry.ModItems;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import com.koteuka404.thaumicforever.config.Config;
import com.koteuka404.thaumicforever.config.ModConfig;
import com.koteuka404.thaumicforever.ThaumicForever;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID, value = Side.CLIENT)
public class GuiTabHandler {
    private static final int BUTTON_ID = 90;

    @SubscribeEvent
    public static void onGuiInit(InitGuiEvent.Post event) {
        if (!ModConfig.enableMysticGuiButton) return;
        if (!(event.getGui() instanceof GuiContainer)) return;
        GuiContainer gui = (GuiContainer) event.getGui();

        if (gui instanceof GuiInventory || gui instanceof GuiMysticTabExample) {
            int xOffset = Config.TAB_BUTTON_X;
            ItemStack icon = new ItemStack(ModItems.bicon);
            event.getButtonList().add(
                new GuiItemToggleButton(BUTTON_ID, gui, xOffset, icon)
            );
        }
    }
}
