package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class WatcherWorldGen {
    private static final Random random = new Random();
    private static boolean spawned = false;

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new WatcherWorldGen());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        World world = player.world;

        if (!world.isRemote && world.provider.getDimension() == 0) { 
            BlockPos pos = player.getPosition();
            // System.out.println("[DEBUG] Player is at: " + pos);

            if (isPlayerInOceanMonument(world, pos)) {
                // System.out.println("[DEBUG] Player is inside an Ocean Monument!");
                
                if (!spawned) {
                    spawnWatcher(world, pos);
                    spawned = true;
                }
            } else {
                // System.out.println("[DEBUG] Player is NOT inside an Ocean Monument.");
            }
        }
    }

    private boolean isPlayerInOceanMonument(World world, BlockPos pos) {
        MapStorage storage = world.getPerWorldStorage();
        MapGenStructureData data = (MapGenStructureData) storage.getOrLoadData(MapGenStructureData.class, "Monument");

        if (data != null) {
            // System.out.println("[DEBUG] Found structure data.");
            NBTTagCompound structures = data.getTagCompound();

            for (String key : structures.getKeySet()) {
                // System.out.println("[DEBUG] Found structure: " + key);
                NBTTagCompound structureData = structures.getCompoundTag(key);

                if (isPositionInsideStructure(structureData, pos)) {
                    // System.out.println("[DEBUG] Player is inside: " + key);
                    return true;
                }
            }
        } else {
            // System.out.println("[DEBUG] No structure data found.");
        }
        return false;
    }

    private boolean isPositionInsideStructure(NBTTagCompound structureData, BlockPos pos) {
        if (!structureData.hasKey("BB")) {
            // System.out.println("[DEBUG] Structure has no bounding box.");
            return false;
        }

        int[] boundingBox = structureData.getIntArray("BB");

        if (boundingBox.length == 6) {
            // System.out.println("[DEBUG] Checking bounding box: " +
            //     "Min(" + boundingBox[0] + "," + boundingBox[1] + "," + boundingBox[2] + ") " +
            //     "Max(" + boundingBox[3] + "," + boundingBox[4] + "," + boundingBox[5] + ") " +
            //     "Player: " + pos);

            boolean inside = pos.getX() >= boundingBox[0] && pos.getX() <= boundingBox[3] &&
                            pos.getY() >= boundingBox[1] && pos.getY() <= boundingBox[4] &&
                            pos.getZ() >= boundingBox[2] && pos.getZ() <= boundingBox[5];

            // System.out.println("[DEBUG] Player inside: " + inside);
            return inside;
        }
        return false;
    }

    private void spawnWatcher(World world, BlockPos pos) {
        // System.out.println("[DEBUG] Attempting to spawn Watcher...");
        
        WatcherEntity watcher = new WatcherEntity(world);
        int spawnX = pos.getX() + random.nextInt(10) - 5;
        int spawnY = pos.getY() + 3;
        int spawnZ = pos.getZ() + random.nextInt(10) - 5;

        watcher.setPosition(spawnX, spawnY, spawnZ);
        
        if (!world.isRemote) {
            boolean success = world.spawnEntity(watcher);
            // System.out.println("[DEBUG] Watcher spawn success: " + success);
        }
    }
}
