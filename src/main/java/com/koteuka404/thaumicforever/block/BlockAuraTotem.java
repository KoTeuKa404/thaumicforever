package com.koteuka404.thaumicforever.block;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAuraTotem extends Block {
    public static final PropertyEnum<TotemType> TYPE = PropertyEnum.create("type", TotemType.class);

    public BlockAuraTotem() {
        super(Material.ROCK);
        setRegistryName(ThaumicForever.MODID, "aura_totem");
        setUnlocalizedName("aura_totem");
        setHardness(0.5F);
        setResistance(5.0F);
        setCreativeTab(ThaumicForever.CREATIVE_TAB);
        setDefaultState(blockState.getBaseState().withProperty(TYPE, TotemType.PUSH));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileAuraTotem();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (TotemType type : new TotemType[] { TotemType.PUSH, TotemType.PULL }) {
            items.add(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        TileAuraTotem tile = findController(world, pos);
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

    public static TileAuraTotem findController(World world, BlockPos pos) {
        TileEntity direct = world.getTileEntity(pos);
        if (direct instanceof TileAuraTotem) return (TileAuraTotem) direct;

        for (int i = 1; i <= 5; i++) {
            BlockPos check = pos.up(i);
            IBlockState state = world.getBlockState(check);
            if (!(state.getBlock() instanceof BlockAuraTotem) && !(state.getBlock() instanceof BlockAuraTotemPole)) break;
            TileEntity tile = world.getTileEntity(check);
            if (tile instanceof TileAuraTotem) return (TileAuraTotem) tile;
        }
        return null;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileAuraTotem) {
            InventoryHelper.dropInventoryItems(world, pos, (TileAuraTotem) tile);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, TotemType.byMeta(meta));
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

    public enum TotemType implements IStringSerializable {
        PUSH,
        PULL;

        public static TotemType byMeta(int meta) {
            TotemType[] values = values();
            if (meta < 0 || meta >= values.length) {
                return PUSH;
            }
            return values[meta];
        }

        @Override
        public String getName() {
            return name().toLowerCase(java.util.Locale.ROOT);
        }
    }
}
