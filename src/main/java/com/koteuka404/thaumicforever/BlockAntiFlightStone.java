package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAntiFlightStone extends Block implements ITileEntityProvider {

    public BlockAntiFlightStone() {
        super(Material.ROCK);
        setUnlocalizedName("anti_flight_stone");
        setRegistryName("anti_flight_stone");
        setHardness(3.0F);
        setCreativeTab(ThaumicForever.CREATIVE_TAB);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityAntiFlightStone();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityAntiFlightStone) {
            ((TileEntityAntiFlightStone) te).update();
        }
        super.breakBlock(world, pos, state);
    }
}
