package com.koteuka404.thaumicforever;

 
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ThaumicForeverCreativeTab extends CreativeTabs {

    // Конструктор, що передає назву вкладки
    public ThaumicForeverCreativeTab() {
        super("thaumicforever_tab");  // Унікальне ім'я вкладки
    }

    @Override
    public ItemStack getTabIconItem() {
        // Повертає предмет, що використовується як значок вкладки
        return new ItemStack(ModItems.ItemBrassGear);  // Приклад з brass_gear
    }
}
