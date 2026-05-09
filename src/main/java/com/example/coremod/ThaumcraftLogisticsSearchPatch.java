package com.example.coremod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.seals.SealProvide;

public final class ThaumcraftLogisticsSearchPatch {

    private ThaumcraftLogisticsSearchPatch() {}

    @SuppressWarnings("unchecked")
    public static void refreshItemList(Object containerObj, boolean forceRefresh) {
        if (containerObj == null) {
            return;
        }

        int total = getIntField(containerObj, "lastTotal", 0);
        TreeMap<String, ItemStack> freshItems = new TreeMap<>();

        if (forceRefresh) {
            total = 0;

            World world = (World) getField(containerObj, "worldObj");
            EntityPlayer player = (EntityPlayer) getField(containerObj, "player");
            Object searchObj = getField(containerObj, "searchText");
            String searchText = searchObj == null ? "" : String.valueOf(searchObj);
            SearchQuery query = SearchQuery.parse(searchText);

            if (world != null && player != null) {
                for (SealEntity sealEntity : SealHandler.getSealsInRange(world, player.getPosition(), 32)) {
                    if (!(sealEntity.getSeal() instanceof SealProvide)) {
                        continue;
                    }
                    if (!sealEntity.getOwner().equals(player.getUniqueID().toString())) {
                        continue;
                    }

                    IItemHandler handler = ThaumcraftInvHelper.getItemHandlerAt(
                        world,
                        sealEntity.getSealPos().pos,
                        sealEntity.getSealPos().face
                    );
                    if (handler == null) {
                        continue;
                    }

                    SealProvide provider = (SealProvide) sealEntity.getSeal();
                    for (int slot = 0; slot < handler.getSlots(); slot++) {
                        ItemStack stack = handler.getStackInSlot(slot).copy();
                        if (stack.isEmpty()) {
                            continue;
                        }
                        if (!provider.matchesFilters(stack)) {
                            continue;
                        }
                        if (!query.matches(stack)) {
                            continue;
                        }

                        String key = stack.getDisplayName() + stack.getItemDamage() + stack.getTagCompound();
                        if (freshItems.containsKey(key)) {
                            stack.grow(freshItems.get(key).getCount());
                        }
                        freshItems.put(key, stack);
                        total += stack.getCount();
                    }
                }
            }
        }

        int lastTotal = getIntField(containerObj, "lastTotal", 0);
        int start = getIntField(containerObj, "start", 0);
        int lastStart = getIntField(containerObj, "lastStart", 0);

        if (lastTotal != total || start != lastStart) {
            setField(containerObj, "lastTotal", total);
            if (forceRefresh) {
                setField(containerObj, "items", freshItems);
            }

            IInventory input = (IInventory) getField(containerObj, "input");
            if (input == null) {
                return;
            }

            input.clear();
            int i = 0;
            int slot = 0;
            TreeMap<String, ItemStack> items = (TreeMap<String, ItemStack>) getField(containerObj, "items");
            if (items == null) {
                items = new TreeMap<>();
            }
            for (String key : items.keySet()) {
                i++;
                if (i <= start * 9) {
                    continue;
                }
                input.setInventorySlotContents(slot, items.get(key));
                slot++;
                if (slot >= input.getSizeInventory()) {
                    break;
                }
            }
            setField(containerObj, "end", items.size() / 9 - 8);
        }
    }

    private static Object getField(Object obj, String name) {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f.get(obj);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            } catch (Throwable t) {
                return null;
            }
        }
        return null;
    }

    private static void setField(Object obj, String name, Object value) {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                f.set(obj, value);
                return;
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            } catch (Throwable ignored) {
                return;
            }
        }
    }

    private static int getIntField(Object obj, String name, int def) {
        Object value = getField(obj, name);
        return value instanceof Integer ? (Integer) value : def;
    }

    private static final class SearchQuery {
        private final List<String> textTokens = new ArrayList<>();
        private final List<String> modTokens = new ArrayList<>();
        private final List<String> aspectTokens = new ArrayList<>();

        static SearchQuery parse(String raw) {
            SearchQuery q = new SearchQuery();
            if (raw == null) {
                return q;
            }

            String search = raw.trim().toLowerCase(Locale.ROOT);
            if (search.isEmpty()) {
                return q;
            }

            for (String token : search.split("\\s+")) {
                if (token.isEmpty()) {
                    continue;
                }
                if (token.startsWith("@")) {
                    String mod = token.substring(1).trim();
                    if (!mod.isEmpty()) {
                        q.modTokens.add(mod);
                    }
                    continue;
                }
                if (isAspectToken(token)) {
                    q.aspectTokens.add(token);
                } else {
                    q.textTokens.add(token);
                }
            }
            return q;
        }

        boolean matches(ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }

            String display = safeLower(stack.getDisplayName());
            ResourceLocation key = stack.getItem().getRegistryName();
            String reg = key == null ? "" : safeLower(key.toString());
            String namespace = key == null ? "" : safeLower(key.getResourceDomain());
            String path = key == null ? "" : safeLower(key.getResourcePath());

            for (String token : textTokens) {
                boolean ok = display.contains(token) || reg.contains(token) || path.contains(token);
                if (!ok) {
                    return false;
                }
            }

            for (String token : modTokens) {
                boolean ok = namespace.contains(token) || reg.contains(token);
                if (!ok) {
                    return false;
                }
            }

            if (!aspectTokens.isEmpty()) {
                AspectList al = AspectHelper.getObjectAspects(stack.copy());
                if (al == null || al.size() == 0) {
                    return false;
                }
                Aspect[] aspects = al.getAspects();
                if (aspects == null || aspects.length == 0) {
                    return false;
                }

                for (String token : aspectTokens) {
                    boolean found = false;
                    for (Aspect aspect : aspects) {
                        if (aspect == null) {
                            continue;
                        }
                        if (matchesAspectToken(aspect, token)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        return false;
                    }
                }
            }

            return true;
        }

        private static boolean isAspectToken(String token) {
            if (token == null || token.isEmpty()) {
                return false;
            }
            if (Aspect.getAspect(token) != null) {
                return true;
            }
            for (Aspect aspect : Aspect.aspects.values()) {
                if (aspect != null && matchesAspectToken(aspect, token)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean matchesAspectToken(Aspect aspect, String token) {
            String t = safeLower(token);
            String tag = safeLower(aspect.getTag());
            String name = safeLower(aspect.getName());
            String local = safeLower(aspect.getLocalizedDescription());
            return tag.equals(t) || name.equals(t) || local.equals(t);
        }

        private static String safeLower(String s) {
            return s == null ? "" : s.toLowerCase(Locale.ROOT);
        }
    }
}
