package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.block.BlockSapling;
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
import thaumcraft.common.world.objects.WorldGenBigMagicTree;

public class ItemBigOakGrower extends Item {
    private final WorldGenBigMagicTree generator = new WorldGenBigMagicTree(false);

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
                return EnumActionResult.PASS;
            }
        } catch (Exception e) {
        }

        world.setBlockToAir(pos);

        generator.generate(world, new Random(), pos);

        ItemStack stack = player.getHeldItem(hand);
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }

        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f + world.rand.nextFloat() * 0.2f);

        return EnumActionResult.SUCCESS;
    }
}
