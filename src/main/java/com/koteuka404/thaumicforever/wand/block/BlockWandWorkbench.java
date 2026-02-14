package com.koteuka404.thaumicforever.wand.block;

import com.koteuka404.thaumicforever.ModGuiHandler;
import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.wand.tile.TileArcaneWorkbenchNew;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import com.koteuka404.thaumicforever.wand.inventory.InventoryArcaneWorkbenchNew;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockWandWorkbench extends Block implements ITileEntityProvider {

    public BlockWandWorkbench() {
        super(Material.WOOD);
        setRegistryName(ThaumicForever.MODID, "wand_workbench");
        setUnlocalizedName(ThaumicForever.MODID + ".wand_workbench");
        setHardness(2.5F);
        setSoundType(SoundType.WOOD);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileArcaneWorkbenchNew();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, net.minecraft.entity.player.EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        player.openGui(ThaumicForever.instance, ModGuiHandler.GUI_WAND_WORKBENCH, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileArcaneWorkbenchNew) {
            InventoryArcaneWorkbenchNew inv = (InventoryArcaneWorkbenchNew) ((TileArcaneWorkbenchNew) te).inventoryCraft;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                if (i >= 9 && i <= 14) continue;
                ItemStack stack = inv.getRealStackInSlot(i);
                if (!stack.isEmpty()) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                    inv.clearSlot(i);
                }
            }
        } else if (te instanceof IInventory) {
            InventoryHelper.dropInventoryItems(world, pos, (IInventory) te);
        }
        super.breakBlock(world, pos, state);
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
