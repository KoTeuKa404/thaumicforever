package com.tutorialmod.turtywurty;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader; // Для перевірки завантаження модів
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;

public class InfusionRecipes {

    public static void init() {
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:ring_runic_charge"), new InfusionRecipe(
            "NEWRUNICS",  // Назва дослідження
            new ItemStack(ModItems.RING_RUNIC_CHARGE),  // Вихідний предмет
            3,  // Рівень нестабільності
            new AspectList()
                .add(Aspect.EXCHANGE, 40)
                .add(Aspect.FIRE, 40)
                .add(Aspect.MAGIC, 60)
                .add(Aspect.AVERSION, 60),
            new ItemStack(thaumcraft.api.items.ItemsTC.baubles, 1, 1),  // Центральний інгредієнт (Amulet of Vis)
            new Object[] {
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(thaumcraft.api.items.ItemsTC.amber),
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(thaumcraft.api.items.ItemsTC.salisMundus)
            }
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:ring_verdant"), new InfusionRecipe(
            "NEWRUNICS",  // Назва дослідження
            new ItemStack(ModItems.RING_VERDANT),
            3,  // Рівень нестабільності
            new AspectList()
                .add(Aspect.EXCHANGE, 40)
                .add(Aspect.PROTECT, 40)
                .add(Aspect.MAGIC, 60)
                .add(Aspect.SENSES, 60),
            new ItemStack(thaumcraft.api.items.ItemsTC.baubles, 1, 1), // Central item
            new Object[] {
                new ItemStack(ItemsTC.charmVerdant), // Assuming charmVerdant exists within ItemsTC
                new ItemStack(Items.MILK_BUCKET),
                new ItemStack(thaumcraft.api.items.ItemsTC.salisMundus),
                new ItemStack(thaumcraft.api.items.ItemsTC.amber)
            }
        ));
        if (Loader.isModLoaded("forbiddenmagicre") && Loader.isModLoaded("avaritia")) {
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:ItemFocus4"), new InfusionRecipe(
                "NEWAUROMANCY",  // Назва дослідження
                new ItemStack(ModItems.FOCUS_4),
                12,  // Рівень нестабільності
                new AspectList()
                .add(Aspect.ORDER, 140)
                .add(Aspect.MAGIC, 160)
                .add(Aspect.AURA, 120)
                .add(Aspect.VOID, 80),
                new ItemStack(ItemsTC.focus3, 1, 1), // Central item
                new Object[] {
                    new ItemStack(ItemsTC.focus2), 
                    new ItemStack(ItemsTC.focus2), 
                    new ItemStack(Item.getByNameOrId("forbiddenmagicre:netherstar_block")), 
                    new ItemStack(Item.getByNameOrId("avaritia:extremely_primordial_pearl"))
                }
            ));
        } else {
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:ItemFocus4"), new InfusionRecipe(
                "NEWAUROMANCY",  // Назва дослідження
                new ItemStack(ModItems.FOCUS_4),
                12,  // Рівень нестабільності
                new AspectList()
                    .add(Aspect.ORDER, 140)
                    .add(Aspect.MAGIC, 160)
                    .add(Aspect.AURA, 120)
                    .add(Aspect.VOID, 80),
                new ItemStack(ItemsTC.focus3),
                    new ItemStack(ItemsTC.focus2), 
                    new ItemStack(ItemsTC.primordialPearl), 
                    new ItemStack(ItemsTC.primordialPearl), 
                    new ItemStack(ItemsTC.primordialPearl), 

                    new ItemStack(ItemsTC.focus2), 
                    new ItemStack(Items.NETHER_STAR), 
                    new ItemStack(Items.NETHER_STAR), 
                    new ItemStack(Items.NETHER_STAR)

                ));}
        
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:zombie_heart_amulet"), new InfusionRecipe(
                "NEWRUNICS",  // Назва дослідження
                new ItemStack(ModItems.ZOMBIE_HEART_AMULET),  // Вихідний предмет
                4,  // Рівень нестабільності
                new AspectList()
                    .add(Aspect.UNDEAD, 80)
                    .add(Aspect.DEATH, 40)
                    .add(Aspect.MAGIC, 60)
                    .add(Aspect.AVERSION, 60),
                new ItemStack(thaumcraft.api.items.ItemsTC.baubles, 1, 4),  // Центральний інгредієнт (Amulet of Vis)
                new Object[] {
                    new ItemStack(ModItems.ItemZombieHeart),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(BlocksTC.fleshBlock)
                }
            ));

            // ThaumcraftApi.addInfusionEnchantmentRecipe(new ResourceLocation("thaumicforever:void_repair_recipe"),
            // new InfusionEnchantmentRecipe(
            //     "NEWRUNICS", // Ідентифікатор рецепта
            //     ModEnchantments.VOID_REPAIR, // Саме зачарування
            //     1, // Рівень зачарування
            //     new AspectList().add(Aspect.VOID, 50).add(Aspect.LIFE, 25).add(Aspect.MAGIC, 25), // Аспекти
            //     new IngredientNBTTC(new ItemStack(ItemsTC.salisMundus)), // Центральний предмет може бути будь-яким
            //     new Object[]{
            //         new ItemStack(ItemsTC.quicksilver),
            //         new ItemStack(ItemsTC.quicksilver),
            //         new ItemStack(ItemsTC.voidSeed),
            //         new ItemStack(ItemsTC.voidSeed),
            //         new ItemStack(ItemsTC.ingots, 1, 1),
            //         new ItemStack(ItemsTC.ingots, 1, 1),
            //     }
            // ));


        
    }
}
