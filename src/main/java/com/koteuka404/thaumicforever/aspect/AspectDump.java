package com.koteuka404.thaumicforever.aspect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Locale;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import com.koteuka404.thaumicforever.config.ModConfig;
import com.koteuka404.thaumicforever.ThaumicForever;

public final class AspectDump {
    private AspectDump() {}

    public static void dumpAllItemAspects() {
        File outFile = resolveOutputFile();
        if (outFile == null) {
            ThaumicForever.LOGGER.warn("[AspectDump] Output file is null, skipping.");
            return;
        }

        int items = 0;
        int stacks = 0;
        int withAspects = 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
            writer.write("# ThaumicForever aspect dump");
            writer.newLine();
            writer.write("# format: registry_name@meta | display_name | aspects");
            writer.newLine();

            for (Item item : ForgeRegistries.ITEMS) {
                if (item == null) continue;
                ResourceLocation key = item.getRegistryName();
                if (key == null) continue;
                items++;

                NonNullList<ItemStack> sub = NonNullList.create();
                try {
                    item.getSubItems(CreativeTabs.SEARCH, sub);
                } catch (Throwable ignored) {
                }
                if (sub.isEmpty()) {
                    sub.add(new ItemStack(item));
                }

                for (ItemStack stack : sub) {
                    if (stack.isEmpty()) continue;
                    stacks++;

                    AspectList al = null;
                    try {
                        al = AspectHelper.getObjectAspects(stack);
                    } catch (Throwable ignored) {
                    }
                    if (al == null || al.size() == 0) {
                        try {
                            al = AspectHelper.generateTags(stack);
                        } catch (Throwable ignored) {
                        }
                    }
                    if (al == null || al.size() == 0) {
                        continue;
                    }
                    withAspects++;

                    String rn = key.toString();
                    int meta = stack.getMetadata();
                    String display = safeDisplayName(stack);
                    String aspects = formatAspects(al);

                    writer.write(rn);
                    writer.write("@");
                    writer.write(Integer.toString(meta));
                    writer.write(" | ");
                    writer.write(display);
                    writer.write(" | ");
                    writer.write(aspects);
                    writer.newLine();
                }
            }

            writer.newLine();
            writer.write(String.format(Locale.ROOT, "# items=%d stacks=%d with_aspects=%d", items, stacks, withAspects));
            writer.newLine();
        } catch (Throwable t) {
            ThaumicForever.LOGGER.warn("[AspectDump] Failed to write aspect dump.", t);
            return;
        }

        ThaumicForever.LOGGER.info("[AspectDump] Wrote {} item stacks ({} items) to {}", withAspects, items, outFile.getAbsolutePath());
    }

    private static File resolveOutputFile() {
        try {
            File configDir = Loader.instance().getConfigDir();
            if (configDir == null) return null;
            String name = ModConfig.aspectDumpFile == null || ModConfig.aspectDumpFile.trim().isEmpty()
                ? "aspect_dump.txt"
                : ModConfig.aspectDumpFile.trim();
            return new File(configDir, name);
        } catch (Throwable t) {
            return null;
        }
    }

    private static String safeDisplayName(ItemStack stack) {
        try {
            return stack.getDisplayName();
        } catch (Throwable t) {
            return "<unknown>";
        }
    }

    private static String formatAspects(AspectList al) {
        StringBuilder sb = new StringBuilder();
        Aspect[] aspects = al.getAspects();
        if (aspects == null) return "";
        for (int i = 0; i < aspects.length; i++) {
            Aspect a = aspects[i];
            if (a == null) continue;
            if (sb.length() > 0) sb.append(", ");
            sb.append(a.getTag()).append("=").append(al.getAmount(a));
        }
        return sb.toString();
    }
}
