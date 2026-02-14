package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class VoidRepairHandler {

    private static final int REPAIR_INTERVAL = 40;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (EnumInfusionEnchantment.getInfusionEnchantmentLevel(itemStack, EnumInfusionEnchantment.VOIDREPAIR) > 0) {
            List<String> tooltip = event.getToolTip();
            tooltip.add(TextFormatting.DARK_PURPLE + "Void Repair");
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            for (ItemStack itemStack : player.inventory.armorInventory) {
                repairItem(itemStack, player);
            }

            for (ItemStack itemStack : player.inventory.mainInventory) {
                repairItem(itemStack, player);
            }
        }
    }

    private void repairItem(ItemStack itemStack, EntityPlayer player) {
        if (itemStack != null && itemStack.isItemDamaged()
            && EnumInfusionEnchantment.getInfusionEnchantmentLevel(itemStack, EnumInfusionEnchantment.VOIDREPAIR) > 0) {
            if (player.world.getTotalWorldTime() % REPAIR_INTERVAL == 0) {
                itemStack.setItemDamage(itemStack.getItemDamage() - 1);
            }
        }
    }
}

