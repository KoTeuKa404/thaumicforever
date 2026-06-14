package com.koteuka404.thaumicforever.api.golemcore;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

public final class GolemCoreRegistry {
    private static final Map<ResourceLocation, IGolemCore> CORES = new LinkedHashMap<>();

    private GolemCoreRegistry() {
    }

    public static void register(IGolemCore core) {
        if (core == null || core.getId() == null) {
            throw new IllegalArgumentException("Golem core and core id must not be null");
        }
        ResourceLocation id = core.getId();
        if (CORES.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate golem core id: " + id);
        }
        CORES.put(id, core);
    }

    public static IGolemCore get(ResourceLocation id) {
        return id == null ? null : CORES.get(id);
    }

    public static boolean contains(ResourceLocation id) {
        return get(id) != null;
    }

    public static Collection<IGolemCore> getValues() {
        return Collections.unmodifiableCollection(CORES.values());
    }
}
