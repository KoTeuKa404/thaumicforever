package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.IChunkGenerator;

public class VoidWorldProvider extends WorldProvider {

    @Override
    public void init() {
        this.biomeProvider = new net.minecraft.world.biome.BiomeProviderSingle(net.minecraft.init.Biomes.VOID);
    }
    public void onEntityUpdate() {
        for (EntityPlayer player : world.playerEntities) {
            if (player.posY < 0) { 
                if (player instanceof EntityPlayerMP) {
                    System.out.println("Гравець впав у безодню, повертаємо в Overworld...");
                    EntityPlayerMP playerMP = (EntityPlayerMP) player;

                    
                    WorldServer overworld = playerMP.getServer().getWorld(0);
                    if (overworld != null) {
                        BlockPos overworldSpawn = overworld.getSpawnPoint();
                        playerMP.changeDimension(0, new CustomTeleporter(overworldSpawn));
                    }
                }
            }
        }
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.VOID_DIMENSION;
    }

    @Override
    public boolean canRespawnHere() {
        return true;
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public int getAverageGroundLevel() {
        return 64;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new VoidChunkGenerator(this.world);
    }

    @Override
    public BlockPos getSpawnPoint() {
        return new BlockPos(0, 64, 0);
    }
}
