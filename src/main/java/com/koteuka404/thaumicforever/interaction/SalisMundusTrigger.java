package com.koteuka404.thaumicforever.interaction;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.items.ItemsTC;
import com.koteuka404.thaumicforever.block.GreatResearchTableBlock;
import com.koteuka404.thaumicforever.block.GreatResearchTableSideBlock;
import com.koteuka404.thaumicforever.block.InvisiblePartBlock;

public class SalisMundusTrigger implements IDustTrigger {

    @Override
    public Placement getValidFace(World world, EntityPlayer player, BlockPos pos, EnumFacing face) {
        IBlockState state = world.getBlockState(pos);
        
        if (findGreatResearchPair(world, pos) != null && hasGreatResearchTableResearch(player)) {
            return new Placement(0, 0, 0, face);
        }

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

        BlockPos[] pair = findGreatResearchPair(world, pos);
        if (pair != null) {
            if (!hasGreatResearchTableResearch(player)) {
                return;
            }
            BlockPos mainPos = pair[0];
            BlockPos sidePos = pair[1];
            EnumFacing sideDir = EnumFacing.getFacingFromVector(
                sidePos.getX() - mainPos.getX(),
                sidePos.getY() - mainPos.getY(),
                sidePos.getZ() - mainPos.getZ()
            );
            if (sideDir == null || sideDir.getAxis().isVertical()) {
                sideDir = EnumFacing.EAST;
            }
            EnumFacing facing = sideDir.rotateYCCW();

            world.setBlockState(
                mainPos,
                ModBlocks.GREAT_RESEARCH_TABLE.getDefaultState().withProperty(GreatResearchTableBlock.FACING, facing),
                3
            );
            world.setBlockState(
                sidePos,
                ModBlocks.GREAT_RESEARCH_TABLE_SIDE.getDefaultState().withProperty(GreatResearchTableSideBlock.FACING, facing),
                3
            );
            consumeSalisMundus(player);
            return;
        }
        
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

                    consumeSalisMundus(player);
                    break;
                }
            }
        }
    }

    private BlockPos[] findGreatResearchPair(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == com.wonginnovations.oldresearch.common.blocks.ModBlocks.RESEARCHTABLE) {
            BlockPos greatwood = findNeighbor(world, pos, ModBlocks.GREATWOOD_TABLE);
            return greatwood != null ? new BlockPos[] { pos, greatwood } : null;
        }
        if (state.getBlock() == ModBlocks.GREATWOOD_TABLE) {
            BlockPos research = findNeighbor(world, pos, com.wonginnovations.oldresearch.common.blocks.ModBlocks.RESEARCHTABLE);
            return research != null ? new BlockPos[] { research, pos } : null;
        }
        return null;
    }

    private BlockPos findNeighbor(World world, BlockPos pos, net.minecraft.block.Block block) {
        BlockPos[] neighbors = {
            pos.north(), pos.south(), pos.east(), pos.west()
        };
        for (BlockPos neighbor : neighbors) {
            if (world.getBlockState(neighbor).getBlock() == block) {
                return neighbor;
            }
        }
        return null;
    }

    private void consumeSalisMundus(EntityPlayer player) {
        if (player.isCreative()) return;
        ItemStack heldItem = player.getHeldItemMainhand();
        if (!heldItem.isEmpty() && heldItem.getItem() == ItemsTC.salisMundus) {
            heldItem.shrink(1);
        }
    }

    private boolean hasGreatResearchTableResearch(EntityPlayer player) {
        if (player == null) return false;
        if (player.capabilities.isCreativeMode) return true;
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        return knowledge != null && knowledge.isResearchComplete("GREATRESEARCHTABLE");
    }

    @Override
    public List<BlockPos> sparkle(World world, EntityPlayer player, BlockPos pos, Placement placement) {
        return Arrays.asList(new BlockPos[]{pos});
    }
}
