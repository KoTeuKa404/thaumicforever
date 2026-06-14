package com.koteuka404.thaumicforever.block;

import java.util.UUID;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.registry.ModGuiHandler;
import com.koteuka404.thaumicforever.tile.TileVoidChest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVoidChest extends Block {
    private static final String TAG_LINK = "ThaumicForeverVoidChestLink";
    private static final String TAG_NETWORK = "NetworkId";
    private static final String TAG_DIM = "Dimension";
    private static final String TAG_X = "X";
    private static final String TAG_Y = "Y";
    private static final String TAG_Z = "Z";
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    private static final AxisAlignedBB CHEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    public BlockVoidChest() {
        super(Material.WOOD);
        setHardness(2.5F);
        setResistance(12.5F);
        setUnlocalizedName(ThaumicForever.MODID + ".void_chest");
        setRegistryName(ThaumicForever.MODID, "void_chest");
        setCreativeTab(ThaumicForever.CREATIVE_TAB);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ThaumicForever.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileVoidChest();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof TileVoidChest)) {
            return false;
        }

        ItemStack held = player.getHeldItem(hand);
        if (held.getItem() == Items.NAME_TAG) {
            if (!world.isRemote) {
                handleNameTagLink((TileVoidChest) tileEntity, player, held, world, pos);
            }
            return true;
        }

        if (!world.isRemote) {
            player.openGui(ThaumicForever.instance, ModGuiHandler.VOID_CHEST_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    private void handleNameTagLink(TileVoidChest tile, EntityPlayer player, ItemStack tagStack, World world, BlockPos pos) {
        NBTTagCompound root = tagStack.getTagCompound();
        if (player.isSneaking() && (root == null || !root.hasKey(TAG_LINK, 10))) {
            tile.resetToOriginalNetwork();
            player.sendMessage(new TextComponentString("Void Chest link reset to its original inventory."));
            return;
        }

        if (root == null) {
            root = new NBTTagCompound();
            tagStack.setTagCompound(root);
        }

        if (!root.hasKey(TAG_LINK, 10)) {
            NBTTagCompound link = new NBTTagCompound();
            link.setString(TAG_NETWORK, tile.getNetworkId().toString());
            link.setInteger(TAG_DIM, world.provider.getDimension());
            link.setInteger(TAG_X, pos.getX());
            link.setInteger(TAG_Y, pos.getY());
            link.setInteger(TAG_Z, pos.getZ());
            root.setTag(TAG_LINK, link);
            tagStack.setStackDisplayName(formatLinkedName(world, pos));
            player.sendMessage(new TextComponentString("Void Chest saved to name tag."));
            return;
        }

        NBTTagCompound link = root.getCompoundTag(TAG_LINK);
        UUID networkId;
        try {
            networkId = UUID.fromString(link.getString(TAG_NETWORK));
        } catch (IllegalArgumentException ex) {
            root.removeTag(TAG_LINK);
            tagStack.clearCustomName();
            player.sendMessage(new TextComponentString("Void Chest name tag data was invalid. Bind it again."));
            return;
        }

        if (tile.getNetworkId().equals(networkId)) {
            player.sendMessage(new TextComponentString("This Void Chest is already connected to that network."));
            return;
        }

        tile.setNetworkId(networkId);
        player.sendMessage(new TextComponentString("Void Chest connected to " + formatCoords(link) + "."));
    }

    private String formatLinkedName(World world, BlockPos pos) {
        return "Void Chest " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " D" + world.provider.getDimension();
    }

    private String formatCoords(NBTTagCompound link) {
        return link.getInteger(TAG_X) + " " + link.getInteger(TAG_Y) + " " + link.getInteger(TAG_Z) + " D" + link.getInteger(TAG_DIM);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CHEST_AABB;
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
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
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

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @SideOnly(Side.CLIENT)
    public net.minecraft.util.BlockRenderLayer getRenderLayer() {
        return net.minecraft.util.BlockRenderLayer.CUTOUT;
    }
}
