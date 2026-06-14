package com.koteuka404.thaumicforever.block;

import java.util.Locale;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.tile.TileAuraTotem;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAuraTotemPole extends Block {
    public static final PropertyEnum<PoleType> TYPE = PropertyEnum.create("type", PoleType.class);
    public static final AxisAlignedBB POLE_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

    public BlockAuraTotemPole() {
        super(Material.ROCK);
        setRegistryName(ThaumicForever.MODID, "aura_totem_pole");
        setUnlocalizedName("aura_totem_pole");
        setHardness(0.5F);
        setResistance(5.0F);
        setCreativeTab(ThaumicForever.CREATIVE_TAB);
        setDefaultState(blockState.getBaseState().withProperty(TYPE, PoleType.POLE_OUTER));
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return POLE_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return POLE_AABB;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (PoleType type : PoleType.values()) {
            items.add(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        TileAuraTotem tile = BlockAuraTotem.findController(world, pos);
        if (tile == null) return false;

        if (TileAuraTotem.isValidCrystal(held)) {
            if (!world.isRemote && tile.insertCrystal(held)) {
                if (!player.capabilities.isCreativeMode) held.shrink(1);
            }
            return true;
        }

        if (player.isSneaking() && held.isEmpty()) {
            if (!world.isRemote) {
                ItemStack removed = tile.removeOneCrystal();
                if (!removed.isEmpty() && !player.inventory.addItemStackToInventory(removed)) {
                    InventoryHelper.spawnItemStack(world, player.posX, player.posY, player.posZ, removed);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, PoleType.byMeta(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public Item getItemDropped(IBlockState state, java.util.Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    public enum PoleType implements IStringSerializable {
        POLE_OUTER,
        POLE_INNER,
        POLE_PURE;

        public static PoleType byMeta(int meta) {
            PoleType[] values = values();
            if (meta < 0 || meta >= values.length) {
                return POLE_OUTER;
            }
            return values[meta];
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
