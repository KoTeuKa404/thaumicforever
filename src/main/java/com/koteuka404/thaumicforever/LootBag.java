package com.koteuka404.thaumicforever;

import java.util.List;
import java.util.Random;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.SoundsTC;

public class LootBag extends Item {

    private static final Random RANDOM = new Random();

    public LootBag() {
        setMaxStackSize(16);
        setUnlocalizedName("lootbag");
        setRegistryName("lootbag");
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON; 
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(I18n.translateToLocal("Click to open, or keep to trade.")); 
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            int coinsToDrop = determineCoinAmount();
            for (int i = 0; i < coinsToDrop; i++) {
                ItemStack coinStack = new ItemStack(ModItems.coin);
                EntityItem entityItem = new EntityItem(world, player.posX, player.posY, player.posZ, coinStack);
                world.spawnEntity(entityItem);
            }

          
            player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        if (world.isRemote) {
            player.playSound(SoundsTC.coins, 0.75f, 1.0f);
        }


        stack.shrink(1);

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
}


 
    private int determineCoinAmount() {
        int roll = RANDOM.nextInt(100);
        if (roll < 50) {    
            return 1; 
        } else if (roll < 80) {
            return 2; 
        } else if (roll < 95) {
            return 3;
        } else {
            return 5; 
        }
    }
}
