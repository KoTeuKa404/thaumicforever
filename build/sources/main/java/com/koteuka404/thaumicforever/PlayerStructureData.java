package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class PlayerStructureData extends WorldSavedData {
    private static final String SAVE_DATA_NAME = "ThaumicForever_Structures";
    private final Map<UUID, BlockPos> playerStructures = new HashMap<>();

    public PlayerStructureData() {
        super(SAVE_DATA_NAME);
    }

    public static PlayerStructureData get(World world) {
        World overworld = world.getMinecraftServer().getWorld(0); // id 0 — Overworld
        MapStorage storage = overworld.getMapStorage();
        PlayerStructureData data = (PlayerStructureData) storage.getOrLoadData(PlayerStructureData.class, SAVE_DATA_NAME);
        if (data == null) {
            data = new PlayerStructureData();
            storage.setData(SAVE_DATA_NAME, data);
            System.out.println("ThaumicForever DEBUG: Створюємо новий файл WorldSavedData");
        } else {
            System.out.println("ThaumicForever DEBUG: Завантажуємо існуючий WorldSavedData");
        }
        return data;
    }

    public void setPlayerStructure(UUID playerID, BlockPos pos) {
        playerStructures.put(playerID, pos);
        System.out.println("ThaumicForever DEBUG: Додаємо/оновлюємо структуру для " + playerID + " (" + pos + ")");
        markDirty();
    }

    public boolean hasPlayerStructure(UUID playerID) {
        boolean result = playerStructures.containsKey(playerID);
        System.out.println("ThaumicForever DEBUG: hasPlayerStructure(" + playerID + ") = " + result);
        return result;
    }

    public BlockPos getPlayerStructure(UUID playerID) {
        BlockPos pos = playerStructures.get(playerID);
        System.out.println("ThaumicForever DEBUG: getPlayerStructure(" + playerID + ") = " + pos);
        return pos;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        playerStructures.clear();
        NBTTagList list = nbt.getTagList("player_structures", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            UUID id = UUID.fromString(entry.getString("playerID"));
            BlockPos p = new BlockPos(
                entry.getInteger("x"),
                entry.getInteger("y"),
                entry.getInteger("z")
            );
            playerStructures.put(id, p);
            System.out.println("ThaumicForever DEBUG: readFromNBT: " + id + " -> " + p);
        }
        System.out.println("ThaumicForever DEBUG: readFromNBT COMPLETE, entries: " + playerStructures.size());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<UUID, BlockPos> e : playerStructures.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("playerID", e.getKey().toString());
            tag.setInteger("x", e.getValue().getX());
            tag.setInteger("y", e.getValue().getY());
            tag.setInteger("z", e.getValue().getZ());
            list.appendTag(tag);
            System.out.println("ThaumicForever DEBUG: writeToNBT: " + e.getKey() + " -> " + e.getValue());
        }
        compound.setTag("player_structures", list);
        return compound;
    }
}
