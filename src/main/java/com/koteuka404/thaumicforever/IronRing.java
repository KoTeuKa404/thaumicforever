package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class IronRing extends Item implements IBauble {

    public IronRing() {
        this.setUnlocalizedName("iron_ring");
        this.setRegistryName("iron_ring");
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Override
public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    if (!world.isRemote) {
        RayTraceResult ray = this.rayTrace(world, player, false);
        if (ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK) return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
        BlockPos center = ray.getBlockPos().up(); 

        BlockPos below = center.down();
        world.setBlockState(below, net.minecraft.init.Blocks.GLASS.getDefaultState());
        
        EntityAuraNode node = new EntityAuraNode(world);
        node.setPosition(center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5);
        world.spawnEntity(node);

        for (int x = -1; x <= 1; x++)
        for (int y = -1; y <= 1; y++)
        for (int z = -1; z <= 1; z++) {
            if (x == 0 && y == 0 && z == 0) continue;
            BlockPos p = center.add(x, y, z);
            world.setBlockState(p, net.minecraft.init.Blocks.GLASS.getDefaultState());
        }

        for (int x = -1; x <= 1; x++)
        for (int z = -1; z <= 1; z++) {
            BlockPos p = center.add(x, 2, z);
            world.setBlockState(p, net.minecraft.init.Blocks.WOODEN_SLAB.getDefaultState());
        }

        player.sendMessage(new net.minecraft.util.text.TextComponentString("Node at: " + center + " (клікай Salis Mundus по блоку знизу!)"));
    }
    return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
}

    
}
