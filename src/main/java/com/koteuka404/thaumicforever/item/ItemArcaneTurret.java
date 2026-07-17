package com.koteuka404.thaumicforever.item;

import java.util.List;

import com.koteuka404.thaumicforever.entity.EntityArcaneTurret;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;

public class ItemArcaneTurret extends Item {
    public ItemArcaneTurret() {
        this.setUnlocalizedName("turret_arcane");
        this.setRegistryName("turret_arcane");
        this.setMaxStackSize(16);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (side == EnumFacing.DOWN) {
            return EnumActionResult.PASS;
        }

        ItemStack stack = player.getHeldItem(hand);
        boolean replaceable = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
        BlockPos placePos = replaceable ? pos : pos.offset(side);
        if (!player.canPlayerEdit(placePos, side, stack)) {
            return EnumActionResult.PASS;
        }

        BlockPos upperPos = placePos.up();
        boolean blocked = !world.isAirBlock(placePos) && !world.getBlockState(placePos).getBlock().isReplaceable(world, placePos);
        blocked |= !world.isAirBlock(upperPos) && !world.getBlockState(upperPos).getBlock().isReplaceable(world, upperPos);
        if (blocked) {
            return EnumActionResult.PASS;
        }

        AxisAlignedBB box = new AxisAlignedBB(placePos.getX(), placePos.getY(), placePos.getZ(), placePos.getX() + 1.0D, placePos.getY() + 2.0D, placePos.getZ() + 1.0D);
        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, box);
        if (!entities.isEmpty()) {
            return EnumActionResult.PASS;
        }

        if (!world.isRemote) {
            world.setBlockToAir(placePos);
            world.setBlockToAir(upperPos);
            EntityOwnedConstruct turret = new EntityArcaneTurret(world, placePos);
            world.spawnEntity(turret);
            turret.setOwned(true);
            turret.setValidSpawn();
            turret.setOwnerId(player.getUniqueID());
            world.playSound(null, turret.posX, turret.posY, turret.posZ, SoundEvents.ENTITY_ARMORSTAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }
}
