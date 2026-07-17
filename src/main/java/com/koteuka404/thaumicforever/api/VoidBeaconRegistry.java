package com.koteuka404.thaumicforever.api;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koteuka404.thaumicforever.ThaumicForever;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

/** Public and JSON-configurable output registry for the Void Beacon. */
public final class VoidBeaconRegistry {
    private static final String CONFIG_PATH = "thaumicforever/void_beacon_outputs.json";
    private static final String RESOLVED_PATH = "thaumicforever/void_beacon_resolved_outputs.json";
    private static final String DEFAULT_RESOURCE = "/assets/thaumicforever/config/void_beacon_outputs.json";
    private static final Map<Aspect, List<Entry>> ENTRIES = new HashMap<>();

    private VoidBeaconRegistry() {}

    public static void initializeDefaults() {
        ENTRIES.clear();
        for (Aspect aspect : Aspect.aspects.values()) ENTRIES.put(aspect, new ArrayList<>());

        File config = new File(Loader.instance().getConfigDir(), CONFIG_PATH);
        try {
            createDefaultConfig(config);
            loadConfig(config);
            writeResolvedOutputs(new File(Loader.instance().getConfigDir(), RESOLVED_PATH));
            ThaumicForever.LOGGER.info("Loaded {} Void Beacon output entries from {}", countEntries(), config);
        } catch (Exception error) {
            ThaumicForever.LOGGER.error("Failed to load Void Beacon outputs from {}", config, error);
        }
    }

    private static void createDefaultConfig(File config) throws Exception {
        if (config.isFile()) return;
        File parent = config.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Could not create config directory " + parent);
        }
        try (InputStream input = VoidBeaconRegistry.class.getResourceAsStream(DEFAULT_RESOURCE)) {
            if (input == null) throw new IllegalStateException("Missing default resource " + DEFAULT_RESOURCE);
            Files.copy(input, config.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void loadConfig(File config) throws Exception {
        JsonObject root;
        try (FileReader reader = new FileReader(config)) {
            root = new JsonParser().parse(reader).getAsJsonObject();
        }
        JsonArray outputs = root.getAsJsonArray("outputs");
        if (outputs == null) throw new IllegalArgumentException("Missing 'outputs' array");

        int index = 0;
        for (JsonElement element : outputs) {
            index++;
            if (!element.isJsonObject()) continue;
            JsonObject output = element.getAsJsonObject();
            if (output.has("enabled") && !output.get("enabled").getAsBoolean()) continue;
            try {
                loadOutput(output);
            } catch (Exception error) {
                ThaumicForever.LOGGER.warn("Ignoring invalid Void Beacon output #{}: {}", index, error.getMessage());
            }
        }
    }

    private static void writeResolvedOutputs(File outputFile) throws Exception {
        JsonObject root = new JsonObject();
        root.addProperty("generated", true);
        root.addProperty("description", "Generated view of the effective Void Beacon pools. Edit void_beacon_outputs.json instead.");
        JsonObject pools = new JsonObject();
        for (Aspect aspect : Aspect.aspects.values()) {
            List<Entry> entries = ENTRIES.get(aspect);
            if (entries == null || entries.isEmpty()) continue;
            JsonArray values = new JsonArray();
            for (Entry entry : entries) {
                JsonObject value = new JsonObject();
                ResourceLocation itemId = entry.stack.getItem().getRegistryName();
                value.addProperty("item", itemId == null ? "unknown" : itemId.toString());
                value.addProperty("metadata", entry.stack.getMetadata());
                value.addProperty("weight", entry.weight);
                values.add(value);
            }
            pools.add(aspect.getTag(), values);
        }
        root.add("aspects", pools);

        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Could not create config directory " + parent);
        }
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(outputFile.toPath()), StandardCharsets.UTF_8)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
        }
    }

    private static void loadOutput(JsonObject output) {
        String type = getRequiredString(output, "type");
        String id = getRequiredString(output, "id");
        AspectList override = readAspectOverride(output);
        if ("ore".equals(type)) {
            registerOre(id, override);
            return;
        }
        if (!"item".equals(type)) throw new IllegalArgumentException("unknown type '" + type + "'");

        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item == null) throw new IllegalArgumentException("unknown item '" + id + "'");
        if (output.has("metadata")) {
            JsonElement metadata = output.get("metadata");
            if (metadata.isJsonArray()) {
                for (JsonElement meta : metadata.getAsJsonArray()) register(new ItemStack(item, 1, meta.getAsInt()), override);
            } else {
                register(new ItemStack(item, 1, metadata.getAsInt()), override);
            }
        } else if (output.has("all_metadata") && output.get("all_metadata").getAsBoolean()) {
            for (int meta = 0; meta < 16; meta++) register(new ItemStack(item, 1, meta), override);
        } else {
            register(new ItemStack(item), override);
        }
    }

    private static AspectList readAspectOverride(JsonObject output) {
        if (!output.has("aspects")) return null;
        AspectList aspects = new AspectList();
        for (Map.Entry<String, JsonElement> value : output.getAsJsonObject("aspects").entrySet()) {
            Aspect aspect = Aspect.getAspect(value.getKey());
            if (aspect == null) throw new IllegalArgumentException("unknown aspect '" + value.getKey() + "'");
            aspects.add(aspect, value.getValue().getAsInt());
        }
        return aspects;
    }

    private static String getRequiredString(JsonObject object, String key) {
        if (!object.has(key)) throw new IllegalArgumentException("missing '" + key + "'");
        return object.get(key).getAsString();
    }

    public static void registerOre(String oreName) {
        registerOre(oreName, null);
    }

    private static void registerOre(String oreName, AspectList override) {
        if (!OreDictionary.doesOreNameExist(oreName)) {
            ThaumicForever.LOGGER.debug("Void Beacon ore entry '{}' is not registered", oreName);
            return;
        }
        for (ItemStack stack : OreDictionary.getOres(oreName, false)) register(stack, override);
    }

    public static void register(ItemStack source) {
        register(source, null);
    }

    private static void register(ItemStack source, AspectList override) {
        if (source == null || source.isEmpty()) return;
        ItemStack stack = source.copy();
        stack.setCount(1);
        AspectList aspects = override == null ? AspectHelper.getObjectAspects(stack) : override;
        if (aspects == null || aspects.size() == 0) return;

        for (Aspect aspect : aspects.getAspects()) {
            int weight = aspects.getAmount(aspect);
            if (weight <= 0) continue;
            List<Entry> list = ENTRIES.computeIfAbsent(aspect, key -> new ArrayList<>());
            boolean duplicate = false;
            for (Entry entry : list) {
                if (ItemStack.areItemStacksEqual(entry.stack, stack)) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) list.add(new Entry(stack.copy(), weight));
        }
    }

    public static ItemStack choose(Aspect aspect, java.util.Random random) {
        List<Entry> list = ENTRIES.get(aspect);
        if (list == null || list.isEmpty()) return ItemStack.EMPTY;
        int total = 0;
        for (Entry entry : list) total += entry.weight;
        if (total <= 0) return ItemStack.EMPTY;
        int roll = random.nextInt(total);
        for (Entry entry : list) {
            roll -= entry.weight;
            if (roll < 0) return entry.stack.copy();
        }
        return ItemStack.EMPTY;
    }

    public static List<ItemStack> getOutputs(Aspect aspect) {
        List<Entry> list = ENTRIES.get(aspect);
        if (list == null) return Collections.emptyList();
        List<ItemStack> outputs = new ArrayList<>();
        for (Entry entry : list) outputs.add(entry.stack.copy());
        return Collections.unmodifiableList(outputs);
    }

    private static int countEntries() {
        int count = 0;
        for (List<Entry> entries : ENTRIES.values()) count += entries.size();
        return count;
    }

    private static final class Entry {
        private final ItemStack stack;
        private final int weight;

        private Entry(ItemStack stack, int weight) {
            this.stack = stack;
            this.weight = weight;
        }
    }
}
