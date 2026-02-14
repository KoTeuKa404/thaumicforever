package com.koteuka404.thaumicforever;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientPrimalAuraCache {

    private static final Map<Long, float[]> CACHE = new ConcurrentHashMap<>();

    private ClientPrimalAuraCache() {}

    private static long chunkKey(int cx, int cz) {
        // exact 32-bit packing, no loss for negatives
        return ((((long) cx) & 0xffffffffL) << 32) | (((long) cz) & 0xffffffffL);
    }

    private static long key(int dim, int cx, int cz) {
        long d = (((long) dim) & 0xffffffffL) << 32;
        return d ^ chunkKey(cx, cz);
    }

    public static void put(int dim, int cx, int cz, float[] vis6) {
        if (vis6 == null || vis6.length != Primal.COUNT) return;
        float[] copy = new float[Primal.COUNT];
        System.arraycopy(vis6, 0, copy, 0, Primal.COUNT);
        CACHE.put(key(dim, cx, cz), copy);
    }

    public static float[] get(int dim, int cx, int cz) {
        return CACHE.get(key(dim, cx, cz));
    }
}
