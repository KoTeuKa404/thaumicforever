package com.koteuka404.thaumicforever.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientFutureDropCache {

    private static final long DROP_CACHE_TIME_MS = 15000L;

    private static final long HARD_EXPIRE_TIME_MS = 60000L;

    private static final long REQUEST_COOLDOWN_MS = 3000L;

    private static final int MAX_CACHED_DROPS = 12;

    private static final Map<Integer, Entry> CACHE = new HashMap<>();
    private static final Map<Integer, Long> REQUESTS = new HashMap<>();

    private ClientFutureDropCache() {}

    public static boolean isFresh(int entityId) {
        Entry entry = CACHE.get(entityId);

        if (entry == null) {
            return false;
        }

        return System.currentTimeMillis() - entry.time <= DROP_CACHE_TIME_MS;
    }

    public static List<ItemStack> getDrops(int entityId) {
        Entry entry = CACHE.get(entityId);

        if (entry == null) {
            return Collections.emptyList();
        }

        if (System.currentTimeMillis() - entry.time > HARD_EXPIRE_TIME_MS) {
            CACHE.remove(entityId);
            REQUESTS.remove(entityId);
            return Collections.emptyList();
        }

        return entry.drops;
    }

    public static boolean canRequest(int entityId) {
        Long last = REQUESTS.get(entityId);

        if (last == null) {
            return true;
        }

        return System.currentTimeMillis() - last >= REQUEST_COOLDOWN_MS;
    }

    public static void markRequested(int entityId) {
        REQUESTS.put(entityId, System.currentTimeMillis());
    }

    public static void put(int entityId, List<ItemStack> drops) {
        Entry oldEntry = CACHE.get(entityId);

        if ((drops == null || drops.isEmpty()) && oldEntry != null) {
            return;
        }

        List<ItemStack> merged = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        if (oldEntry != null && System.currentTimeMillis() - oldEntry.time <= HARD_EXPIRE_TIME_MS) {
            for (ItemStack stack : oldEntry.drops) {
                addIfUnique(merged, seen, stack);

                if (merged.size() >= MAX_CACHED_DROPS) {
                    break;
                }
            }
        }

        if (drops != null) {
            for (ItemStack stack : drops) {
                addIfUnique(merged, seen, stack);

                if (merged.size() >= MAX_CACHED_DROPS) {
                    break;
                }
            }
        }

        if (merged.isEmpty()) {
            return;
        }

        CACHE.put(entityId, new Entry(merged));
    }

    public static void clear() {
        CACHE.clear();
        REQUESTS.clear();
    }

    private static void addIfUnique(List<ItemStack> list, Set<String> seen, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        String key = keyOf(stack);

        if (!seen.add(key)) {
            return;
        }

        list.add(stack.copy());
    }

    private static String keyOf(ItemStack stack) {
        ResourceLocation registryName = stack.getItem().getRegistryName();
        NBTTagCompound tag = stack.getTagCompound();

        return (registryName == null ? "" : registryName.toString())
                + ":"
                + stack.getMetadata()
                + ":"
                + (tag == null ? "" : tag.toString());
    }

    private static final class Entry {
        private final long time;
        private final List<ItemStack> drops;

        private Entry(List<ItemStack> drops) {
            this.time = System.currentTimeMillis();
            this.drops = new ArrayList<>();

            if (drops != null) {
                for (ItemStack stack : drops) {
                    if (stack != null && !stack.isEmpty()) {
                        this.drops.add(stack.copy());
                    }
                }
            }
        }
    }
}