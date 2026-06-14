package com.koteuka404.thaumicforever.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.koteuka404.thaumicforever.ThaumicForever;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class VoidChestWorldData extends WorldSavedData {
    private static final String DATA_NAME = ThaumicForever.MODID + "_void_chests";
    private static final int INVENTORY_SIZE = 72;

    private final Map<UUID, NonNullList<ItemStack>> inventories = new HashMap<>();

    public VoidChestWorldData() {
        super(DATA_NAME);
    }

    public VoidChestWorldData(String name) {
        super(name);
    }

    public static VoidChestWorldData get(World world) {
        MapStorage storage = getGlobalStorage(world);
        if (storage == null) {
            return new VoidChestWorldData(DATA_NAME);
        }
        VoidChestWorldData data = (VoidChestWorldData) storage.getOrLoadData(VoidChestWorldData.class, DATA_NAME);
        if (data == null) {
            data = new VoidChestWorldData(DATA_NAME);
            storage.setData(DATA_NAME, data);
        }
        return data;
    }

    private static MapStorage getGlobalStorage(World world) {
        if (!world.isRemote && world.getMinecraftServer() != null && world.getMinecraftServer().getWorld(0) != null) {
            return world.getMinecraftServer().getWorld(0).getMapStorage();
        }
        return world.getMapStorage();
    }

    public NonNullList<ItemStack> getInventory(UUID networkId) {
        NonNullList<ItemStack> stacks = this.inventories.computeIfAbsent(networkId, id -> NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY));
        if (stacks.size() == INVENTORY_SIZE) {
            return stacks;
        }

        NonNullList<ItemStack> resized = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        for (int i = 0; i < stacks.size() && i < resized.size(); i++) {
            resized.set(i, stacks.get(i));
        }
        this.inventories.put(networkId, resized);
        this.markDirty();
        return resized;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.inventories.clear();
        NBTTagList list = nbt.getTagList("Inventories", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound inventoryTag = list.getCompoundTagAt(i);
            try {
                UUID id = UUID.fromString(inventoryTag.getString("Id"));
                NonNullList<ItemStack> stacks = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
                NBTTagList items = inventoryTag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
                for (int j = 0; j < items.tagCount(); j++) {
                    NBTTagCompound itemTag = items.getCompoundTagAt(j);
                    int slot = itemTag.getByte("Slot") & 255;
                    if (slot >= 0 && slot < stacks.size()) {
                        stacks.set(slot, new ItemStack(itemTag));
                    }
                }
                this.inventories.put(id, stacks);
            } catch (IllegalArgumentException ignored) {
                // Skip malformed old data instead of breaking the whole save.
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList inventoriesTag = new NBTTagList();
        for (Map.Entry<UUID, NonNullList<ItemStack>> entry : this.inventories.entrySet()) {
            NBTTagCompound inventoryTag = new NBTTagCompound();
            inventoryTag.setString("Id", entry.getKey().toString());

            NBTTagList items = new NBTTagList();
            NonNullList<ItemStack> stacks = entry.getValue();
            for (int i = 0; i < stacks.size(); i++) {
                ItemStack stack = stacks.get(i);
                if (!stack.isEmpty()) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    itemTag.setByte("Slot", (byte) i);
                    stack.writeToNBT(itemTag);
                    items.appendTag(itemTag);
                }
            }
            inventoryTag.setTag("Items", items);
            inventoriesTag.appendTag(inventoryTag);
        }
        compound.setTag("Inventories", inventoriesTag);
        return compound;
    }
}
