package com.koteuka404.thaumicforever.item;

import com.koteuka404.thaumicforever.entity.EntityVoidSingularityMinecart;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemVoidSingularityMinecart extends Item {

    public ItemVoidSingularityMinecart() {
        setUnlocalizedName("void_singularity_minecart");
        setRegistryName("void_singularity_minecart");
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos,
            EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        if (!BlockRailBase.isRailBlock(state)) {
            return EnumActionResult.FAIL;
        }

        if (!world.isRemote) {
            EntityVoidSingularityMinecart cart = new EntityVoidSingularityMinecart(
                    world, pos.getX() + 0.5D, pos.getY() + 0.0625D, pos.getZ() + 0.5D);
            world.spawnEntity(cart);
            if (!player.capabilities.isCreativeMode) {
                player.getHeldItem(hand).shrink(1);
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
