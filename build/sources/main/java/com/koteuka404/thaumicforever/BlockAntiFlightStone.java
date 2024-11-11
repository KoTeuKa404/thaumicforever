package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockAntiFlightStone extends Block implements ITileEntityProvider {

    public BlockAntiFlightStone() {
        super(Material.ROCK);
        setUnlocalizedName("anti_flight_stone");
        setRegistryName("anti_flight_stone");
        setHardness(3.0F);
        setCreativeTab(ThaumicForever.CREATIVE_TAB);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityAntiFlightStone();
    }
}
