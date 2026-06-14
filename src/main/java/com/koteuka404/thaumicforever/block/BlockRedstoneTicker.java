package com.koteuka404.thaumicforever.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRedstoneTicker extends Block {
    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);

    private static final int MIN_DELAY_TICKS = 2;
    private static final int MAX_EXTRA_DELAY_TICKS = 28;

    public BlockRedstoneTicker() {
        super(Material.ROCK);
        setRegistryName("thaumicforever", "redstone_ticker");
        setUnlocalizedName("redstone_ticker");
        setSoundType(SoundType.STONE);
        setHardness(3.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(this.blockState.getBaseState().withProperty(POWER, 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWER);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(POWER, meta & 15);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWER) & 15;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (!world.isRemote) {
            scheduleNextTick(world, pos);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote && state.getValue(POWER) > 0) {
            notifyRedstoneNeighbors(world, pos);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) {
            return;
        }

        int nextPower = rollPower(rand);
        if (nextPower != state.getValue(POWER)) {
            world.setBlockState(pos, state.withProperty(POWER, nextPower), 3);
            notifyRedstoneNeighbors(world, pos);
        }

        scheduleNextTick(world, pos);
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, EnumFacing side) {
        return state.getValue(POWER);
    }

    @Override
    public int getStrongPower(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, EnumFacing side) {
        return state.getValue(POWER);
    }

    private static int rollPower(Random rand) {
        if (rand.nextInt(4) == 0) {
            return 0;
        }
        return 1 + rand.nextInt(15);
    }

    private static void scheduleNextTick(World world, BlockPos pos) {
        world.scheduleUpdate(pos, world.getBlockState(pos).getBlock(), MIN_DELAY_TICKS + world.rand.nextInt(MAX_EXTRA_DELAY_TICKS + 1));
    }

    private static void notifyRedstoneNeighbors(World world, BlockPos pos) {
        world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), false);
        for (EnumFacing facing : EnumFacing.values()) {
            world.notifyNeighborsOfStateChange(pos.offset(facing), world.getBlockState(pos).getBlock(), false);
        }
    }
}
