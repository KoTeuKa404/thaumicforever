package com.koteuka404.thaumicforever.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.koteuka404.thaumicforever.tile.TileEntityFlightStone;
import com.koteuka404.thaumicforever.ThaumicForever;

public class BlockFlightStone extends Block implements ITileEntityProvider {

    public BlockFlightStone() {
        super(Material.ROCK);
        setUnlocalizedName("flight_stone");
        setRegistryName("flight_stone");
        setHardness(3.0F);
        setCreativeTab(ThaumicForever.CREATIVE_TAB);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityFlightStone();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityFlightStone.cleanupAfterBreak(world, pos);
        super.breakBlock(world, pos, state);
    }
}
