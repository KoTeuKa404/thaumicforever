package com.koteuka404.thaumicforever;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemWand extends Item {

    private BlockPos linkSource = null;

    public ItemWand() {
        setUnlocalizedName("tf_wand");
        setRegistryName("tf_wand");
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos,EnumHand hand, EnumFacing face,float hitX, float hitY, float hitZ) {
        if (world.isRemote) return EnumActionResult.SUCCESS;

        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof PortBlock)) {
            player.sendStatusMessage(new TextComponentString("This is not a Port!"), true);
            return EnumActionResult.FAIL;
        }

        if (player.isSneaking()) {
            linkSource = pos;
            player.sendStatusMessage(new TextComponentString("Source port saved."), true);
            return EnumActionResult.SUCCESS;
        }

        if (linkSource == null) {
            player.sendStatusMessage(new TextComponentString("No source port selected."), true);
            return EnumActionResult.FAIL;
        }

        double distSq = linkSource.distanceSq(pos);
        if (distSq > 10 * 10) {
            player.sendStatusMessage(new TextComponentString("Ports too far apart."), true);
            linkSource = null;
            return EnumActionResult.FAIL;
        }

        TileEntity teSrc = world.getTileEntity(linkSource);
        TileEntity teTgt = world.getTileEntity(pos);
        if (teSrc instanceof TilePort && teTgt instanceof TilePort) {
            TilePort src = (TilePort) teSrc;
            TilePort tgt = (TilePort) teTgt;

            src.setTargetPort(pos);
            tgt.setSourcePort(linkSource);

            player.sendStatusMessage(new TextComponentString("Ports linked!"), true);
        } else {
            player.sendStatusMessage(new TextComponentString("Invalid port tile!"), true);
        }

        linkSource = null;
        return EnumActionResult.SUCCESS;
    }
}
