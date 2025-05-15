package com.koteuka404.thaumicforever;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;

public class BlockImmortalizer extends Block implements ITileEntityProvider {

    public static final net.minecraft.block.properties.PropertyInteger STATE = net.minecraft.block.properties.PropertyInteger.create("state", 0, 3);

    public BlockImmortalizer() {
        super(Material.IRON);
        this.setUnlocalizedName("immortalizer");
        this.setRegistryName("immortalizer");
        this.setHardness(15.0F);
        this.setResistance(40.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(STATE, 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STATE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STATE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(STATE, meta);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            ItemStack heldItem = player.getHeldItem(hand);
            TileEntity tile = world.getTileEntity(pos);
    
            if (tile instanceof TileEntityImmortalizer) {
                TileEntityImmortalizer immortalizer = (TileEntityImmortalizer) tile;
    
                if (!heldItem.isEmpty() && heldItem.getItem() == ItemsTC.primordialPearl) {
    
                    if (TileEntityImmortalizer.REQUIRED_ASPECT == null) {
                        player.sendStatusMessage(
                            new net.minecraft.util.text.TextComponentString("Аспект CAELES не доступний."), true
                        );
                        return true;
                    }
    
                    if (immortalizer.activateImmortality(player)) {
                        heldItem.shrink(1);
    
                        world.setBlockState(pos, state.withProperty(BlockImmortalizer.STATE, 3), 3);
                        immortalizer.markDirty();
    
                        world.playSound(null, pos, net.minecraft.init.SoundEvents.BLOCK_END_PORTAL_SPAWN,
                            net.minecraft.util.SoundCategory.BLOCKS, 1.0F, 1.0F);
                    } else {
                        player.sendStatusMessage(
                            new net.minecraft.util.text.TextComponentTranslation("not enough aspect"), true
                        );
                    }
    
                    return true;
                }
            }
        }
        return true;
    }
    



    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityImmortalizer();
    }
}
