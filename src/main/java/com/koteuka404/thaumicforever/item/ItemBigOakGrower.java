package com.koteuka404.thaumicforever.item;

import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import thaumcraft.common.world.objects.WorldGenBigMagicTree;

public class ItemBigOakGrower extends Item {
    public ItemBigOakGrower() {
        super();
        setMaxStackSize(16);
        setUnlocalizedName("core_nature");
        setRegistryName("core_nature");
        setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos,EnumHand hand, EnumFacing facing,float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }

        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockSapling)) {
            return EnumActionResult.PASS;
        }

        PropertyEnum<?> prop = BlockSapling.TYPE;
        try {
            Object saplingType = state.getValue(prop);
            if (!saplingType.toString().equalsIgnoreCase("oak")) {
                return forceSaplingGrow(player, world, pos, state, hand);
            }
        } catch (Exception e) {
        }

        world.setBlockToAir(pos);

        WorldGenAbstractTree generator = pickGenerator(world.rand);
        boolean generated = generator.generate(world, world.rand, pos);
        if (!generated) {
            // Retry once with another random scale if generation failed.
            pickGenerator(world.rand).generate(world, world.rand, pos);
        }

        ItemStack stack = player.getHeldItem(hand);
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }

        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f + world.rand.nextFloat() * 0.2f);

        return EnumActionResult.SUCCESS;
    }

    private EnumActionResult forceSaplingGrow(EntityPlayer player, World world, BlockPos pos, IBlockState state, EnumHand hand) {
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        BlockSapling sapling = (BlockSapling) state.getBlock();
        boolean grew = false;
        // Force to stage 1 then generate tree in the same use.
        sapling.grow(world, world.rand, pos, state);
        IBlockState after = world.getBlockState(pos);
        if (after.getBlock() instanceof BlockSapling) {
            sapling.grow(world, world.rand, pos, after);
        }
        if (!(world.getBlockState(pos).getBlock() instanceof BlockSapling)) {
            grew = true;
        }

        if (grew) {
            ItemStack stack = player.getHeldItem(hand);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            world.playEvent(2005, pos, 0);
            world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f + world.rand.nextFloat() * 0.2f);
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    private static WorldGenAbstractTree pickGenerator(Random rand) {
        // Magic Forest "oak" uses Thaumcraft big magic tree with internal variance.
        return new WorldGenBigMagicTree(false);
    }
}
