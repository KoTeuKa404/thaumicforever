package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRedRose extends BlockBush {
    public BlockRedRose() {
        super();
        setRegistryName("red_rose");
        setUnlocalizedName("red_rose");
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        // Дозволяємо рости тільки на траві, ґрунті або ріллі
        return state.getBlock() == Blocks.GRASS
                || state.getBlock() == Blocks.DIRT
                || state.getBlock() == Blocks.FARMLAND;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        // Викликаємо метод для перевірки блоку під квіткою
        IBlockState soil = worldIn.getBlockState(pos.down());
        return this.canSustainBush(soil);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        // Видаляємо квітку, якщо блок під нею більше не підтримує її
        if (!canPlaceBlockAt(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }
}
