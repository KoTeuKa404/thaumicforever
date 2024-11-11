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

public class SalisMundusDoubleTableTrigger implements IDustTrigger {

    // Перевірка для valid face
    @Override
    public Placement getValidFace(World world, EntityPlayer player, BlockPos pos, EnumFacing face) {
        IBlockState state = world.getBlockState(pos);
        
        // Перевіряємо, чи це блок GREATWOOD_TABLE
        if (state.getBlock() == ModBlocks.GREATWOOD_TABLE) {
            // Перевіряємо сусідні блоки, щоб знайти інший GREATWOOD_TABLE
            BlockPos[] neighbors = {
                pos.north(), pos.south(), pos.east(), pos.west()
            };
            
            for (BlockPos neighborPos : neighbors) {
                IBlockState neighborState = world.getBlockState(neighborPos);
                if (neighborState.getBlock() == ModBlocks.GREATWOOD_TABLE) {
                    return new Placement(0, 0, 0, face); // Якщо є два сусідні блоки, повертаємо валідний face
                }
            }
        }
        return null; // Якщо не знайдено пари блоків, повертаємо null
    }

    // Реалізація основної дії
    @Override
    public void execute(World world, EntityPlayer player, BlockPos pos, Placement placement, EnumFacing side) {
        IBlockState state = world.getBlockState(pos);
        
        if (state.getBlock() == ModBlocks.GREATWOOD_TABLE) {
            // Перевіряємо сусідні блоки, щоб знайти інший GREATWOOD_TABLE
            BlockPos[] neighbors = {
                pos.north(), pos.south(), pos.east(), pos.west()
            };
            
            for (BlockPos neighborPos : neighbors) {
                IBlockState neighborState = world.getBlockState(neighborPos);
                if (neighborState.getBlock() == ModBlocks.GREATWOOD_TABLE) {
                    // Заміна блоків на DOUBLE_TABLE
                    world.setBlockState(pos, ModBlocks.DOUBLE_TABLE.getDefaultState().withProperty(BlockHorizontal.FACING, state.getValue(BlockHorizontal.FACING)));
                    world.setBlockToAir(neighborPos); // Видаляємо другий блок

                    // Якщо не в Creative режимі, зменшуємо кількість Salis Mundus
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
        // Повертаємо список позицій, де будуть ефекти
        return Arrays.asList(new BlockPos[]{pos});
    }
}