package com.koteuka404.thaumicforever.wand.block;

import com.koteuka404.thaumicforever.registry.ModGuiHandler;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.item.ItemWand;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockArcaneWorkbenchWandCharger extends Block {

    public BlockArcaneWorkbenchWandCharger() {
        super(Material.IRON);
        setRegistryName(ThaumicForever.MODID, "arcane_workbench_wand_charger");
        setUnlocalizedName(ThaumicForever.MODID + ".arcane_workbench_wand_charger");
        setHardness(2.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos.down()).getBlock() == ModBlocks.WAND_WORKBENCH;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (worldIn.getBlockState(pos.down()).getBlock() != ModBlocks.WAND_WORKBENCH) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        BlockPos benchPos = pos.down();
        if (worldIn.getBlockState(benchPos).getBlock() != ModBlocks.WAND_WORKBENCH) return false;

        ItemStack held = playerIn.getHeldItem(hand);
        if (!held.isEmpty() && held.getItem() instanceof ItemWand) {
            if (worldIn.isRemote) return true;
            EnumActionResult result = ((ItemWand) held.getItem()).handleBlockIntercept(playerIn, worldIn, pos, hand);
            return result != EnumActionResult.PASS;
        }

        if (worldIn.isRemote) return true;
        playerIn.openGui(ThaumicForever.instance, ModGuiHandler.GUI_WAND_WORKBENCH, worldIn,
                benchPos.getX(), benchPos.getY(), benchPos.getZ());
        return true;
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
}
