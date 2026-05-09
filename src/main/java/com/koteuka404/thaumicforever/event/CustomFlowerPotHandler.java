package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.ThaumicForever;

import com.koteuka404.thaumicforever.config.ModConfig;

import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public final class CustomFlowerPotHandler {
    private CustomFlowerPotHandler() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        BlockPos clickedPos = event.getPos();
        BlockPos potPos = findNearbyVanillaPot(world, clickedPos, event.getFace());

        if (potPos == null) {
            return;
        }

        // Prevent offhand fallback around flower pots.
        if (event.getHand() != EnumHand.MAIN_HAND) {
            event.setUseBlock(Event.Result.DENY);
            event.setUseItem(Event.Result.DENY);
            event.setCancellationResult(EnumActionResult.FAIL);
            event.setCanceled(true);
            return;
        }

        ItemStack held = event.getItemStack();
        if (held.isEmpty()) {
            return;
        }

        Block heldBlock = Block.getBlockFromItem(held.getItem());
        if (heldBlock == null || heldBlock == Blocks.AIR) {
            return;
        }
        if (!ModConfig.isAllowedVanillaPotPlant(heldBlock, held.getMetadata())) {
            return;
        }

        TileEntity te = world.getTileEntity(potPos);
        if (!(te instanceof TileEntityFlowerPot)) {
            return;
        }

        TileEntityFlowerPot pot = (TileEntityFlowerPot) te;
        if (!isVanillaPotEmpty(pot)) {
            event.setUseBlock(Event.Result.DENY);
            event.setUseItem(Event.Result.DENY);
            event.setCancellationResult(EnumActionResult.FAIL);
            event.setCanceled(true);
            return;
        }

        boolean inserted = false;
        if (!world.isRemote) {
            ItemStack toPot = held.copy();
            toPot.setCount(1);
            inserted = setPotItem(pot, toPot);
            if (inserted) {
                if (!event.getEntityPlayer().capabilities.isCreativeMode) {
                    held.shrink(1);
                }
                pot.markDirty();
                world.markBlockRangeForRenderUpdate(potPos, potPos);
                world.notifyBlockUpdate(potPos, world.getBlockState(potPos), world.getBlockState(potPos), 3);
            }
        }

        event.setUseBlock(Event.Result.DENY);
        event.setUseItem(Event.Result.DENY);
        event.setCancellationResult(inserted || world.isRemote ? EnumActionResult.SUCCESS : EnumActionResult.FAIL);
        event.setCanceled(true);
    }

    private static boolean setPotItem(TileEntityFlowerPot pot, ItemStack stack) {
        if (invokeIfExists(pot, "setItemStack", new Class<?>[] { ItemStack.class }, new Object[] { stack })) {
            return true;
        }
        return invokeIfExists(pot, "func_190614_a", new Class<?>[] { ItemStack.class }, new Object[] { stack });
    }

    private static boolean invokeIfExists(Object target, String name, Class<?>[] sig, Object[] args) {
        try {
            Method m = target.getClass().getMethod(name, sig);
            m.setAccessible(true);
            m.invoke(target, args);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean isVanillaPotEmpty(TileEntityFlowerPot pot) {
        try {
            return pot.getFlowerItemStack().isEmpty();
        } catch (Throwable ignored) {
            return true;
        }
    }

    private static BlockPos findNearbyVanillaPot(World world, BlockPos clickedPos, EnumFacing face) {
        if (world.getBlockState(clickedPos).getBlock() == Blocks.FLOWER_POT) {
            return clickedPos;
        }

        if (face != null) {
            BlockPos backPos = clickedPos.offset(face.getOpposite());
            if (world.getBlockState(backPos).getBlock() == Blocks.FLOWER_POT) {
                return backPos;
            }
        }

        for (EnumFacing dir : EnumFacing.VALUES) {
            BlockPos p = clickedPos.offset(dir);
            if (world.getBlockState(p).getBlock() == Blocks.FLOWER_POT) {
                return p;
            }
        }
        return null;
    }
}
