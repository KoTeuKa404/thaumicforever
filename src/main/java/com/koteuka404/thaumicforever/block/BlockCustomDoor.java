package com.koteuka404.thaumicforever.block;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.BlockDoor.EnumDoorHalf;

public class BlockCustomDoor extends BlockDoor {
    private Item doorItem;

    public BlockCustomDoor(String name) {
        this(name, false);
    }

    public BlockCustomDoor(String name, boolean ironLike) {
        super(ironLike ? Material.IRON : Material.WOOD);
        setRegistryName(name);
        setUnlocalizedName(name);
        setHardness(3.0F);
        setResistance(5.0F);
        setSoundType(ironLike ? SoundType.METAL : SoundType.WOOD);
    }

    public void setDoorItem(Item doorItem) {
        this.doorItem = doorItem;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return doorItem != null ? new ItemStack(doorItem) : ItemStack.EMPTY;
    }

    @Override
    public Item getItemDropped(IBlockState state, java.util.Random rand, int fortune) {
        return state.getValue(HALF) == EnumDoorHalf.UPPER ? Items.AIR : (doorItem != null ? doorItem : Items.AIR);
    }
}
