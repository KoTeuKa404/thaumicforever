package com.koteuka404.thaumicforever.golemcore;

import com.koteuka404.thaumicforever.api.golemcore.GolemCoreRegistry;

public final class GolemCores {
    private static boolean registered;

    private GolemCores() {
    }

    public static void registerDefaults() {
        if (registered) return;
        GolemCoreRegistry.register(new GoliathGolemCore());
        GolemCoreRegistry.register(new IronSkinGolemCore());
        GolemCoreRegistry.register(new ArcaneGolemCore());
        GolemCoreRegistry.register(new SwiftGolemCore());
        registered = true;
    }
}
