package com.koteuka404.thaumicforever.compat;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import com.koteuka404.thaumicforever.config.ModConfig;
import com.koteuka404.thaumicforever.ThaumicForever;

public final class FlowerPotCompat {
    private static final Set<String> REGISTERED_POT_PLANTS = new HashSet<>();

    private FlowerPotCompat() {}

    public static void registerVanillaPotPlants() {
        REGISTERED_POT_PLANTS.clear();

        Method addPlant = findAddPlantMethod();
        if (addPlant == null) {
            ThaumicForever.LOGGER.warn("Could not find BlockFlowerPot.addPlant method. Skipping custom flower pot compatibility.");
            return;
        }

        if (ModConfig.vanillaPotAllowedPlants == null) {
            return;
        }

        for (String raw : ModConfig.vanillaPotAllowedPlants) {
            registerConfiguredPlant(addPlant, raw);
        }
    }

    public static boolean isRegisteredVanillaPotPlant(Block block, int meta) {
        if (block == null) {
            return false;
        }
        ResourceLocation key = block.getRegistryName();
        return key != null && REGISTERED_POT_PLANTS.contains(key.toString() + "@" + meta);
    }

    private static void registerConfiguredPlant(Method addPlant, String raw) {
        if (raw == null) {
            return;
        }

        String entry = raw.trim();
        if (entry.isEmpty()) {
            return;
        }

        int at = entry.indexOf('@');
        String idPart = at >= 0 ? entry.substring(0, at).trim() : entry;
        ResourceLocation blockId;
        try {
            blockId = new ResourceLocation(idPart);
        } catch (RuntimeException e) {
            ThaumicForever.LOGGER.warn("Skipping invalid flower pot config id '{}'", entry);
            return;
        }
        Block block = ForgeRegistries.BLOCKS.getValue(blockId);

        if (block == null || block == Blocks.AIR || block == Blocks.FLOWER_POT) {
            ThaumicForever.LOGGER.warn("Skipping invalid flower pot config plant '{}'", entry);
            return;
        }

        if (at >= 0) {
            try {
                registerBlockMeta(addPlant, block, Integer.parseInt(entry.substring(at + 1).trim()));
            } catch (NumberFormatException e) {
                ThaumicForever.LOGGER.warn("Skipping invalid flower pot config meta '{}'", entry);
            }
            return;
        }

        // No explicit meta in config means "allow all valid variants for this block".
        Set<Integer> seenMetas = new HashSet<>();
        registerBlockMeta(addPlant, block, 0);
        seenMetas.add(0);
        for (int meta = 1; meta < 16; meta++) {
            IBlockState state = safeStateFromMeta(block, meta);
            if (state == null) {
                continue;
            }
            int resolvedMeta = safeMeta(block, state);
            if (seenMetas.add(resolvedMeta)) {
                registerBlockMeta(addPlant, block, resolvedMeta);
            }
        }
    }

    private static Method findAddPlantMethod() {
        Class<?> cls = Blocks.FLOWER_POT.getClass();
        while (cls != null && cls != Object.class) {
            Method[] methods;
            try {
                methods = cls.getDeclaredMethods();
            } catch (Throwable t) {
                cls = cls.getSuperclass();
                continue;
            }

            for (Method m : methods) {
                try {
                    if (!"addPlant".equals(m.getName()) || m.getParameterCount() != 2) {
                        continue;
                    }
                    m.setAccessible(true);
                    return m;
                } catch (Throwable ignored) {
                }
            }

            cls = cls.getSuperclass();
        }
        return null;
    }

    private static void registerBlockMeta(Method addPlant, Block block, int meta) {
        if (block == null || block == Blocks.AIR || block == Blocks.FLOWER_POT) {
            return;
        }

        ResourceLocation key = block.getRegistryName();
        IBlockState state = safeStateFromMeta(block, meta);
        if (key == null || state == null) {
            return;
        }

        try {
            Class<?> p0 = addPlant.getParameterTypes()[0];
            Class<?> p1 = addPlant.getParameterTypes()[1];
            Object arg1 = buildSecondArg(p1, block, state);
            if (arg1 == null) {
                return;
            }

            boolean success = false;
            boolean exactMetaSuccess = meta <= 0;
            Object[] candidates = buildFirstArgCandidates(p0, block, state, key, meta);
            for (int i = 0; i < candidates.length; i++) {
                boolean invoked = tryInvoke(addPlant, candidates[i], arg1);
                success |= invoked;
                if (invoked && (p0 != String.class || i == 0)) {
                    exactMetaSuccess = true;
                }
            }

            if (success && exactMetaSuccess) {
                REGISTERED_POT_PLANTS.add(key.toString() + "@" + safeMeta(block, state));
            }
        } catch (Throwable ignored) {
        }
    }

    private static Object[] buildFirstArgCandidates(Class<?> p0, Block block, IBlockState state, ResourceLocation key, int meta) {
        if (p0 == String.class) {
            if (meta > 0) {
                return new Object[] { key.toString() + "@" + meta, key.toString() };
            }
            return new Object[] { key.toString() };
        }
        if (p0 == ResourceLocation.class) {
            return new Object[] { key };
        }
        if (p0 == ItemStack.class) {
            return new Object[] { new ItemStack(block, 1, safeMeta(block, state)) };
        }
        if (p0 == Item.class) {
            return new Object[] { Item.getItemFromBlock(block) };
        }
        if (p0 == Block.class) {
            return new Object[] { block };
        }
        return new Object[0];
    }

    private static boolean tryInvoke(Method addPlant, Object arg0, Object arg1) {
        if (arg0 == null || arg1 == null) {
            return false;
        }
        try {
            addPlant.invoke(Blocks.FLOWER_POT, arg0, arg1);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static Object buildSecondArg(Class<?> p1, Block block, IBlockState state) {
        if (IBlockState.class.isAssignableFrom(p1)) {
            return state;
        }
        if (p1 == Block.class) {
            return block;
        }
        if (p1 == ItemStack.class) {
            return new ItemStack(block, 1, safeMeta(block, state));
        }
        return null;
    }

    private static IBlockState safeStateFromMeta(Block block, int meta) {
        try {
            return block.getStateFromMeta(meta);
        } catch (Throwable t) {
            try {
                return block.getDefaultState();
            } catch (Throwable ignored) {
                return null;
            }
        }
    }

    private static int safeMeta(Block block, IBlockState state) {
        try {
            return block.getMetaFromState(state);
        } catch (Throwable t) {
            return 0;
        }
    }
}
