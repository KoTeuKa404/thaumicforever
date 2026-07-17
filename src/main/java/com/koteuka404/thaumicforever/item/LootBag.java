package com.koteuka404.thaumicforever.item;

import com.koteuka404.thaumicforever.registry.ModItems;

import java.util.List;
import java.util.Random;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.SoundsTC;

public class LootBag extends Item {

    public static final String COIN_COUNT_TAG = "CoinCount";
    private static final Random RANDOM = new Random();

    public LootBag() {
        setMaxStackSize(1);
        setUnlocalizedName("lootbag");
        setRegistryName("lootbag");
    }

    public static ItemStack createStack(int coins) {
        ItemStack stack = new ItemStack(ModItems.lootbag);
        setCoinCount(stack, coins);
        return stack;
    }

    public static void setCoinCount(ItemStack stack, int coins) {
        if (coins <= 0) {
            stack.removeSubCompound(COIN_COUNT_TAG);
            return;
        }
        stack.getOrCreateSubCompound(COIN_COUNT_TAG).setInteger("Amount", coins);
    }

    public static int getCoinCount(ItemStack stack) {
        NBTTagCompound tag = stack.getSubCompound(COIN_COUNT_TAG);
        return tag == null ? 0 : Math.max(0, tag.getInteger("Amount"));
    }

    public static int rollDropAmount() {
        int roll = RANDOM.nextInt(100);
        if (roll < 50) return 1;
        if (roll < 80) return 2;
        if (roll < 95) return 3;
        return 5;
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        // Vanilla crafting removes only one item from each input slot. The
        // bag recipe consumes the complete coin stacks in one operation.
        if (player == null || player.openContainer == null) return;
        for (Slot slot : player.openContainer.inventorySlots) {
            if (slot.inventory instanceof InventoryCrafting
                && !slot.getStack().isEmpty()
                && slot.getStack().getItem() == ModItems.coin) {
                slot.putStack(ItemStack.EMPTY);
                slot.onSlotChanged();
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(TextFormatting.GOLD + "Stores " + getCoinCount(stack) + " coin");
        tooltip.add(TextFormatting.GRAY + "Right-click to release all coins.");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack bag = player.getHeldItem(hand);
        int coins = getCoinCount(bag);

        if (!world.isRemote && coins > 0) {
            int remaining = coins;
            while (remaining > 0) {
                int amount = Math.min(64, remaining);
                ItemStack coinStack = new ItemStack(ModItems.coin, amount);
                if (!player.inventory.addItemStackToInventory(coinStack)) {
                    player.dropItem(coinStack, false);
                }
                remaining -= amount;
            }

            bag.shrink(1);
            player.playSound(SoundsTC.coins, 0.75F, 1.0F);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, bag);
    }
}
