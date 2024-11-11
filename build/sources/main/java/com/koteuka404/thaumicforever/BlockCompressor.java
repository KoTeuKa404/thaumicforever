package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCompressor extends Block {

    public BlockCompressor() {
        super(Material.IRON);
        setUnlocalizedName("compressor");
        setRegistryName("compressor");
        setHardness(5.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCompressor();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityCompressor) {
                player.openGui(ThaumicForever.instance, ModGuiHandler.GUI_ID_COMPRESSOR, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false; // Робить блок прозорим
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false; // Робить блок нефізичним
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
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        // Додаємо дроп предметів з інвентаря TileEntity при руйнуванні блоку
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityCompressor) {
            TileEntityCompressor compressor = (TileEntityCompressor) tileEntity;

            // Дропаємо всі предмети з інвентаря
            for (int i = 0; i < compressor.getInventory().getSlots(); i++) {
                ItemStack stack = compressor.getInventory().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }

        // Викликаємо стандартний метод для завершення знищення блоку
        super.breakBlock(world, pos, state);
    }
}
