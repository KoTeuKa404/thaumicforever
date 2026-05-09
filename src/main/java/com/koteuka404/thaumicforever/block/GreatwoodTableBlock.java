package com.koteuka404.thaumicforever.block;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.container.InventoryFake;
import com.koteuka404.thaumicforever.ThaumicForever;

public class GreatwoodTableBlock extends Block {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public GreatwoodTableBlock() {
        super(Material.WOOD);
        setUnlocalizedName(ThaumicForever.MODID + ".greatwood_table");
        setHardness(2.5F);
        setResistance(12.5F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
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

    @SideOnly(Side.CLIENT)
    public net.minecraft.util.BlockRenderLayer getRenderLayer() {
        return net.minecraft.util.BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty() || !(held.getItem() instanceof IScribeTools)) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }

        IBlockState bs = com.wonginnovations.oldresearch.common.blocks.ModBlocks.RESEARCHTABLE.getDefaultState();
        bs = bs.withProperty(IBlockFacingHorizontal.FACING, player.getHorizontalFacing());
        world.setBlockState(pos, bs);

        net.minecraft.tileentity.TileEntity te = world.getTileEntity(pos);
        if (te instanceof com.wonginnovations.oldresearch.common.tiles.TileResearchTable) {
            ((com.wonginnovations.oldresearch.common.tiles.TileResearchTable) te).setInventorySlotContents(0, held.copy());
            te.markDirty();
        }
        player.setHeldItem(hand, ItemStack.EMPTY);
        player.inventory.markDirty();
        world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), bs, bs, 3);
        FMLCommonHandler.instance().firePlayerCraftingEvent(
            player,
            new ItemStack(com.wonginnovations.oldresearch.common.blocks.ModBlocks.RESEARCHTABLE),
            new InventoryFake(1)
        );
        return true;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
}
