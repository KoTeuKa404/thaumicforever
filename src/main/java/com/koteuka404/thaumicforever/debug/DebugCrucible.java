package com.koteuka404.thaumicforever.debug;

import com.koteuka404.thaumicforever.ThaumicForever;

import com.koteuka404.thaumicforever.config.ModConfig;

import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;

import java.io.File;
import java.net.URI;
import java.security.CodeSource;
import java.util.Map;

public class DebugCrucible {

    public static void onLoadComplete() {
        if (!ModConfig.debugCrucible) {
            return;
        }

        int replaced = 0;
        int foreignOutputs = 0;
        ResourceLocation targetKey = new ResourceLocation(ThaumicForever.MODID, "dragon_breath_recipe");
        ItemStack targetOutput = new ItemStack(Items.DRAGON_BREATH);

        for (Map.Entry<ResourceLocation, IThaumcraftRecipe> entry : ThaumcraftApi.getCraftingRecipes().entrySet()) {
            if (!(entry.getValue() instanceof CrucibleRecipe)) {
                continue;
            }
            CrucibleRecipe recipe = (CrucibleRecipe) entry.getValue();
            ResourceLocation key = entry.getKey();
            String owner = resolveOwner(entry.getValue().getClass());
            boolean fromThisMod = ThaumicForever.MODID.equals(owner);

            if (key != null && ThaumicForever.MODID.equals(key.getResourceDomain()) && !fromThisMod) {
                ThaumicForever.LOGGER.warn(
                    "[CrucibleDebug] Recipe key {} was replaced by mod {} (class {}).",
                    key, owner, entry.getValue().getClass().getName()
                );
                replaced++;
            }

            ItemStack output = recipe.getRecipeOutput();
            if (!output.isEmpty()) {
                ResourceLocation outId = output.getItem().getRegistryName();
                if (outId != null && ThaumicForever.MODID.equals(outId.getResourceDomain()) && !fromThisMod) {
                    ThaumicForever.LOGGER.warn(
                        "[CrucibleDebug] Foreign recipe outputs our item {}. Key={}, owner={}, class={}",
                        outId, key, owner, entry.getValue().getClass().getName()
                    );
                    foreignOutputs++;
                }
            }

            if (key != null && key.equals(targetKey)) {
                ThaumicForever.LOGGER.warn(
                    "[CrucibleDebug] dragon_breath_recipe now owned by {} (class {}). Output={}",
                    owner, entry.getValue().getClass().getName(), output
                );
            }

            if (!output.isEmpty() && output.isItemEqual(targetOutput) && !fromThisMod) {
                ThaumicForever.LOGGER.warn(
                    "[CrucibleDebug] DRAGON_BREATH recipe provided by {} (class {}). Key={}",
                    owner, entry.getValue().getClass().getName(), key
                );
            }
        }

        ThaumicForever.LOGGER.info(
            "[CrucibleDebug] Scan complete. replaced={}, foreign_outputs={}",
            replaced, foreignOutputs
        );
    }

    private static String resolveOwner(Class<?> clazz) {
        File source = resolveSourceFile(clazz);
        if (source != null) {
            String owner = findModIdBySource(source);
            if (owner != null) {
                return owner;
            }
            return source.getName();
        }
        return "unknown";
    }

    private static File resolveSourceFile(Class<?> clazz) {
        try {
            CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
            if (codeSource == null || codeSource.getLocation() == null) {
                return null;
            }
            URI uri = codeSource.getLocation().toURI();
            return new File(uri).getCanonicalFile();
        } catch (Exception e) {
            return null;
        }
    }

    private static String findModIdBySource(File source) {
        for (ModContainer mod : Loader.instance().getActiveModList()) {
            File modSource = mod.getSource();
            if (modSource == null) {
                continue;
            }
            try {
                if (modSource.getCanonicalFile().equals(source)) {
                    return mod.getModId();
                }
            } catch (Exception ignored) {
                if (modSource.getAbsolutePath().equals(source.getAbsolutePath())) {
                    return mod.getModId();
                }
            }
        }
        return null;
    }
}
