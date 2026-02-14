package com.koteuka404.thaumicforever;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID, value = Side.CLIENT)
public final class RareTooltipHandler {
    private static final int RARE_BG = 0xF0150B00;
    private static final int RARE_BORDER_START = 0xFFE0B35A;
    private static final int RARE_BORDER_END = 0xFF7A4A12;

    private RareTooltipHandler() {}

    @SubscribeEvent
    public static void onTooltipColor(RenderTooltipEvent.Color event) {
        if (!isRareStack(event.getStack())) return;

        event.setBackground(RARE_BG);
        event.setBorderStart(RARE_BORDER_START);
        event.setBorderEnd(RARE_BORDER_END);
    }

    private static boolean isRareStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (item == null) return false;

        if (item == ModItems.ItemGoldenFish) return true;
        return item == Item.getItemFromBlock(ModBlocks.BLUE_ROSE);
    }
}
