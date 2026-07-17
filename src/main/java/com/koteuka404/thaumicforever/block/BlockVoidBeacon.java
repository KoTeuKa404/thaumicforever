package com.koteuka404.thaumicforever.block;

import com.koteuka404.thaumicforever.tile.TileVoidBeacon;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVoidBeacon extends Block {
    public BlockVoidBeacon() {
        super(Material.GLASS);
        setRegistryName("void_beacon");
        setUnlocalizedName("void_beacon");
        setHardness(3.0F);
        setResistance(15.0F);
        setHarvestLevel("pickaxe", 2);
    }

    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World world, IBlockState state) { return new TileVoidBeacon(); }
    @Override public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
            EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking() || !player.getHeldItem(hand).isEmpty()) return false;
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote && tile instanceof TileVoidBeacon) {
            ((TileVoidBeacon) tile).clearEssentia();
        }
        return true;
    }
}
