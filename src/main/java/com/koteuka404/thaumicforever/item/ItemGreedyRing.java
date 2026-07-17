package com.koteuka404.thaumicforever.item;

import java.util.List;

import com.koteuka404.thaumicforever.registry.ModItems;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class ItemGreedyRing extends Item implements IBauble {

    public static final int BASE_EXTRA_LOOT_ROLLS = 3;

    public static final int COINS_PER_EXTRA_ROLL = 64;

    public static final int MAX_EXTRA_LOOT_ROLLS = 64;

    public ItemGreedyRing() {
        setUnlocalizedName("ring_greedy");
        setRegistryName("ring_greedy");
        setMaxStackSize(1);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.RING;
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase entity) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase entity) {
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    public static boolean isEquipped(EntityPlayer player) {
        if (player == null) {
            return false;
        }

        IItemHandler baubles = BaublesApi.getBaublesHandler(player);

        if (baubles == null) {
            return false;
        }

        for (int i = 0; i < baubles.getSlots(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);

            if (!stack.isEmpty() && stack.getItem() == ModItems.RING_GREEDY) {
                return true;
            }
        }

        return false;
    }

    public static int getExtraLootRolls(EntityPlayer player) {
        if (player == null) {
            return BASE_EXTRA_LOOT_ROLLS;
        }

        int coins = countCoins(player);
        int coinBonusRolls = coins / COINS_PER_EXTRA_ROLL;

        int totalRolls = BASE_EXTRA_LOOT_ROLLS + coinBonusRolls;

        if (totalRolls > MAX_EXTRA_LOOT_ROLLS) {
            return MAX_EXTRA_LOOT_ROLLS;
        }

        return totalRolls;
    }

    private static int countCoins(EntityPlayer player) {
        int coins = 0;

        for (ItemStack stack : player.inventory.mainInventory) {
            coins += getCoinValue(stack);
        }

        for (ItemStack stack : player.inventory.offHandInventory) {
            coins += getCoinValue(stack);
        }

        return coins;
    }

    private static int getCoinValue(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        if (stack.getItem() == ModItems.coin) {
            return stack.getCount();
        }
        if (stack.getItem() == ModItems.lootbag) {
            return LootBag.getCoinCount(stack) * stack.getCount();
        }
        return 0;
    }

    @SideOnly(Side.CLIENT)
@Override
public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
    EntityPlayer player = Minecraft.getMinecraft().player;

    int coins = 0;
    int extraRolls = BASE_EXTRA_LOOT_ROLLS;
    int totalRolls = BASE_EXTRA_LOOT_ROLLS + 1;
    int coinBonusRolls = 0;
    int coinsToNextRoll = COINS_PER_EXTRA_ROLL;

    if (player != null) {
        coins = countCoins(player);
        coinBonusRolls = coins / COINS_PER_EXTRA_ROLL;
        extraRolls = getExtraLootRolls(player);
        totalRolls = extraRolls + 1;

        int remainder = coins % COINS_PER_EXTRA_ROLL;
        coinsToNextRoll = COINS_PER_EXTRA_ROLL - remainder;

        if (extraRolls >= MAX_EXTRA_LOOT_ROLLS) {
            coinsToNextRoll = 0;
        }
    }

    tooltip.add(
            TextFormatting.GRAY + "Current loot rolls: "
                    + TextFormatting.GOLD + totalRolls
                    + TextFormatting.GRAY + " total"
    );

    tooltip.add(
            TextFormatting.DARK_GRAY + "Base: "
                    + TextFormatting.GRAY + (BASE_EXTRA_LOOT_ROLLS + 1)
                    + TextFormatting.DARK_GRAY + " | Coin bonus: "
                    + TextFormatting.GOLD + "+" + coinBonusRolls
    );

    tooltip.add(
            TextFormatting.DARK_GRAY + "Every "
                    + TextFormatting.GOLD + COINS_PER_EXTRA_ROLL
                    + TextFormatting.DARK_GRAY + " coins = "
                    + TextFormatting.GOLD + "+1"
                    + TextFormatting.DARK_GRAY + " extra roll"
    );

    super.addInformation(stack, world, tooltip, flag);
}
}
