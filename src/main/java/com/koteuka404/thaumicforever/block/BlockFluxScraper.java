package com.koteuka404.thaumicforever.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import com.koteuka404.thaumicforever.tile.TileEntityFluxScraper;
import com.koteuka404.thaumicforever.ThaumicForever;

public class BlockFluxScraper extends Block implements ITileEntityProvider {

    public BlockFluxScraper() {
        super(Material.ROCK);
        setUnlocalizedName("flux_scraper");
        setRegistryName("flux_scraper");
        setHardness(4.0F);
        setResistance(8.0F);
        setCreativeTab(ThaumicForever.CREATIVE_TAB);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityFluxScraper();
    }
}
