package com.koteuka404.thaumicforever;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;

public class InfusionRecipes {

    public static void init() {
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:ring_runic_charge"), new InfusionRecipe(
            "NEWRUNICS",   
            new ItemStack(ModItems.RING_RUNIC_CHARGE),   
            3,  
            new AspectList()
                .add(Aspect.EXCHANGE, 40)
                .add(Aspect.FIRE, 40)
                .add(Aspect.MAGIC, 60)
                .add(Aspect.AVERSION, 60),
            new ItemStack(thaumcraft.api.items.ItemsTC.baubles, 1, 1),    
            new Object[] {
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(thaumcraft.api.items.ItemsTC.amber),
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(thaumcraft.api.items.ItemsTC.salisMundus)
            }
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:ring_verdant"), new InfusionRecipe(
            "NEWRUNICS",   
            new ItemStack(ModItems.RING_VERDANT),
            3,  
            new AspectList()
                .add(Aspect.EXCHANGE, 40)
                .add(Aspect.PROTECT, 40)
                .add(Aspect.MAGIC, 60)
                .add(Aspect.SENSES, 60),
            new ItemStack(thaumcraft.api.items.ItemsTC.baubles, 1, 1),  
            new Object[] {
                new ItemStack(ItemsTC.charmVerdant),   
                new ItemStack(Items.MILK_BUCKET),
                new ItemStack(thaumcraft.api.items.ItemsTC.salisMundus),
                new ItemStack(thaumcraft.api.items.ItemsTC.amber)
            }
        ));
        if (Loader.isModLoaded("forbiddenmagicre") && Loader.isModLoaded("avaritia")) {
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:ItemFocus4"), new InfusionRecipe(
                "NEWAUROMANCY",   
                new ItemStack(ModItems.FOCUS_4),
                12,  
                new AspectList()
                .add(Aspect.ORDER, 140)
                .add(Aspect.MAGIC, 160)
                .add(Aspect.AURA, 120)
                .add(Aspect.VOID, 80),
                new ItemStack(ItemsTC.focus3),  
                new Object[] {
                    new ItemStack(ItemsTC.focus2), 
                    new ItemStack(ItemsTC.focus2), 
                    new ItemStack(Item.getByNameOrId("forbiddenmagicre:netherstar_block")), 
                    new ItemStack(Item.getByNameOrId("avaritia:extremely_primordial_pearl"))
                }
            ));
        } else {
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:ItemFocus4"), new InfusionRecipe(
                "NEWAUROMANCY",   
                new ItemStack(ModItems.FOCUS_4),
                12,  
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
                "NEWRUNICS",   
                new ItemStack(ModItems.ZOMBIE_HEART_AMULET),   
                4,  
                new AspectList()
                    .add(Aspect.UNDEAD, 80)
                    .add(Aspect.DEATH, 40)
                    .add(Aspect.MAGIC, 60)
                    .add(Aspect.AVERSION, 60),
                new ItemStack(thaumcraft.api.items.ItemsTC.baubles, 1, 4),    
                new Object[] {
                    new ItemStack(ModItems.ItemZombieHeart),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(BlocksTC.fleshBlock),
                    new ItemStack(ItemsTC.salisMundus)
                }
            ));

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:ring_ender"), new InfusionRecipe(
                "NEWRUNICS",   
                new ItemStack(ModItems.RING_ENDER),   
                4,  
                new AspectList()
                    .add(Aspect.ELDRITCH, 40)
                    .add(Aspect.MOTION, 60)
                    .add(Aspect.MAGIC, 60)
                    .add(Aspect.VOID, 60),
                new ItemStack(thaumcraft.api.items.ItemsTC.baubles, 1, 1),    
                new Object[] {
                    new ItemStack(Items.ENDER_PEARL),
                    new ItemStack(ItemsTC.amber),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(Items.CHORUS_FRUIT),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.amber)

                }
            ));
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:time_trap"), new InfusionRecipe(
                "NEWARTIFICE",   
                new ItemStack(ModItems.ItemTimeFreeze),   
                4,  
                new AspectList()
                    .add(Aspect.VOID, 40)
                    .add(Aspect.TRAP, 60)
                    .add(Aspect.MAGIC, 20)
                    .add(Aspect.CRYSTAL, 40),
                new ItemStack(ItemsTC.alumentum),    
                new Object[] {
                    new ItemStack(ItemsTC.quicksilver),
                    new ItemStack(BlocksTC.amberBlock),
                    new ItemStack(Blocks.GOLD_BLOCK),
                    new ItemStack(BlocksTC.amberBlock),
                    new ItemStack(ItemsTC.quicksilver),
                    new ItemStack(BlocksTC.amberBlock),
                    new ItemStack(Blocks.GOLD_BLOCK),
                    new ItemStack(BlocksTC.amberBlock)
     

                }
            ));
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:DUPLICATOR"), new InfusionRecipe(
                "DUPLICATOR",   
                new ItemStack(ModBlocks.Duplicator),   
                8,  
                new AspectList()
                    .add(Aspect.CRAFT, 100)
                    .add(AspectRegistry.MATTERYA, 50)
                    .add(Aspect.MECHANISM, 60)
                    .add(Aspect.ORDER, 40),
                new ItemStack(BlocksTC.tableStone),    
                new Object[] {
                    new ItemStack(ItemsTC.primordialPearl),   
                    new ItemStack(BlocksTC.matrixCost),
                    new ItemStack(BlocksTC.visBattery),
                    new ItemStack(BlocksTC.stoneArcane),
                    new ItemStack(BlocksTC.metalAlchemicalAdvanced),

                    new ItemStack(BlocksTC.stoneArcane),
                    new ItemStack(BlocksTC.visBattery),
                    new ItemStack(BlocksTC.matrixCost),
                    new ItemStack(Blocks.BEACON),
                    new ItemStack(ModItems.AQUAREIA_GEM)     

                }
            ));

        
    }
}
