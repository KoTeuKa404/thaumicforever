package com.koteuka404.thaumicforever;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.items.ItemsTC;

public class SalisMundusTrigger implements IDustTrigger {

    @Override
    public Placement getValidFace(World world, EntityPlayer player, BlockPos pos, EnumFacing face) {
        IBlockState state = world.getBlockState(pos);
        
        if (state.getBlock() == ModBlocks.GREATWOOD_TABLE) {
            BlockPos[] neighbors = {
                pos.north(), pos.south(), pos.east(), pos.west()
            };
            
            for (BlockPos neighborPos : neighbors) {
                IBlockState neighborState = world.getBlockState(neighborPos);
                if (neighborState.getBlock() == ModBlocks.GREATWOOD_TABLE) {
                    return new Placement(0, 0, 0, face); 
                }
            }
        }
        return null; 
    }

    @Override
    public void execute(World world, EntityPlayer player, BlockPos pos, Placement placement, EnumFacing side) {
        IBlockState state = world.getBlockState(pos);
        
        if (state.getBlock() == ModBlocks.GREATWOOD_TABLE) {
            BlockPos[] neighbors = {
                pos.north(), pos.south(), pos.east(), pos.west()
            };
            
            for (BlockPos neighborPos : neighbors) {
                IBlockState neighborState = world.getBlockState(neighborPos);
                if (neighborState.getBlock() == ModBlocks.GREATWOOD_TABLE) {
                    EnumFacing neighborDir = EnumFacing.getFacingFromVector(
                        neighborPos.getX() - pos.getX(),
                        neighborPos.getY() - pos.getY(),
                        neighborPos.getZ() - pos.getZ()
                    );
                    if (neighborDir == null || neighborDir.getAxis().isVertical()) {
                        neighborDir = state.getValue(BlockHorizontal.FACING).rotateY();
                    }

                    EnumFacing facing = neighborDir.rotateYCCW();

                    world.setBlockState(
                        pos,
                        ModBlocks.DOUBLE_TABLE.getDefaultState().withProperty(BlockHorizontal.FACING, facing),
                        3
                    );
                    world.setBlockState(
                        neighborPos,
                        ModBlocks.INVISIBLE_PART.getDefaultState().withProperty(InvisiblePartBlock.FACING, facing),
                        3
                    );

                    ItemStack heldItem = player.getHeldItemMainhand();
                    if (!player.isCreative() && !heldItem.isEmpty() && heldItem.getItem() == ItemsTC.salisMundus) {
                        heldItem.shrink(1);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public List<BlockPos> sparkle(World world, EntityPlayer player, BlockPos pos, Placement placement) {
        return Arrays.asList(new BlockPos[]{pos});
    }
}
