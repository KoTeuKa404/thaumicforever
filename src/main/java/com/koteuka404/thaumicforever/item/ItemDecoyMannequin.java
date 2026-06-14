package com.koteuka404.thaumicforever.item;

import com.koteuka404.thaumicforever.entity.EntityDecoyMannequin;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemDecoyMannequin extends Item {
    public ItemDecoyMannequin() {
        this.setRegistryName("decoy_mannequin");
        this.setUnlocalizedName("decoy_mannequin");
        this.setMaxStackSize(16);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        BlockPos placePos = world.getBlockState(pos).getMaterial() == Material.AIR ? pos : pos.offset(facing);
        if (!player.canPlayerEdit(placePos, facing, stack)) {
            return EnumActionResult.FAIL;
        }
        if (!world.getBlockState(placePos).getBlock().isReplaceable(world, placePos)) {
            return EnumActionResult.FAIL;
        }

        double x = placePos.getX() + 0.5D;
        double y = placePos.getY();
        double z = placePos.getZ() + 0.5D;
        AxisAlignedBB box = new AxisAlignedBB(x - 0.3D, y, z - 0.3D, x + 0.3D, y + 1.6D, z + 0.3D);
        if (!world.getCollisionBoxes(null, box).isEmpty()) {
            return EnumActionResult.FAIL;
        }

        if (!world.isRemote) {
            EntityDecoyMannequin decoy = new EntityDecoyMannequin(world);
            decoy.setLocationAndAngles(x, y, z, player.rotationYaw + 180.0F, 0.0F);
            world.spawnEntity(decoy);
        }
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }
}
