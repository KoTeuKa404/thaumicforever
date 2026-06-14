package com.koteuka404.thaumicforever.block;

import com.koteuka404.thaumicforever.item.Primal;
import com.koteuka404.thaumicforever.tile.TilePrimalAuraConverter;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPrimalAuraConverter extends Block {

    public BlockPrimalAuraConverter() {
        super(Material.ROCK);
        setRegistryName("primal_aura_converter");
        setUnlocalizedName("primal_aura_converter");
        setHardness(3.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TilePrimalAuraConverter();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty()) return false;

        TileEntity te = world.getTileEntity(pos);
        if (!world.isRemote && te instanceof TilePrimalAuraConverter) {
            TilePrimalAuraConverter tile = (TilePrimalAuraConverter) te;
            Primal last = tile.getLastPrimal();
            String status = last == null
                    ? "Primal Aura Converter: connect a port below and feed CV."
                    : "Primal Aura Converter: " + last.name().toLowerCase(java.util.Locale.ROOT)
                        + " buffer " + tile.getBufferedCentivis(last) + " CV.";
            player.sendStatusMessage(new TextComponentString(status), true);
        }
        return true;
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
}
