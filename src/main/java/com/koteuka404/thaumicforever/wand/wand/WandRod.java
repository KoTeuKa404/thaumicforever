package com.koteuka404.thaumicforever.wand.wand;

import com.koteuka404.thaumicforever.wand.api.item.wand.IWandRod;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWandUpdate;
import com.koteuka404.thaumicforever.wand.main.ThaumicWands;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class WandRod implements IWandRod {

    int craftCost;
    int capacity;

    String tag;
    String research;
    ItemStack item;
    IWandUpdate update;


    public WandRod(String tag, int capacity, ItemStack item, int craftCost) {
        this(tag, capacity, item, craftCost, null, "ROD_" + tag.toUpperCase());
    }

    public WandRod(String tag, int capacity, ItemStack item, int craftCost, IWandUpdate update) {
        this(tag, capacity, item, craftCost, update, "ROD_" + tag.toUpperCase());
    }

    public WandRod(String tag, int capacity, ItemStack item, int craftCost, String research) {
        this(tag, capacity, item, craftCost, null, research);
    }

    public WandRod(String tag, int capacity, ItemStack item, int craftCost, IWandUpdate update, String research) {
        this.tag = tag;
        this.capacity = capacity;
        this.item = item;
        this.craftCost = craftCost;
        this.update = update;
        this.research = research;

        TW_Wands.RODS.add(this);
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(ThaumicWands.modID, "textures/items/wand/models/wand_rod_" + tag + ".png");
    }

    @Override
    public ItemStack getItemStack() {
        return item;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public String getRequiredResearch() {
        return research;
    }

    @Override
    public int getCraftCost() {
        return craftCost;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public IWandUpdate getUpdate() {
        return update;
    }

}
