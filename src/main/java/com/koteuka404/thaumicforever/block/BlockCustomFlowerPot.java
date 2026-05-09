package com.koteuka404.thaumicforever.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import com.koteuka404.thaumicforever.tile.TileCustomFlowerPot;

public class BlockCustomFlowerPot extends BlockContainer {
    private static final AxisAlignedBB POT_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D);

    public BlockCustomFlowerPot() {
        super(Material.CIRCUITS);
        setRegistryName("flower_pot_custom");
        setUnlocalizedName("flower_pot_custom");
        setHardness(0.0F);
        setSoundType(SoundType.STONE);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCustomFlowerPot();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return POT_AABB;
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

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
            EnumFacing facing, float hitX, float hitY, float hitZ) {
        // Prevent double-processing (main hand + offhand) that could instantly remove freshly planted flowers.
        if (hand != EnumHand.MAIN_HAND) {
            return true;
        }

        ItemStack held = player.getHeldItem(hand);
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TileCustomFlowerPot)) {
            return false;
        }
        TileCustomFlowerPot pot = (TileCustomFlowerPot) te;

        if (pot.isEmpty()) {
            if (!isPlantItem(held)) {
                return false;
            }
            if (!worldIn.isRemote) {
                pot.setPlant(copySingle(held));
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                    player.inventory.markDirty();
                    player.inventoryContainer.detectAndSendChanges();
                }
            }
            return true;
        }

        if (!worldIn.isRemote) {
            ItemStack plant = pot.removePlant();
            if (!plant.isEmpty()) {
                if (!player.inventory.addItemStackToInventory(plant)) {
                    player.dropItem(plant, false);
                }
            }

            if (isPlantItem(held)) {
                pot.setPlant(copySingle(held));
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                    player.inventory.markDirty();
                    player.inventoryContainer.detectAndSendChanges();
                }
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileCustomFlowerPot) {
            ItemStack plant = ((TileCustomFlowerPot) te).getPlant();
            if (!plant.isEmpty()) {
                spawnAsEntity(worldIn, pos, plant.copy());
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public Item getItemDropped(IBlockState state, java.util.Random rand, int fortune) {
        return Items.FLOWER_POT;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(Items.FLOWER_POT);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(Items.FLOWER_POT));
    }

    private static boolean isPlantItem(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemBlock)) {
            return false;
        }
        Block block = ((ItemBlock) stack.getItem()).getBlock();
        return block instanceof BlockBush || block instanceof IPlantable;
    }

    private static ItemStack copySingle(ItemStack stack) {
        ItemStack one = stack.copy();
        one.setCount(1);
        return one;
    }
}
