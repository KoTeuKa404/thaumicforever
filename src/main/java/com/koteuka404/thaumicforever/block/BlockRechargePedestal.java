package com.koteuka404.thaumicforever.block;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.items.IRechargable;
import com.koteuka404.thaumicforever.tile.TileRechargePedestal;
import com.koteuka404.thaumicforever.ThaumicForever;

public class BlockRechargePedestal extends Block implements ITileEntityProvider {

    public BlockRechargePedestal() {
        super(Material.ROCK);
        setRegistryName(ThaumicForever.MODID, "recharge_pedestal");
        setUnlocalizedName(ThaumicForever.MODID + ".recharge_pedestal");
        setHardness(2.5F);
        setResistance(12.0F);
        setSoundType(SoundType.STONE);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileRechargePedestal();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileRechargePedestal();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TileRechargePedestal)) return false;
        TileRechargePedestal pedestal = (TileRechargePedestal) te;

        if (worldIn.isRemote) {
            return true;
        }

        ItemStack held = playerIn.getHeldItem(hand);
        ItemStack stored = pedestal.getWand();

        if (stored.isEmpty() && !held.isEmpty() && canAcceptRechargeItem(held)) {
            ItemStack one = held.copy();
            one.setCount(1);
            pedestal.setWand(one);
            held.shrink(1);
            if (held.getCount() <= 0) {
                playerIn.setHeldItem(hand, ItemStack.EMPTY);
            }
            worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2f,
                ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7f + 1.0f) * 1.6f);
            return true;
        }

        if (!stored.isEmpty()) {
            ItemStack out = stored.copy();
            pedestal.setWand(ItemStack.EMPTY);
            if (held.isEmpty()) {
                playerIn.setHeldItem(hand, out);
            } else if (!playerIn.inventory.addItemStackToInventory(out)) {
                playerIn.dropItem(out, false);
            }
            worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2f,
                ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7f + 1.0f) * 1.5f);
            return true;
        }

        return true;
    }

    private boolean canAcceptRechargeItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.getItem() instanceof IWand
            || stack.getItem() instanceof IRechargable
            || stack.getItem() instanceof ICaster;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileRechargePedestal) {
            ItemStack stored = ((TileRechargePedestal) te).getWand();
            if (!stored.isEmpty()) {
                spawnAsEntity(worldIn, pos, stored.copy());
            }
        }
        super.breakBlock(worldIn, pos, state);
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
