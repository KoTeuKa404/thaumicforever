package com.koteuka404.thaumicforever.world.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import com.koteuka404.thaumicforever.ThaumicForever;

public class PlayerStructureData extends WorldSavedData {
    private static final String SAVE_DATA_NAME = "ThaumicForever_Structures";
    private final Map<UUID, BlockPos> playerStructures = new HashMap<>();
    private final Map<UUID, TraderState> traderStates = new HashMap<>();

    public PlayerStructureData() {
        super(SAVE_DATA_NAME);
    }

    // MapStorage creates WorldSavedData through a String-argument constructor.
    public PlayerStructureData(String name) {
        super(name);
    }

    public static PlayerStructureData get(World world) {
        World overworld = world.getMinecraftServer().getWorld(0); // id 0 — Overworld
        MapStorage storage = overworld.getMapStorage();
        PlayerStructureData data = (PlayerStructureData) storage.getOrLoadData(PlayerStructureData.class, SAVE_DATA_NAME);
        if (data == null) {
            data = new PlayerStructureData();
            storage.setData(SAVE_DATA_NAME, data);
        }
        return data;
    }

    public void setPlayerStructure(UUID playerID, BlockPos pos) {
        playerStructures.put(playerID, pos);
        markDirty();
    }

    public boolean hasPlayerStructure(UUID playerID) {
        boolean result = playerStructures.containsKey(playerID);
        return result;
    }

    public BlockPos getPlayerStructure(UUID playerID) {
        BlockPos pos = playerStructures.get(playerID);
        return pos;
    }

    public Map<UUID, BlockPos> getPlayerStructures() {
        return new HashMap<>(playerStructures);
    }

    public TraderState getTraderState(UUID playerID) {
        TraderState state = traderStates.get(playerID);
        if (state == null) {
            state = new TraderState();
            traderStates.put(playerID, state);
            markDirty();
        }
        return state;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        playerStructures.clear();
        traderStates.clear();
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
            if (entry.hasKey("trader", 10)) {
                traderStates.put(id, TraderState.read(entry.getCompoundTag("trader")));
            }
        }
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
            TraderState state = traderStates.get(e.getKey());
            if (state != null) {
                tag.setTag("trader", state.write());
            }
            list.appendTag(tag);
        }
        compound.setTag("player_structures", list);
        return compound;
    }

    public static final class TraderState {
        public long nextSpawnTime = -1L;
        public long despawnTime = -1L;
        public boolean active;
        public BlockPos areaStart;
        public int areaX;
        public int areaY;
        public int areaZ;

        public NBTTagCompound write() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setLong("nextSpawn", nextSpawnTime);
            tag.setLong("despawn", despawnTime);
            tag.setBoolean("active", active);
            if (areaStart != null) {
                tag.setInteger("x", areaStart.getX());
                tag.setInteger("y", areaStart.getY());
                tag.setInteger("z", areaStart.getZ());
                tag.setInteger("sizeX", areaX);
                tag.setInteger("sizeY", areaY);
                tag.setInteger("sizeZ", areaZ);
            }
            return tag;
        }

        private static TraderState read(NBTTagCompound tag) {
            TraderState state = new TraderState();
            state.nextSpawnTime = tag.getLong("nextSpawn");
            state.despawnTime = tag.getLong("despawn");
            state.active = tag.getBoolean("active");
            if (tag.hasKey("x") && tag.hasKey("sizeX")) {
                state.areaStart = new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
                state.areaX = tag.getInteger("sizeX");
                state.areaY = tag.getInteger("sizeY");
                state.areaZ = tag.getInteger("sizeZ");
            }
            return state;
        }
    }
}
