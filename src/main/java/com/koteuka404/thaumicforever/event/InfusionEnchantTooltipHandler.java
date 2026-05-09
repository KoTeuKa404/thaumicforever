package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.recipe.EnumInfusionEnchantment;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InfusionEnchantTooltipHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack == null || stack.isEmpty()) {
            return;
        }

        List<String> infusionLines = new ArrayList<>();
        for (EnumInfusionEnchantment enchantment : EnumInfusionEnchantment.values()) {
            int level = EnumInfusionEnchantment.getInfusionEnchantmentLevel(stack, enchantment);
            if (level <= 0) {
                continue;
            }

            String name;
            switch (enchantment) {
                case VOIDREPAIR:
                    name = "Void Repair";
                    break;
                case POISON:
                    name = "Poison";
                    break;
                case RUBYPROTECT:
                    name = "Ruby Protection";
                    break;
                case BLEEDING:
                    name = "Bleeding";
                    break;
                case UNBREAKABLE:
                    name = "Unbreakable";
                    break;
                default:
                    name = enchantment.name();
                    break;
            }

            TextFormatting color = enchantment == EnumInfusionEnchantment.UNBREAKABLE
                ? TextFormatting.LIGHT_PURPLE
                : TextFormatting.GOLD;
            String line = color + name;
            if (enchantment.maxLevel > 1) {
                line += TextFormatting.GRAY + " Lvl " + level;
            }
            infusionLines.add(line);
        }

        if (infusionLines.isEmpty()) {
            return;
        }

        List<String> tooltip = event.getToolTip();
        int insertIndex = tooltip.isEmpty() ? 0 : 1;
        tooltip.addAll(insertIndex, infusionLines);
    }
}
