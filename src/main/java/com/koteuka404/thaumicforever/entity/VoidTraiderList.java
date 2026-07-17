package com.koteuka404.thaumicforever.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.koteuka404.thaumicforever.config.ModConfig;

import net.minecraft.item.ItemStack;

public final class VoidTraiderList {
    private static final String DEFAULT_REQUIREMENT = "thaumicforever:coin*32";

    private static final List<String> TRADE_ITEMS = Arrays.asList(
        "thaumicforever:knowledge_epiphany{VOID_BEACON}",
        "thaumicforever:knowledge_epiphany{PRIMALTIMESTONE}",
        "thaumicforever:knowledge_epiphany{PRIMALTOOLS}",
        "thaumicforever:mask",
        "thaumicforever:ring_greedy",
        "thaumicforever:broken_future_lens",
        "thaumicforever:warp_blade",
        "thaumicforever:golem_core_primal",
        "thaumicforever:knowledge_epiphany{PRIMAL_RUPTURE}",
        "thaumicforever:knowledge_epiphany{TAINT_AMULET}",
        "thaumicforever:knowledge_epiphany{VOID_SINGULARITY}",
        "thaumicforever:re_trade",
        "thaumicforever:flux_lamp"
        
        

    );

    private static List<String> tradeItems = new ArrayList<>();
    private static final Map<String, List<String>> FIXED_TRADE_REQUIREMENTS = new LinkedHashMap<>();
    private static final Map<String, List<String>> TRADE_REQUIREMENTS = new LinkedHashMap<>();

    static {
        FIXED_TRADE_REQUIREMENTS.put("thaumicforever:knowledge_epiphany{VOID_BEACON}",
            Arrays.asList(
                "thaumicforever:lootbag{coins=224}",
                "minecraft:nether_star*1",
                "minecraft:dragon_breath*4"
            ));
        FIXED_TRADE_REQUIREMENTS.put("thaumicforever:mask",
            Arrays.asList(
                "thaumicforever:lootbag{coins=82}",
                "thaumicforever:orb_of_soul*1"
            ));
        FIXED_TRADE_REQUIREMENTS.put("thaumicforever:ring_greedy",
            Arrays.asList(
                "thaumicforever:lootbag{coins=176}",
                "thaumcraft:baubles;5*1"
            ));
        FIXED_TRADE_REQUIREMENTS.put("thaumicforever:broken_future_lens",
            Arrays.asList(
                "thaumicforever:lootbag{coins=160}",
                "minecraft:nether_star*1",
                "thaumcraft:goggles"

            ));
        FIXED_TRADE_REQUIREMENTS.put("thaumicforever:warp_blade",
            Arrays.asList(
                "thaumicforever:lootbag{coins=240}",
                "thaumicforever:primalingot*7",
                "thaumcraft:void_sword*1"
            ));
        FIXED_TRADE_REQUIREMENTS.put("thaumicforever:golem_core_primal",
            Arrays.asList(
                "thaumicforever:lootbag{coins=208}",
                "thaumicforever:nether_star_block*1",
                "thaumcraft:primordial_pearl*1"
            ));

        FIXED_TRADE_REQUIREMENTS.put("thaumicforever:knowledge_epiphany{PRIMALTIMESTONE}",
            Arrays.asList(
                "thaumicforever:lootbag{coins=446}",
                "thaumicforever:condensed_primal_pearl*1",
                "thaumicforever:matterya_block"
            ));
        FIXED_TRADE_REQUIREMENTS.put("thaumicforever:knowledge_epiphany{PRIMALTOOLS}",
            Arrays.asList(
                "thaumicforever:lootbag{coins=112}",
                "thaumicforever:primal_block*1",
                "thaumcraft:metal_void"
            ));
        FIXED_TRADE_REQUIREMENTS.put("thaumicforever:knowledge_epiphany{PRIMAL_RUPTURE}",
            Arrays.asList(
                "thaumicforever:lootbag{coins=144}",
                "thaumicforever:condensed_primal_pearl*1",
                "thaumicforever:nether_star_block*1"
            ));
        FIXED_TRADE_REQUIREMENTS.put("thaumicforever:knowledge_epiphany{TAINT_AMULET}",
            Arrays.asList(
                "thaumicforever:lootbag{coins=128}"
            ));
        FIXED_TRADE_REQUIREMENTS.put("thaumicforever:knowledge_epiphany{VOID_SINGULARITY}",
            Arrays.asList(
                "thaumicforever:lootbag{coins=384}",
                "thaumicforever:nether_star_block*4"

            ));

        FIXED_TRADE_REQUIREMENTS.put("thaumicforever:re_trade",
            Arrays.asList(
                "thaumicforever:lootbag{coins=900}"
            ));
    }

    private VoidTraiderList() {
    }

    public static void initializeFromConfig() {
        tradeItems = new ArrayList<>();
        TRADE_REQUIREMENTS.clear();

        if (ModConfig.voidTraiderTradeItems != null) {
            for (String entry : ModConfig.voidTraiderTradeItems) {
                addIfMissing(entry);
            }
        }

        for (String entry : TRADE_ITEMS) {
            addIfMissing(entry);
        }

        for (String entry : tradeItems) {
            List<String> fixedRequirements = FIXED_TRADE_REQUIREMENTS.get(entry);
            TRADE_REQUIREMENTS.put(entry, fixedRequirements == null
                ? Collections.singletonList(DEFAULT_REQUIREMENT)
                : new ArrayList<>(fixedRequirements));
        }
    }

    public static List<String> getAllTradeItems() {
        return new ArrayList<>(tradeItems);
    }

    public static List<String> getRequirements(ItemStack output) {
        if (output == null || output.isEmpty() || output.getItem().getRegistryName() == null) {
            return Collections.emptyList();
        }

        String outputId = output.getItem().getRegistryName().toString();
        String exactOutputKey = outputId;
        if (output.hasTagCompound() && output.getTagCompound().hasKey("research")) {
            exactOutputKey += "{" + output.getTagCompound().getString("research") + "}";
        }

        List<String> exactRequirements = TRADE_REQUIREMENTS.get(exactOutputKey);
        if (exactRequirements != null) {
            return new ArrayList<>(exactRequirements);
        }

        for (Map.Entry<String, List<String>> entry : TRADE_REQUIREMENTS.entrySet()) {
            String configuredId = entry.getKey();
            int separator = configuredId.indexOf('{');
            if (separator >= 0) configuredId = configuredId.substring(0, separator);
            separator = configuredId.indexOf('@');
            if (separator >= 0) configuredId = configuredId.substring(0, separator);
            separator = configuredId.indexOf('*');
            if (separator >= 0) configuredId = configuredId.substring(0, separator);
            if (outputId.equals(configuredId)) {
                return new ArrayList<>(entry.getValue());
            }
        }

        return Collections.singletonList(DEFAULT_REQUIREMENT);
    }

    private static void addIfMissing(String entry) {
        if (entry == null) {
            return;
        }

        String normalized = entry.trim();
        if (!normalized.isEmpty() && !tradeItems.contains(normalized)) {
            tradeItems.add(normalized);
        }
    }
}
