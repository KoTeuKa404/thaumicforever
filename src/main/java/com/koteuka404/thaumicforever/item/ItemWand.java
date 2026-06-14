package com.koteuka404.thaumicforever.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import com.koteuka404.thaumicforever.block.PortBlock;
import com.koteuka404.thaumicforever.registry.ModBlocks;
import com.koteuka404.thaumicforever.tile.TilePort;

public class ItemWand extends Item {

    private static final String TAG_LINK_X = "tf_link_x";
    private static final String TAG_LINK_Y = "tf_link_y";
    private static final String TAG_LINK_Z = "tf_link_z";
    private static final String TAG_LINK_DIM = "tf_link_dim";

    public ItemWand() {
        setUnlocalizedName("tf_wand");
        setRegistryName("tf_wand");
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing face, float hitX, float hitY, float hitZ, EnumHand hand) {
        return handleUse(player, world, pos, hand, true);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos,EnumHand hand, EnumFacing face,float hitX, float hitY, float hitZ) {
        return handleUse(player, world, pos, hand, false);
    }

    private EnumActionResult handleUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, boolean beforeBlockActivation) {
        IBlockState state = world.getBlockState(pos);
        boolean isPort = state.getBlock() instanceof PortBlock;
        boolean isCharger = state.getBlock() == ModBlocks.ARCANE_WORKBENCH_WAND_CHARGER;

        if (!isPort && !isCharger) {
            if (beforeBlockActivation) return EnumActionResult.PASS;
            player.sendStatusMessage(new TextComponentString("This is not a Port or Wand Charger!"), true);
            return EnumActionResult.FAIL;
        }

        if (world.isRemote) return EnumActionResult.SUCCESS;

        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            if (!isPort) {
                player.sendStatusMessage(new TextComponentString("Source must be a Port."), true);
                return EnumActionResult.FAIL;
            }
            setLinkSource(stack, world, pos);
            player.sendStatusMessage(new TextComponentString("Source port saved."), true);
            return EnumActionResult.SUCCESS;
        }

        BlockPos linkSource = getLinkSource(stack, world);
        if (linkSource == null) {
            player.sendStatusMessage(new TextComponentString("No source port selected."), true);
            return EnumActionResult.FAIL;
        }

        double distSq = linkSource.distanceSq(pos);
        int maxDistance = isCharger ? 32 : 10;
        if (distSq > maxDistance * maxDistance) {
            player.sendStatusMessage(new TextComponentString("Target too far away."), true);
            clearLinkSource(stack);
            return EnumActionResult.FAIL;
        }

        TileEntity teSrc = world.getTileEntity(linkSource);
        TileEntity teTgt = world.getTileEntity(pos);
        if (teSrc instanceof TilePort && teTgt instanceof TilePort) {
            TilePort src = (TilePort) teSrc;
            TilePort tgt = (TilePort) teTgt;

            src.setTargetPort(pos);
            src.clearTargetCharger();
            tgt.setSourcePort(linkSource);

            player.sendStatusMessage(new TextComponentString("Ports linked!"), true);
        } else if (teSrc instanceof TilePort && isCharger) {
            TilePort src = (TilePort) teSrc;
            src.setTargetCharger(pos);
            player.sendStatusMessage(new TextComponentString("Port linked to Wand Charger!"), true);
        } else {
            player.sendStatusMessage(new TextComponentString("Invalid link target!"), true);
        }

        clearLinkSource(stack);
        return EnumActionResult.SUCCESS;
    }

    public EnumActionResult handleBlockIntercept(EntityPlayer player, World world, BlockPos pos, EnumHand hand) {
        return handleUse(player, world, pos, hand, false);
    }

    private void setLinkSource(ItemStack stack, World world, BlockPos pos) {
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setInteger(TAG_LINK_X, pos.getX());
        tag.setInteger(TAG_LINK_Y, pos.getY());
        tag.setInteger(TAG_LINK_Z, pos.getZ());
        tag.setInteger(TAG_LINK_DIM, world.provider.getDimension());
        stack.setTagCompound(tag);
    }

    private BlockPos getLinkSource(ItemStack stack, World world) {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) return null;
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey(TAG_LINK_X) || !tag.hasKey(TAG_LINK_Y) || !tag.hasKey(TAG_LINK_Z)) return null;
        if (tag.getInteger(TAG_LINK_DIM) != world.provider.getDimension()) return null;
        return new BlockPos(tag.getInteger(TAG_LINK_X), tag.getInteger(TAG_LINK_Y), tag.getInteger(TAG_LINK_Z));
    }

    private void clearLinkSource(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) return;
        NBTTagCompound tag = stack.getTagCompound();
        tag.removeTag(TAG_LINK_X);
        tag.removeTag(TAG_LINK_Y);
        tag.removeTag(TAG_LINK_Z);
        tag.removeTag(TAG_LINK_DIM);
    }
}
