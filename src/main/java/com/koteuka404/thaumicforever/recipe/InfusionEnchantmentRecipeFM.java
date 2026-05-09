package com.koteuka404.thaumicforever.recipe;

import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.util.RecipeMatcher;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
public class InfusionEnchantmentRecipeFM extends InfusionRecipe {

    public final EnumInfusionEnchantment enchantment;
    private final int baseInstability;
    private final AspectList baseAspects;

    public InfusionEnchantmentRecipeFM(EnumInfusionEnchantment enchantment, AspectList aspects, Object... components) {
        super(enchantment.researchKey, null, 6, aspects, Ingredient.EMPTY, components);
        this.enchantment = enchantment;
        this.baseInstability = this.instability;
        this.baseAspects = aspects.copy();
    }

    @Override
    public Object getRecipeOutput() {
        return null;
    }

    @Override
    public Object getRecipeOutput(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
        if (input == null || input.isEmpty()) {
            return input; 
        }

        ItemStack output = input.copy();

        int currentLevel = EnumInfusionEnchantment.getInfusionEnchantmentLevel(output, this.enchantment);

        if (currentLevel >= this.enchantment.maxLevel) {
            return input;
        }

        EnumInfusionEnchantment.addInfusionEnchantment(output, this.enchantment, currentLevel + 1);

        return output;
    }

    @Override
    public boolean matches(List<ItemStack> input, ItemStack central, net.minecraft.world.World world, EntityPlayer player) {
        if (world == null || player == null) {
            return false;
        }
        if (!ThaumcraftCapabilities.knowsResearch(player, research)) {
            return false;
        }
        if (central == null || central.isEmpty()) {
            return false;
        }
        if (!isApplicable(central)) {
            return false;
        }
        int currentLevel = EnumInfusionEnchantment.getInfusionEnchantmentLevel(central, this.enchantment);
        if (currentLevel >= this.enchantment.maxLevel) {
            return false;
        }
        return RecipeMatcher.findMatches((List) input, (List) getComponents()) != null;
    }

    @Override
    public AspectList getAspects(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
        int currentLevel = (input == null || input.isEmpty()) ? 0 : EnumInfusionEnchantment.getInfusionEnchantmentLevel(input, this.enchantment);
        int multiplier = Math.max(1, currentLevel + 1);

        AspectList scaled = new AspectList();
        for (Aspect aspect : this.baseAspects.getAspects()) {
            scaled.add(aspect, this.baseAspects.getAmount(aspect) * multiplier);
        }
        return scaled;
    }

    @Override
    public int getInstability(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
        int currentLevel = (input == null || input.isEmpty()) ? 0 : EnumInfusionEnchantment.getInfusionEnchantmentLevel(input, this.enchantment);
        return this.baseInstability + currentLevel * 2;
    }

    private boolean isApplicable(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        // Unbreakable is intended to be universal in this modpack setup.
        if (this.enchantment == EnumInfusionEnchantment.UNBREAKABLE || this.enchantment.applicableItems.contains("any")) {
            return true;
        }

        Set<String> types = this.enchantment.applicableItems;
        Set<String> toolClasses = stack.getItem().getToolClasses(stack);
        if (types.contains("weapon") && toolClasses.contains("sword")) {
            return true;
        }
        if (types.contains("armor") && isArmor(stack)) {
            return true;
        }
        if (types.contains("weapon") && stack.getItem() instanceof ItemSword) {
            return true;
        }
        if (types.contains("axe") && toolClasses.contains("axe")) {
            return true;
        }
        if (types.contains("pickaxe") && toolClasses.contains("pickaxe")) {
            return true;
        }
        if (types.contains("shovel") && toolClasses.contains("shovel")) {
            return true;
        }
        if (stack.getItem() instanceof ItemTool && types.contains("weapon")) {
            return toolClasses.contains("axe");
        }
        return false;
    }

    private boolean isArmor(ItemStack stack) {
        if (stack.getItem() instanceof ItemArmor) {
            return true;
        }
        for (EntityEquipmentSlot slot : new EntityEquipmentSlot[] {
                EntityEquipmentSlot.HEAD,
                EntityEquipmentSlot.CHEST,
                EntityEquipmentSlot.LEGS,
                EntityEquipmentSlot.FEET
        }) {
            if (stack.getItem().isValidArmor(stack, slot, null)) {
                return true;
            }
        }
        return false;
    }
}
