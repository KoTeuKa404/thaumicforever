package com.koteuka404.thaumicforever.block;

import com.koteuka404.thaumicforever.tile.TilePrimalAuraConverter;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPrimalAuraConverter extends Block {
    private static final AxisAlignedBB CONVERTER_AABB = new AxisAlignedBB(
            0.0D, 0.0D, 0.1D,
            1.0D, 0.75D, 0.875D);

    public BlockPrimalAuraConverter() {
        super(Material.ROCK);
        setRegistryName("primal_aura_converter");
        setUnlocalizedName("primal_aura_converter");
        setHardness(3.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TilePrimalAuraConverter();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CONVERTER_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
    public net.minecraft.util.BlockRenderLayer getRenderLayer() {
        return net.minecraft.util.BlockRenderLayer.CUTOUT;
    }
}
