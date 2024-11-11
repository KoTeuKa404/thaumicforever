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

    private static final int REPAIR_INTERVAL = 40; // 40 ticks = 2 seconds

    // Add the "Void Repair" text to the tooltip when the item has the Void Repair enchantment;
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (EnumInfusionEnchantment.getInfusionEnchantmentLevel(itemStack, EnumInfusionEnchantment.VOIDREPAIR) > 0) {
            List<String> tooltip = event.getToolTip();
            tooltip.add(TextFormatting.DARK_PURPLE + "Void Repair"); // Display "Void Repair" in purple
        }
    }

    // Repair items with the Void Repair enchantment over time (every 2 seconds);
    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            // Iterate through the player's armor inventory
            for (ItemStack itemStack : player.inventory.armorInventory) {
                repairItem(itemStack, player);
            }

            // Iterate through the player's main inventory
            for (ItemStack itemStack : player.inventory.mainInventory) {
                repairItem(itemStack, player);
            }
        }
    }

    // Function to handle item repair logic
    private void repairItem(ItemStack itemStack, EntityPlayer player) {
        if (itemStack != null && itemStack.isItemDamaged() 
            && EnumInfusionEnchantment.getInfusionEnchantmentLevel(itemStack, EnumInfusionEnchantment.VOIDREPAIR) > 0) {
            // Check if the current tick count is divisible by the repair interval (every 2 seconds)
            if (player.world.getTotalWorldTime() % REPAIR_INTERVAL == 0) {
                itemStack.setItemDamage(itemStack.getItemDamage() - 1); // Repair by 1 durability
            }
        }
    }
}

