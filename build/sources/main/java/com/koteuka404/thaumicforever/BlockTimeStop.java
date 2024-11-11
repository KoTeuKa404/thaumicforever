package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTimeStop extends Block {

    public BlockTimeStop() {
        super(Material.GLASS); // Використовуємо скляний матеріал для магічного поля
        setHardness(10.0F); // Блок важко зламати
        setResistance(100.0F); // Висока стійкість до вибухів
        setLightOpacity(1); // Мінімальна прозорість
        setUnlocalizedName("time_stop_block");
        setRegistryName("time_stop_block");
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
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT; // Напівпрозорий рендер
    }
    @Override
    public void updateTick(World world, BlockPos pos, net.minecraft.block.state.IBlockState state, java.util.Random rand) {
        // Видаляємо блок після певного часу
        world.setBlockToAir(pos);
    }
}

