package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.WorldSavedData;

public class PlayerStructureManager {
    private static final String STRUCTURE_NAME = "thaumicforever:void";
    private static final String SAVE_DATA_NAME = "ThaumicForever_Structures";

    private static final Map<UUID, BlockPos> structureCache = new HashMap<>();

    public static BlockPos getOrCreateStructureForPlayer(UUID playerID, World world) {
        WorldSavedStructureData savedData = WorldSavedStructureData.get(world);

        if (structureCache.containsKey(playerID)) {
            return structureCache.get(playerID);
        }

        if (savedData.hasStructure(playerID)) {
            BlockPos centerPos = savedData.getStructurePos(playerID);

            if (!world.isAirBlock(centerPos)) {
                structureCache.put(playerID, centerPos); 
                return centerPos;
            }
        }

        BlockPos structurePos = generateStructurePosition(playerID);
        BlockPos centerPos = placeStructure(world, structurePos);

        savedData.addStructure(playerID, centerPos);
        savedData.markDirty();
        structureCache.put(playerID, centerPos);

        return centerPos;
    }

    private static BlockPos generateStructurePosition(UUID playerID) {
        return new BlockPos(0, 64, 0).add(playerID.hashCode() % 1000, 0, playerID.hashCode() / 1000);
    }

    private static BlockPos placeStructure(World world, BlockPos pos) {
        TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
        Template template = templateManager.getTemplate(world.getMinecraftServer(), new ResourceLocation(STRUCTURE_NAME));

        if (template != null) {
            int centerX = template.getSize().getX() / 2;
            int centerZ = template.getSize().getZ() / 2;
            BlockPos structureCenter = pos.add(centerX, 1, centerZ);

            System.out.println("Generating structure at: " + pos);

            if (!world.isBlockLoaded(pos)) {
                world.getChunkProvider().provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
            }

            template.addBlocksToWorld(world, pos, new PlacementSettings());

            System.out.println("Structure generated successfully at: " + structureCenter);
            return structureCenter;
        }

        System.out.println("Failed to load structure template!");
        return pos;
    }

    public static class WorldSavedStructureData extends WorldSavedData {
        private final Map<UUID, BlockPos> structures = new HashMap<>();

        public WorldSavedStructureData() {
            super(SAVE_DATA_NAME);
        }

        public static WorldSavedStructureData get(World world) {
            WorldSavedData data = world.getPerWorldStorage().getOrLoadData(WorldSavedStructureData.class, SAVE_DATA_NAME);
            if (data == null) {
                data = new WorldSavedStructureData();
                world.getPerWorldStorage().setData(SAVE_DATA_NAME, data);
            }
            return (WorldSavedStructureData) data;
        }

        public boolean hasStructure(UUID playerID) {
            return structures.containsKey(playerID);
        }

        public BlockPos getStructurePos(UUID playerID) {
            return structures.get(playerID);
        }

        public void addStructure(UUID playerID, BlockPos pos) {
            structures.put(playerID, pos);
            markDirty();
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            structures.clear();
            NBTTagList list = nbt.getTagList("structures", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound entry = list.getCompoundTagAt(i);
                UUID playerID = UUID.fromString(entry.getString("playerID"));
                BlockPos pos = new BlockPos(entry.getInteger("x"), entry.getInteger("y"), entry.getInteger("z"));
                structures.put(playerID, pos);
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            NBTTagList list = new NBTTagList();
            for (Map.Entry<UUID, BlockPos> entry : structures.entrySet()) {
                NBTTagCompound entryTag = new NBTTagCompound();
                entryTag.setString("playerID", entry.getKey().toString());
                entryTag.setInteger("x", entry.getValue().getX());
                entryTag.setInteger("y", entry.getValue().getY());
                entryTag.setInteger("z", entry.getValue().getZ());
                list.appendTag(entryTag);
            }
            compound.setTag("structures", list);
            return compound;
        }
    }
}
