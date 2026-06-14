package com.koteuka404.thaumicforever.storage;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.network.PacketBiomeUpdate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class BiomeSpreadWorldData extends WorldSavedData {
    private static final String DATA_NAME = ThaumicForever.MODID + "_spread_biomes";
    private static Class<?> jeidChunkClass;
    private static Method jeidGetIntBiomeArray;
    private static boolean jeidLookupDone;
    private static boolean jeidWarned;

    private final Map<Long, Map<Long, String>> columnsByChunk = new HashMap<>();

    public BiomeSpreadWorldData() {
        super(DATA_NAME);
    }

    public BiomeSpreadWorldData(String name) {
        super(name);
    }

    public static BiomeSpreadWorldData get(World world) {
        MapStorage storage = world.getPerWorldStorage();
        BiomeSpreadWorldData data = (BiomeSpreadWorldData) storage.getOrLoadData(BiomeSpreadWorldData.class, DATA_NAME);
        if (data == null) {
            data = new BiomeSpreadWorldData(DATA_NAME);
            storage.setData(DATA_NAME, data);
        }
        return data;
    }

    public static void setBiomeAt(World world, BlockPos pos, Biome biome) {
        if (world == null || biome == null) {
            return;
        }

        applyBiomeAt(world, pos, biome);

        if (!world.isRemote) {
            BiomeSpreadWorldData data = get(world);
            data.remember(pos, biome);
        }
    }

    public void applyToChunk(World world, Chunk chunk) {
        if (world == null || chunk == null || world.isRemote) {
            return;
        }

        Map<Long, String> columns = columnsByChunk.get(chunkKey(chunk.x, chunk.z));
        if (columns == null || columns.isEmpty()) {
            return;
        }

        for (Map.Entry<Long, String> entry : columns.entrySet()) {
            Biome biome = Biome.REGISTRY.getObject(new ResourceLocation(entry.getValue()));
            if (biome == null) {
                continue;
            }

            int x = unpackX(entry.getKey());
            int z = unpackZ(entry.getKey());
            BlockPos pos = new BlockPos(x, 0, z);
            applyBiomeAt(world, pos, biome);
        }
    }

    private static void applyBiomeAt(World world, BlockPos pos, Biome biome) {
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        if (chunk == null) {
            return;
        }

        if (!trySetJeidBiome(chunk, pos, biome)) {
            setVanillaBiome(chunk, pos, biome);
        }

        chunk.markDirty();
        if (world.isRemote) {
            world.markBlockRangeForRenderUpdate(pos.getX(), 0, pos.getZ(), pos.getX(), 255, pos.getZ());
        } else {
            world.markAndNotifyBlock(pos, chunk, world.getBlockState(pos), world.getBlockState(pos), 3);
            sendBiomeUpdate(world, pos, biome);
        }
    }

    private static void sendBiomeUpdate(World world, BlockPos pos, Biome biome) {
        if (ThaumicForever.network == null) {
            return;
        }

        ThaumicForever.network.sendToAllAround(
            new PacketBiomeUpdate(pos.getX(), pos.getZ(), Biome.getIdForBiome(biome)),
            new NetworkRegistry.TargetPoint(
                world.provider.getDimension(),
                pos.getX(),
                world.getHeight(pos).getY(),
                pos.getZ(),
                64.0D
            )
        );
    }

    private static void setVanillaBiome(Chunk chunk, BlockPos pos, Biome biome) {
        byte[] biomeArray = chunk.getBiomeArray();
        int index = ((pos.getZ() & 15) << 4) | (pos.getX() & 15);
        biomeArray[index] = (byte) (Biome.getIdForBiome(biome) & 255);
        chunk.setBiomeArray(biomeArray);
    }

    private static boolean trySetJeidBiome(Chunk chunk, BlockPos pos, Biome biome) {
        Method method = getJeidGetIntBiomeArray();
        if (method == null || jeidChunkClass == null || !jeidChunkClass.isInstance(chunk)) {
            return false;
        }

        try {
            int[] biomeArray = (int[]) method.invoke(chunk);
            if (biomeArray == null || biomeArray.length < 256) {
                return false;
            }

            int index = ((pos.getZ() & 15) << 4) | (pos.getX() & 15);
            biomeArray[index] = Biome.getIdForBiome(biome);
            return true;
        } catch (Throwable t) {
            if (!jeidWarned) {
                jeidWarned = true;
                ThaumicForever.LOGGER.warn("Failed to write JEID biome array; falling back to vanilla biome array.", t);
            }
            return false;
        }
    }

    private static Method getJeidGetIntBiomeArray() {
        if (jeidLookupDone) {
            return jeidGetIntBiomeArray;
        }

        jeidLookupDone = true;
        try {
            jeidChunkClass = Class.forName("org.dimdev.jeid.INewChunk");
            jeidGetIntBiomeArray = jeidChunkClass.getMethod("getIntBiomeArray");
        } catch (Throwable ignored) {
            jeidChunkClass = null;
            jeidGetIntBiomeArray = null;
        }
        return jeidGetIntBiomeArray;
    }

    private void remember(BlockPos pos, Biome biome) {
        ResourceLocation key = biome.getRegistryName();
        if (key == null) {
            return;
        }

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        long chunkKey = chunkKey(chunkX, chunkZ);
        Map<Long, String> columns = columnsByChunk.get(chunkKey);
        if (columns == null) {
            columns = new HashMap<>();
            columnsByChunk.put(chunkKey, columns);
        }

        columns.put(columnKey(pos.getX(), pos.getZ()), key.toString());
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        columnsByChunk.clear();
        NBTTagList list = nbt.getTagList("Columns", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int x = tag.getInteger("x");
            int z = tag.getInteger("z");
            String biome = tag.getString("biome");
            if (biome.isEmpty()) {
                continue;
            }

            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            long chunkKey = chunkKey(chunkX, chunkZ);
            Map<Long, String> columns = columnsByChunk.get(chunkKey);
            if (columns == null) {
                columns = new HashMap<>();
                columnsByChunk.put(chunkKey, columns);
            }
            columns.put(columnKey(x, z), biome);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map<Long, String> columns : columnsByChunk.values()) {
            for (Map.Entry<Long, String> entry : columns.entrySet()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("x", unpackX(entry.getKey()));
                tag.setInteger("z", unpackZ(entry.getKey()));
                tag.setString("biome", entry.getValue());
                list.appendTag(tag);
            }
        }
        compound.setTag("Columns", list);
        return compound;
    }

    private static long chunkKey(int chunkX, int chunkZ) {
        return (((long) chunkX) << 32) ^ (chunkZ & 0xffffffffL);
    }

    private static long columnKey(int x, int z) {
        return (((long) x) << 32) ^ (z & 0xffffffffL);
    }

    private static int unpackX(long key) {
        return (int) (key >> 32);
    }

    private static int unpackZ(long key) {
        return (int) key;
    }
}
