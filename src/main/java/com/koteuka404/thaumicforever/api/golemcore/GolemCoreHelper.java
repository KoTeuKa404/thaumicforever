package com.koteuka404.thaumicforever.api.golemcore;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.IGolemAPI;

public final class GolemCoreHelper {
    public static final String NBT_ROOT = "ThaumicForeverGolemCore";
    public static final String NBT_CORE_ID = "CoreId";

    private GolemCoreHelper() {
    }

    public static boolean isGolem(Entity entity) {
        return entity instanceof IGolemAPI;
    }

    public static ResourceLocation getActiveCoreId(IGolemAPI golem) {
        Entity entity = golem == null ? null : golem.getGolemEntity();
        if (entity == null) return null;

        NBTTagCompound root = entity.getEntityData().getCompoundTag(NBT_ROOT);
        if (!root.hasKey(NBT_CORE_ID, 8)) return null;
        return new ResourceLocation(root.getString(NBT_CORE_ID));
    }

    public static IGolemCore getActiveCore(IGolemAPI golem) {
        return GolemCoreRegistry.get(getActiveCoreId(golem));
    }

    public static boolean hasActiveCore(IGolemAPI golem) {
        return getActiveCoreId(golem) != null;
    }

    public static boolean setActiveCore(IGolemAPI golem, ResourceLocation coreId) {
        if (golem == null || golem.getGolemEntity() == null || !GolemCoreRegistry.contains(coreId)) {
            return false;
        }

        IGolemCore oldCore = getActiveCore(golem);
        if (oldCore != null) {
            oldCore.onRemoved(golem);
        }

        Entity entity = golem.getGolemEntity();
        NBTTagCompound root = entity.getEntityData().getCompoundTag(NBT_ROOT);
        root.setString(NBT_CORE_ID, coreId.toString());
        entity.getEntityData().setTag(NBT_ROOT, root);

        IGolemCore newCore = GolemCoreRegistry.get(coreId);
        if (newCore != null) {
            newCore.onInstalled(golem);
        }
        return true;
    }

    public static boolean clearActiveCore(IGolemAPI golem) {
        if (golem == null || golem.getGolemEntity() == null) return false;

        IGolemCore oldCore = getActiveCore(golem);
        if (oldCore != null) {
            oldCore.onRemoved(golem);
        }

        Entity entity = golem.getGolemEntity();
        NBTTagCompound root = entity.getEntityData().getCompoundTag(NBT_ROOT);
        if (!root.hasKey(NBT_CORE_ID, 8)) return false;
        root.removeTag(NBT_CORE_ID);
        entity.getEntityData().setTag(NBT_ROOT, root);
        return true;
    }
}
