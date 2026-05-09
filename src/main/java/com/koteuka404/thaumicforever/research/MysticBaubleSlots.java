package com.koteuka404.thaumicforever.research;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import baubles.api.BaubleType;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import com.koteuka404.thaumicforever.compat.ResearchBaubleMapping;

public final class MysticBaubleSlots {
    public static final int DEFAULT_BASE_BAUBLE_SLOTS = 7;
    public static final int ORIGINAL_BAUBLE_SLOTS = 7;

    private MysticBaubleSlots() {}

    public static class SlotInfo {
        public final BaubleType type;
        public final String    categoryKey;  
        public SlotInfo(BaubleType t, String key) {
            this.type       = t;
            this.categoryKey = key;
        }
    }

    public static class BoundSlot {
        public final int physicalIndex;
        public final SlotInfo slotInfo;

        public BoundSlot(int physicalIndex, SlotInfo slotInfo) {
            this.physicalIndex = physicalIndex;
            this.slotInfo = slotInfo;
        }
    }

    public static List<SlotInfo> getAllSlots(EntityPlayer player) {
        List<SlotInfo> out = new ArrayList<>();
        for (BaubleType t : ResearchBaubleMapping.IMMEDIATE) {
            out.add(new SlotInfo(t, null));
        }
        for (Map.Entry<String, BaubleType> e : ResearchBaubleMapping.BY_CATEGORY.entrySet()) {
            out.add(new SlotInfo(e.getValue(), e.getKey()));
        }
        return out;
    }

    public static int getBaseSlotOffset(int handlerSlots) {
        if (handlerSlots <= 0) return 0;
        return Math.min(DEFAULT_BASE_BAUBLE_SLOTS, handlerSlots);
    }

    public static int getAvailableMysticSlotCount(EntityPlayer player, int handlerSlots) {
        int base = getBaseSlotOffset(handlerSlots);
        int capacity = Math.max(0, handlerSlots - base);
        int requested = getAllSlots(player).size();
        return Math.min(requested, capacity);
    }

    public static int toPhysicalSlotIndex(int handlerSlots, int mysticIndex) {
        return getBaseSlotOffset(handlerSlots) + mysticIndex;
    }

    public static List<BoundSlot> getBoundMysticSlots(EntityPlayer player, IBaublesItemHandler handler) {
        List<BoundSlot> out = new ArrayList<>();
        if (handler == null) return out;
        if (isMysticMechanicDisabled()) return out;

        // Classic Baubles storage: mystic slots are placed after the 7 base slots.
        int handlerSlots = handler.getSlots();
        int base = ORIGINAL_BAUBLE_SLOTS;
        int capacity = Math.max(0, handlerSlots - base);
        if (capacity <= 0) return out;

        List<SlotInfo> requested = new ArrayList<>(getAllSlots(player));
        if (requested.isEmpty()) return out;

        int count = Math.min(requested.size(), capacity);
        for (int i = 0; i < count; i++) {
            int phys = base + i;
            SlotInfo req = requested.get(i);
            out.add(new BoundSlot(phys, new SlotInfo(req.type, req.categoryKey)));
        }
        return out;
    }

    public static List<BoundSlot> getGuiBoundSlots(EntityPlayer player, IBaublesItemHandler handler) {
        if (handler == null) return new ArrayList<>();
        if (isMysticMechanicDisabled()) return new ArrayList<>();

        return getBoundMysticSlots(player, handler);
    }

    public static boolean isMysticMechanicDisabled() {
        return getMysticDisabledByModName() != null;
    }

    public static String getMysticDisabledByModName() {
        // Mod id is "baubles" for both forks, so detect by API classes.
        if (hasClassResource("baubles/api/BaubleTypeEx.class")) {
            return "BaublesEX";
        }
        // Strict Bubbles marker to avoid matching classic Baubles.
        if (hasClassResource("baubles/api/BaubleTypeImpl.class")) {
            return "Bubbles";
        }
        return null;
    }

    private static boolean hasClassResource(String classResourcePath) {
        ClassLoader cl = MysticBaubleSlots.class.getClassLoader();
        return cl != null && cl.getResource(classResourcePath) != null;
    }

    private static List<SlotInfo> reorderRequestedForGroupedLayout(List<SlotInfo> in) {
        Map<BaubleType, List<SlotInfo>> byType = new EnumMap<>(BaubleType.class);
        for (SlotInfo s : in) {
            byType.computeIfAbsent(s.type, k -> new ArrayList<>()).add(s);
        }

        List<SlotInfo> out = new ArrayList<>(in.size());
        BaubleType[] order = {
            BaubleType.AMULET,
            BaubleType.RING,
            BaubleType.BELT,
            BaubleType.HEAD,
            BaubleType.BODY,
            BaubleType.CHARM,
            BaubleType.TRINKET
        };
        for (BaubleType t : order) {
            List<SlotInfo> lst = byType.get(t);
            if (lst != null) out.addAll(lst);
        }
        return out;
    }

    private static List<BoundSlot> getClassicBaseGuiSlots(IBaublesItemHandler handler) {
        int total = Math.max(0, handler.getSlots());
        if (total <= 0) {
            return new ArrayList<>();
        }

        int count = Math.min(total, DEFAULT_BASE_BAUBLE_SLOTS);
        List<BoundSlot> out = new ArrayList<>(count);
        for (int phys = 0; phys < count; phys++) {
            BaubleType type = resolvePhysicalSlotType(handler, phys, inferClassicBaseType(phys));
            out.add(new BoundSlot(phys, new SlotInfo(type, null)));
        }
        return out;
    }

    private static BaubleType inferClassicBaseType(int physIndex) {
        switch (physIndex) {
            case 0:
                return BaubleType.AMULET;
            case 1:
            case 2:
                return BaubleType.RING;
            case 3:
                return BaubleType.BELT;
            case 4:
                return BaubleType.HEAD;
            case 5:
                return BaubleType.BODY;
            case 6:
                return BaubleType.CHARM;
            default:
                return BaubleType.TRINKET;
        }
    }

    private static boolean hasGetTypeInSlot(IBaublesItemHandler handler) {
        try {
            handler.getClass().getMethod("getTypeInSlot", int.class);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean hasTypeDataRegistryApi() {
        try {
            Class.forName("baubles.api.registries.TypeData", false, MysticBaubleSlots.class.getClassLoader());
            Class.forName("baubles.api.BaubleTypeEx", false, MysticBaubleSlots.class.getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static BaubleType inferTypeBySlotIndex(int physIndex, BaubleType fallback) {
        // Prefer non-global/specific types first.
        BaubleType[] preferred = {
            BaubleType.AMULET, BaubleType.RING, BaubleType.BELT,
            BaubleType.HEAD, BaubleType.BODY, BaubleType.CHARM,
            BaubleType.TRINKET
        };
        for (BaubleType t : preferred) {
            try {
                if (t.hasSlot(physIndex)) return t;
            } catch (Throwable ignored) {
            }
        }
        return fallback == null ? BaubleType.TRINKET : fallback;
    }

    private static BaubleType getPhysicalSlotType(IBaublesItemHandler handler, int physIndex) {
        BaubleType byRegistry = resolveTypeByTypeDataRegistry(physIndex);
        if (byRegistry != null) {
            return byRegistry;
        }

        if (!hasGetTypeInSlot(handler)) {
            return inferTypeBySlotIndex(physIndex, BaubleType.TRINKET);
        }

        try {
            Object ex = handler.getClass().getMethod("getTypeInSlot", int.class).invoke(handler, physIndex);
            if (ex != null) {
                try {
                    java.lang.reflect.Method m = ex.getClass().getMethod("getOldType");
                    Object old = m.invoke(ex);
                    if (old instanceof BaubleType) {
                        return (BaubleType) old;
                    }
                } catch (Throwable ignored) {
                }

                String name = ex.toString();
                if (name != null) {
                    try {
                        return BaubleType.valueOf(name.trim().toUpperCase());
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return inferTypeBySlotIndex(physIndex, BaubleType.TRINKET);
    }

    private static BaubleType resolveTypeByTypeDataRegistry(int physIndex) {
        if (!hasTypeDataRegistryApi()) {
            return null;
        }
        try {
            Class<?> typeDataCls = Class.forName("baubles.api.registries.TypeData");
            Object listObj = typeDataCls.getMethod("sortedList").invoke(null);
            if (!(listObj instanceof List)) return null;

            @SuppressWarnings("rawtypes")
            List list = (List) listObj;
            int cursor = 0;
            for (Object ex : list) {
                if (ex == null) continue;
                int amount = 0;
                try {
                    Object amt = ex.getClass().getMethod("getAmount").invoke(ex);
                    if (amt instanceof Integer) amount = ((Integer) amt).intValue();
                } catch (Throwable ignored) {
                }
                if (amount <= 0) continue;

                if (physIndex >= cursor && physIndex < cursor + amount) {
                    try {
                        Object old = ex.getClass().getMethod("getOldType").invoke(ex);
                        if (old instanceof BaubleType) return (BaubleType) old;
                    } catch (Throwable ignored) {
                    }
                    String name = null;
                    try {
                        Object nm = ex.getClass().getMethod("getName").invoke(ex);
                        if (nm != null) name = nm.toString();
                    } catch (Throwable ignored) {
                    }
                    if (name != null) {
                        try {
                            return BaubleType.valueOf(name.trim().toUpperCase());
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    return null;
                }
                cursor += amount;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static BaubleType resolvePhysicalSlotType(Object handler, int physIndex, BaubleType fallback) {
        if (!(handler instanceof IBaublesItemHandler)) {
            return inferTypeBySlotIndex(physIndex, fallback);
        }
        IBaublesItemHandler h = (IBaublesItemHandler) handler;
        BaubleType t = getPhysicalSlotType(h, physIndex);
        return t == null ? inferTypeBySlotIndex(physIndex, fallback) : t;
    }

    public static boolean shouldUseLegacyStrictValidation(Object handler) {
        if (!(handler instanceof IBaublesItemHandler)) {
            return true;
        }
        IBaublesItemHandler h = (IBaublesItemHandler) handler;
        return !hasGetTypeInSlot(h) && !hasTypeDataRegistryApi();
    }

    public static boolean isUnlocked(EntityPlayer player, String categoryKey) {
        // IMMEDIATE
        if (categoryKey == null) return true;
        IPlayerKnowledge know = ThaumcraftCapabilities.getKnowledge(player);
        ResearchCategory rc = ResearchCategories.getResearchCategory(categoryKey);
        if (rc == null) return false;
        if (rc.research.isEmpty()) return false;
        for (String key : rc.research.keySet()) {
            if (!know.isResearchComplete(key)) {
                return false;
            }
        }
        return true;
    }
}
