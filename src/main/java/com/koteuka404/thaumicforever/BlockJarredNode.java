package com.koteuka404.thaumicforever;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockJarredNode extends BlockContainer {
    public static final AxisAlignedBB JARRED_NODE_AABB = new AxisAlignedBB(
        0.1875, 0.0, 0.1875,
        0.8125, 0.75, 0.8125
    );

    private static boolean suppressDrop = false;

    public BlockJarredNode() {
        super(Material.GLASS);
        setUnlocalizedName("jarred_node");
        setRegistryName("jarred_node");
        setHardness(0.4F);
        setResistance(1.0F);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return JARRED_NODE_AABB;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
            EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityJarredNode) {
            ItemStack held = player.getHeldItem(hand);
            if (held.isEmpty() || !(held.getItem() instanceof thaumcraft.api.casters.ICaster)) return false;

            NBTTagCompound nodeNBT = ((TileEntityJarredNode) te).getTrueNodeNBT();
            if (nodeNBT != null) {
                EntityAuraNode node = new EntityAuraNode(world);
                node.readEntityFromNBT(nodeNBT);
                node.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                world.spawnEntity(node);

                ((TileEntityJarredNode) te).setNodeNBT(null);

                suppressDrop = true;
                world.setBlockToAir(pos);
                suppressDrop = false;

                return true;
            }
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityJarredNode();
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos,
                         IBlockState state, int fortune) {
        return;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state,
                                          float chance, int fortune) {
        return;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return false;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!suppressDrop) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityJarredNode) {
                NBTTagCompound nbt = new NBTTagCompound();
                ((TileEntityJarredNode) te).writeNodeNBT(nbt);
                if (nbt.hasKey("nodeAspects", 10) && !nbt.getCompoundTag("nodeAspects").hasNoTags()) {
                    ItemStack drop = new ItemStack(ModBlocks.JARRED_NODE);
                    drop.setTagInfo("nodeData", nbt);
                    spawnAsEntity(world, pos, drop);
                }
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World world, IBlockState state) { return new TileEntityJarredNode(); }

    public BlockRenderLayer getRenderLayer() { return BlockRenderLayer.TRANSLUCENT; }
    @Override public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
}
