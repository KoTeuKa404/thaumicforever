package com.koteuka404.thaumicforever.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.item.ItemKnowledgeEpiphany;
import com.koteuka404.thaumicforever.item.LootBag;
import com.koteuka404.thaumicforever.registry.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.ItemStackHandler;

public final class VoidTraiderPool {
    private VoidTraiderPool() {
    }

    static void fill(ItemStackHandler inventory, Random random) {
        List<ItemStack> pool = parseConfiguredItems();
        if (pool.isEmpty()) {
            return;
        }

        Collections.shuffle(pool, random);
        int offers = Math.min(inventory.getSlots(), pool.size());
        for (int slot = 0; slot < offers; slot++) {
            inventory.setStackInSlot(slot, pool.get(slot).copy());
        }
    }

    private static List<ItemStack> parseConfiguredItems() {
        List<ItemStack> result = new ArrayList<>();
        for (String rawEntry : VoidTraiderList.getAllTradeItems()) {
            ItemStack stack = parseTradeStack(rawEntry);
            if (!stack.isEmpty()) {
                result.add(stack);
            }
        }
        return result;
    }

    public static ItemStack parseTradeStack(String rawEntry) {
        if (rawEntry == null || rawEntry.trim().isEmpty()) {
            return ItemStack.EMPTY;
        }

        String entry = rawEntry.trim();
        String research = "";
        int count = 1;
        int metadata = 0;

        int researchSeparator = entry.indexOf('{');
        if (researchSeparator >= 0) {
            if (!entry.endsWith("}") || researchSeparator == entry.length() - 2) {
                warn(rawEntry);
                return ItemStack.EMPTY;
            }
            research = entry.substring(researchSeparator + 1, entry.length() - 1).trim();
            entry = entry.substring(0, researchSeparator).trim();
        }

        int countSeparator = entry.lastIndexOf('*');
        if (countSeparator >= 0) {
            try {
                count = Integer.parseInt(entry.substring(countSeparator + 1).trim());
            } catch (NumberFormatException exception) {
                warn(rawEntry);
                return ItemStack.EMPTY;
            }
            entry = entry.substring(0, countSeparator).trim();
        }

        int metadataSeparator = entry.lastIndexOf('@');
        if (metadataSeparator >= 0) {
            try {
                metadata = Integer.parseInt(entry.substring(metadataSeparator + 1).trim());
            } catch (NumberFormatException exception) {
                warn(rawEntry);
                return ItemStack.EMPTY;
            }
            entry = entry.substring(0, metadataSeparator).trim();
        }

        ResourceLocation itemId;
        try {
            itemId = new ResourceLocation(entry);
        } catch (RuntimeException exception) {
            warn(rawEntry);
            return ItemStack.EMPTY;
        }

        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        if (item == null || count < 1 || metadata < 0) {
            warn(rawEntry);
            return ItemStack.EMPTY;
        }

        count = Math.min(count, item.getItemStackLimit());
        if (item instanceof ItemKnowledgeEpiphany) {
            if (research.isEmpty()) {
                warn(rawEntry);
                return ItemStack.EMPTY;
            }
            return ItemKnowledgeEpiphany.create(item, research);
        }
        if (item == ModItems.lootbag) {
            if (!research.startsWith("coins=")) {
                warn(rawEntry);
                return ItemStack.EMPTY;
            }

            try {
                int storedCoins = Integer.parseInt(research.substring("coins=".length()).trim());
                if (storedCoins < 1) {
                    warn(rawEntry);
                    return ItemStack.EMPTY;
                }
                return LootBag.createStack(storedCoins);
            } catch (NumberFormatException exception) {
                warn(rawEntry);
                return ItemStack.EMPTY;
            }
        }
        if (!research.isEmpty()) {
            warn(rawEntry);
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, count, metadata);
    }

    private static void warn(String entry) {
        ThaumicForever.LOGGER.warn("Ignoring invalid Void Traider trade item config entry: {}", entry);
    }
}
