package com.koteuka404.thaumicforever.entity;

import com.koteuka404.thaumicforever.registry.ModBlocks;
import com.koteuka404.thaumicforever.tile.TileEntityVoidSingularity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityVoidSingularityMinecart extends EntityMinecartTNT {

    public EntityVoidSingularityMinecart(World world) {
        super(world);
    }

    public EntityVoidSingularityMinecart(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return ModBlocks.VOID_SINGULARITY.getDefaultState();
    }

    @Override
    protected void explodeCart(double speedSquared) {
        if (!world.isRemote) {
            TileEntityVoidSingularity.detonateAt(world, posX, posY, posZ);
            setDead();
        }
    }

    @Override
    public void killMinecart(DamageSource source) {
        super.killMinecart(source);
    }
}
