package com.koteuka404.thaumicforever.block;

import javax.annotation.Nullable;

import com.koteuka404.thaumicforever.tile.TileCvDefender;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCvDefender extends BlockContainer {
    /**
     * Matches the visible model:
     * X/Z: 3..13 model pixels
     * Y:   0..11.5 model pixels
     *
     * This replaces the default full-block outline/collision box.
     */
    private static final AxisAlignedBB DEFENDER_AABB =
            new AxisAlignedBB(
                    3.0D / 16.0D,
                    0.0D,
                    3.0D / 16.0D,
                    13.0D / 16.0D,
                    11.5D / 16.0D,
                    13.0D / 16.0D
            );

    public BlockCvDefender() {
        super(Material.ROCK);
        setRegistryName("cv_defender");
        setUnlocalizedName("cv_defender");
        setHardness(4.0F);
        setResistance(30.0F);
        setHarvestLevel("pickaxe", 2);
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

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state,
                                        IBlockAccess world,
                                        BlockPos pos) {
        return DEFENDER_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state,
                                                 IBlockAccess world,
                                                 BlockPos pos) {
        return DEFENDER_AABB;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCvDefender();
    }
}
