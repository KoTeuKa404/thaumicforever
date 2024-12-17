package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InvisiblePartBlock extends Block {

    public InvisiblePartBlock() {
        super(Material.WOOD);
        setUnlocalizedName("invisible_part");
        setRegistryName("invisible_part");
        setHardness(2.5F);
        setResistance(12.5F);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE; // Робить блок невидимим
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return false; // Забороняє ставити блок вручну
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // Перенаправляємо взаємодію на основний блок
        for (EnumFacing direction : EnumFacing.HORIZONTALS) {
            BlockPos possibleMainBlockPos = pos.offset(direction);
            IBlockState possibleMainBlockState = worldIn.getBlockState(possibleMainBlockPos);

            if (possibleMainBlockState.getBlock() instanceof DoubleTableBlock) {
                return possibleMainBlockState.getBlock().onBlockActivated(worldIn, possibleMainBlockPos, possibleMainBlockState, playerIn, hand, facing, hitX, hitY, hitZ);
            }
        }
        return false; // Якщо основний блок не знайдено
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, java.util.Random rand) {
        // Перевіряємо чи існує основний блок
        boolean mainBlockExists = false;

        for (EnumFacing direction : EnumFacing.HORIZONTALS) {
            BlockPos possibleMainBlockPos = pos.offset(direction);
            IBlockState possibleMainBlockState = worldIn.getBlockState(possibleMainBlockPos);

            if (possibleMainBlockState.getBlock() instanceof DoubleTableBlock) {
                mainBlockExists = true;
                break;
            }
        }

        // Якщо основний блок відсутній, видаляємо себе
        if (!mainBlockExists) {
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        // Викликаємо перевірку на основний блок при зміні сусіднього блоку
        worldIn.scheduleUpdate(pos, this, 1);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        for (EnumFacing direction : EnumFacing.HORIZONTALS) {
            BlockPos possibleMainBlockPos = pos.offset(direction);
            IBlockState possibleMainBlockState = worldIn.getBlockState(possibleMainBlockPos);
    
            if (possibleMainBlockState.getBlock() instanceof DoubleTableBlock) {
                // Ламаємо основний блок
                worldIn.setBlockToAir(possibleMainBlockPos);
    
                // Випадіння стола
                Block.spawnAsEntity(worldIn, possibleMainBlockPos, new ItemStack(ModBlocks.DOUBLE_TABLE));
                break;
            }
        }
    
        super.breakBlock(worldIn, pos, state);
    }
    

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        // Забороняємо дроп при ламанні цього блоку
        world.setBlockToAir(pos);
        return false;
    }
     @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new DoubleTableTileEntity();
    }

    // @Override
    // public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    //     if (!world.isRemote) {
    //         TileEntity tileEntity = world.getTileEntity(pos);
    //         if (tileEntity instanceof DoubleTableTileEntity) {
    //             player.openGui(ThaumicForever.instance, ModGuiHandler.DOUBLE_TABLE_GUI, world, pos.getX(), pos.getY(), pos.getZ());
    //         }
    //     }
    //     return true;
    // }
}
