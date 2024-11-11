package com.koteuka404.thaumicforever;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemCluster extends Item {
    // Масив з іменами варіантів кластерів
    public static final String[] CLUSTER_TYPES = new String[]{"chromium", "iridium", "quartz", "charged_quartz"};

    public ItemCluster() {
        this.setHasSubtypes(true); // Вказуємо, що предмет має кілька варіантів
        this.setMaxDamage(0); // Предмет не має міцності
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int metadata = stack.getMetadata();
        if (metadata < 0 || metadata >= CLUSTER_TYPES.length) {
            metadata = 0;
        }
        return super.getUnlocalizedName() + "." + CLUSTER_TYPES[metadata];
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (int i = 0; i < CLUSTER_TYPES.length; i++) {
                items.add(new ItemStack(this, 1, i)); // Додаємо всі підтипи у вкладку
            }
        }
    }
}
