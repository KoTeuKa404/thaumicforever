package com.koteuka404.thaumicforever.recipe;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import com.koteuka404.thaumicforever.registry.ModItems;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import com.koteuka404.thaumicforever.block.BlockImmortalizer;
import com.koteuka404.thaumicforever.block.BlockTimeStone;
import com.koteuka404.thaumicforever.item.ItemFocus4;
import com.koteuka404.thaumicforever.item.ItemTaintAmulet;
import com.koteuka404.thaumicforever.item.ItemThaumiumGear;
import com.koteuka404.thaumicforever.item.ItemTimeFreeze;
import com.koteuka404.thaumicforever.item.ItemVoidGear;
import com.koteuka404.thaumicforever.item.ItemZombieHeart;
import com.koteuka404.thaumicforever.item.RavenCloakItemH;
import com.koteuka404.thaumicforever.item.RingIron;
import com.koteuka404.thaumicforever.registry.AspectRegistry;

public class InfusionRecipes {
    private static Aspect CAELES;
    static {
        try {
            Class<?> knowledgeTarClass = Class.forName("org.zeith.thaumicadditions.init.KnowledgeTAR");
            CAELES = (Aspect) knowledgeTarClass.getField("CAELES").get(null);
        } catch (Exception e) {}
    }
    private static Aspect TIME;
    static {
        try {
            Class<?> pa = Class.forName("leppa.planarartifice.registry.PAAspects");
            TIME = (Aspect) pa.getField("TIME").get(null);
        } catch (Exception e) {
            TIME = Aspect.getAspect("tempus");
        }
    }
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

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:artifact_ruby_ring"), new InfusionRecipe(
            "!VAMPIRE_BAT",
            new ItemStack(ModItems.ARTIFACT_RUBY_RING),
            6,
            new AspectList()
                .add(Aspect.LIFE, 80)
                .add(Aspect.DEATH, 80)
                .add(Aspect.BEAST, 60)
                .add(Aspect.AVERSION, 50)
                .add(Aspect.MAGIC, 60),
            new ItemStack(thaumcraft.api.items.ItemsTC.baubles, 1, 1),
            new Object[] {
                new ItemStack(ModItems.ruby_gem),
                new ItemStack(ItemsTC.salisMundus),
                new ItemStack(Items.ROTTEN_FLESH),

                new ItemStack(Items.GHAST_TEAR),
                new ItemStack(ItemsTC.quicksilver),
                new ItemStack(ModItems.ruby_gem),
                new ItemStack(ItemsTC.salisMundus),
                new ItemStack(Items.FERMENTED_SPIDER_EYE)
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
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    Ingredient.fromItem(ItemsTC.primordialPearl),

                    new ItemStack(ItemsTC.focus2),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(Items.NETHER_STAR)

                ));}

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:FOCUS_COMPLEX"), new InfusionRecipe(
            "NEWAUROMANCY",
            new ItemStack(ModItems.FOCUS_COMPLEX),
            8,
            new AspectList()
                .add(Aspect.ORDER, 120)
                .add(Aspect.MAGIC, 120)
                .add(Aspect.AURA, 120)
                .add(Aspect.SENSES, 60)
                .add(Aspect.ENERGY, 60),
            new ItemStack(ModItems.EMPTY_FOCUS),
            new Object[] {
                Ingredient.fromItem(ItemsTC.primordialPearl),
                new ItemStack(ModItems.AQUAREIA_GEM),
                new ItemStack(ItemsTC.salisMundus),
                new ItemStack(ItemsTC.visResonator),
                Ingredient.fromItem(ItemsTC.primordialPearl),
                new ItemStack(ModItems.AQUAREIA_GEM),
                new ItemStack(ItemsTC.salisMundus),
                new ItemStack(ItemsTC.visResonator)
            }
        ));
        
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
                    new ItemStack(ModItems.AQUAREIA_GEM),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(BlocksTC.fleshBlock),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ModItems.AQUAREIA_GEM)
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
                    Ingredient.fromItem(ItemsTC.primordialPearl),
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
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:CLOAK"), new InfusionRecipe(
                "CLOAK",
                new ItemStack(ModItems.RavenCloakBauble),
                3,
                new AspectList()
                    .add(Aspect.MOTION, 100)
                    .add(Aspect.DARKNESS, 60)
                    .add(Aspect.VOID, 60)
                    .add(Aspect.FLIGHT, 40),
                new ItemStack(Items.LEATHER_CHESTPLATE),
                new Object[] {
                    new ItemStack(Items.DYE,1,0),
                    new ItemStack(ItemsTC.fabric),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.fabric),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.fabric),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.fabric),
                    new ItemStack(ItemsTC.salisMundus),
                }
            ));
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:CLOAK1"), new InfusionRecipe(
                "CLOAK",
                new ItemStack(ModItems.RavenCloakItemH),
                3,
                new AspectList()
                    .add(Aspect.MOTION, 100)
                    .add(Aspect.DARKNESS, 60)
                    .add(Aspect.VOID, 60)
                    .add(Aspect.FLIGHT, 40),
                new ItemStack(Items.LEATHER_HELMET),
                new Object[] {
                    new ItemStack(Items.DYE,1,0),
                    new ItemStack(ItemsTC.fabric),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.fabric),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.fabric),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.fabric),
                    new ItemStack(ItemsTC.salisMundus)
                }
            ));
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:time_stone"), new InfusionRecipe(
                "TIMESTONE",
                new ItemStack(ModBlocks.BlockTimeStone),
                3,
                new AspectList()
                    .add(Aspect.ORDER, 80)
                    .add(Aspect.ENTROPY, 60)
                    .add(Aspect.VOID, 60)
                    .add(Aspect.ENERGY, 80)
                    .add(Aspect.SENSES, 60)
                    .add(Aspect.DESIRE, 60)
                    .add(Aspect.ELDRITCH, 80),
                new ItemStack(Blocks.GOLD_BLOCK),
                new Object[] {
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(Items.ENDER_EYE),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.quicksilver),
                    new ItemStack(Blocks.BEACON),
                    new ItemStack(ItemsTC.mechanismComplex),
                    new ItemStack(Items.NETHER_STAR)
                }
            ));

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:flying_crystal"), new InfusionRecipe(
                "AQUAALKIMIA",
                new ItemStack(Item.getByNameOrId("astralsorcery:blockcollectorcrystal"), 1, 0),
                5,
                new AspectList()
                    .add(Aspect.AIR, 50)
                    .add(Aspect.CRYSTAL, 80)
                    .add(Aspect.LIGHT, 60)
                    .add(Aspect.ENERGY, 50)
                    .add(Aspect.ELDRITCH, 70),
                new ItemStack(Item.getByNameOrId("minecraft:beacon")),
                new Object[] {
                    new ItemStack(Item.getByNameOrId("minecraft:diamond"), 1),
                    new ItemStack(Item.getByNameOrId("astralsorcery:itemcraftingcomponent"), 1, 0),
                    new ItemStack(Item.getByNameOrId("minecraft:nether_star")),
                    new ItemStack(Item.getByNameOrId("minecraft:ghast_tear")),
                    new ItemStack(Item.getByNameOrId("minecraft:ender_eye")),
                    new ItemStack(Item.getByNameOrId("astralsorcery:blockcustomore"), 1, 0),
                    new ItemStack(Item.getByNameOrId("minecraft:quartz"))
                }
            ));

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:crystalworld"), new InfusionRecipe(
                "TELEPORT",
                new ItemStack(ModItems.VOID_TELEPORT_ITEM),
                15,
                new AspectList()
                    .add(Aspect.AIR, 150)
                    .add(Aspect.CRYSTAL, 150)
                    .add(Aspect.VOID, 150)
                    .add(Aspect.SOUL, 150)
                    .add(Aspect.FLIGHT, 150)
                    .add(Aspect.PROTECT, 150)
                    .add(Aspect.ELDRITCH, 150),
                new ItemStack(BlocksTC.mirror),
                new Object[] {
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(Items.ENDER_EYE),
                    new ItemStack(ModItems.primalingot),
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(ModItems.primalingot),
                    new ItemStack(ItemsTC.morphicResonator),
                    new ItemStack(ItemsTC.visResonator),
                    new ItemStack(ModItems.primalingot),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(Items.ENDER_EYE),
                    new ItemStack(ModItems.primalingot),
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(ModItems.primalingot),
                    new ItemStack(ItemsTC.morphicResonator),
                    new ItemStack(ItemsTC.visResonator),
                    new ItemStack(ModItems.primalingot),

                }
            ));

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:eternal_blade"), new InfusionRecipe(
                "ETERNAL_BLADE",
                new ItemStack(ModItems.ETERNAL_BLADE),
                15,
                new AspectList()
                    .add(Aspect.DEATH, 666)
                    .add(Aspect.DARKNESS, 666)
                    .add(Aspect.ELDRITCH, 666)
                    .add(Aspect.SOUL, 666)
                    .add(Aspect.UNDEAD, 666)
                    .add(Aspect.BEAST, 666)
                    .add(Aspect.ENTROPY, 666)
                    .add(Aspect.AVERSION, 666),
                new ItemStack(ItemsTC.crimsonBlade),
                new Object[] {
                    new ItemStack(ModItems.primalingot),
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(ModItems.primalingot),
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(ModItems.primalingot),
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(ModItems.primalingot),
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(ModItems.primalingot),
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(ModItems.primalingot),
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(ModItems.primalingot),
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    new ItemStack(Items.NETHER_STAR)
                }
            ));
            

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:ELDRITCHAMULET"), new InfusionRecipe(
                "ELDRITCHAMULET",
                new ItemStack(ModItems.ELDTRITCH_EYE_AMULET),
                7,
                new AspectList()
                    .add(Aspect.FLUX, 65)
                    .add(Aspect.MIND, 65)
                    .add(Aspect.ELDRITCH, 65)
                    .add(Aspect.SOUL, 65)
                    .add(Aspect.MAGIC, 65),
                new ItemStack(ModItems.primalingot),
                new Object[] {
                    new ItemStack(ItemsTC.curio,1,3),
                    new ItemStack(ItemsTC.plate,1,3),
                    new ItemStack(ItemsTC.visResonator),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(ItemsTC.morphicResonator),
                    new ItemStack(ItemsTC.plate,1,3)
                }
            ));

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:PrimordialGuardian"), new InfusionRecipe(
                "PrimordialGuardian",
                new ItemStack(ModBlocks.BlockImmortalizer),
                25,
                new AspectList()
                    .add(Aspect.UNDEAD, 25)
                    .add(Aspect.SOUL, 125)
                    .add(Aspect.ELDRITCH, 75)
                    .add(Aspect.LIFE, 250)
                    .add(Aspect.ENTROPY, 25)
                    .add(Aspect.ORDER, 25)
                    .add(CAELES, 250)
                    .add(Aspect.MAGIC, 100),
                new ItemStack(ModBlocks.PRIMALBLOCK),
                new Object[] {
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    new ItemStack(BlocksTC.metalBlockVoid),
                    new ItemStack(Item.getByNameOrId("thaumadditions:mithrillium_resonator")),
                    new ItemStack(BlocksTC.metalAlchemicalAdvanced),
                    new ItemStack(BlocksTC.metalBlockVoid),
                    new ItemStack(BlocksTC.metalAlchemicalAdvanced),
                    new ItemStack(Item.getByNameOrId("thaumadditions:mithrillium_resonator")),
                    new ItemStack(BlocksTC.metalBlockVoid),
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    new ItemStack(Item.getByNameOrId("thaumadditions:mithminite_block"))

                }
            ));

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:TAINT_AMULET"), new InfusionRecipe(
                "TAINT_AMULET",
                new ItemStack(ModItems.ItemTaintAmulet),
                9,
                new AspectList()
                    .add(Aspect.FLUX, 75)
                    .add(Aspect.ELDRITCH, 70)
                    .add(Aspect.MAGIC, 65)
                    .add(Aspect.PROTECT, 55),
                new ItemStack(ItemsTC.baubles,1,4),
                new Object[] {
                    new ItemStack(ModItems.orb_of_taint),
                    new ItemStack(ModItems.taint_slime),
                    new ItemStack(BlocksTC.metalBlockVoid),
                    new ItemStack(ModItems.taint_tendril),
                    new ItemStack(Items.NETHER_STAR),
                    new ItemStack(ModItems.taint_tendril),
                    new ItemStack(BlocksTC.metalBlockVoid),
                    new ItemStack(ModItems.taint_slime),
                }
            ));

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:ring_cooldown"), new InfusionRecipe(
                "NEWRUNICS",
                new ItemStack(ModItems.CdRing),
                7,
                new AspectList()
                    .add(Aspect.VOID, 45)
                    .add(Aspect.ORDER, 45)
                    .add(Aspect.ENERGY, 55)
                    .add(TIME, 250)
                    .add(Aspect.SENSES, 25),
                    
                new ItemStack(ModItems.IRONRING),
                new Object[] {
                    new ItemStack(BlocksTC.visBattery),
                    new ItemStack(ModItems.AQUAREIA_GEM),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(BlocksTC.matrixCost),
                    new ItemStack(ModItems.AQUAREIA_GEM),
                    new ItemStack(ItemsTC.quicksilver),
                    new ItemStack(BlocksTC.visBattery),
                    new ItemStack(ModItems.AQUAREIA_GEM),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(BlocksTC.matrixCost),
                    new ItemStack(ModItems.AQUAREIA_GEM),
                    new ItemStack(ItemsTC.quicksilver)
                }
            ));
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:ring_revive"), new InfusionRecipe(
                "NEWRUNICS",
                new ItemStack(ModItems.ReviveRing),
                7,
                new AspectList()
                    .add(Aspect.DESIRE, 70)
                    .add(Aspect.LIFE, 50)
                    .add(Aspect.MAGIC, 55)
                    .add(TIME, 250)
                    .add(Aspect.SOUL, 35),
                    
                new ItemStack(ModItems.RingIron),
                new Object[] {
                    new ItemStack(ItemsTC.morphicResonator),
                    new ItemStack(Items.TOTEM_OF_UNDYING),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(Blocks.GOLD_BLOCK),
                    new ItemStack(Items.TOTEM_OF_UNDYING),
                    new ItemStack(ItemsTC.amber),
                    new ItemStack(ItemsTC.morphicResonator),
                    new ItemStack(Items.TOTEM_OF_UNDYING),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(Blocks.GOLD_BLOCK),
                    new ItemStack(Items.TOTEM_OF_UNDYING),
                    new ItemStack(ItemsTC.amber)
                }
            ));

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:ring_regeneration"), new InfusionRecipe(
                "NEWRUNICS",
                new ItemStack(ModItems.RegenRing),
                5,
                new AspectList()
                    .add(Aspect.DESIRE, 30)
                    .add(Aspect.LIFE, 45)
                    .add(Aspect.MAN, 20)
                    .add(Aspect.PROTECT, 40)
                    .add(Aspect.BEAST, 30)
                    .add(Aspect.SOUL, 15),
                    
                new ItemStack(ModItems.IRONRING),
                new Object[] {
                    new ItemStack(ItemsTC.visResonator),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ModItems.ItemZombieHeart),
                    new ItemStack(ItemsTC.amber),
                    new ItemStack(ItemsTC.visResonator),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(Items.GOLDEN_APPLE),
                    new ItemStack(ItemsTC.nuggets,1,10)
                }
            ));
// // // / // / / / // / // /// // / //// / / / / // / / // / //

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:thaumium_core"), new InfusionRecipe(
                "IUALKIMIA",
                new ItemStack(ModItems.thaumium_core),
                4,
                new AspectList().add(Aspect.AIR, 85).add(Aspect.EARTH, 60).add(Aspect.METAL, 80).add(Aspect.ORDER, 75).add(Aspect.MAGIC, 110).add(Aspect.ENERGY, 90),
                new ItemStack(Item.getByNameOrId("industrialupgrade:itemcore"), 1, 0),
                new Object[] {
                    new ItemStack(ItemsTC.morphicResonator),
                    new ItemStack(Item.getByNameOrId("industrialupgrade:crafting_elements"), 1, 273),
                    new ItemStack(ItemsTC.visResonator),
                    new ItemStack(Item.getByNameOrId("industrialupgrade:itemingots"),1,5),
                    new ItemStack(ItemsTC.plate,1,2),
                    new ItemStack(ModItems.ItemThaumiumGear),
                    new ItemStack(ItemsTC.plate,1,2)     ,
                    new ItemStack(Item.getByNameOrId("industrialupgrade:itemingots"),1,5),
            
                }
            ));
            

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:void_core"), new InfusionRecipe(
                "IUALKIMIA",
                new ItemStack(ModItems.void_core),
                6,
                new AspectList().add(Aspect.FIRE, 90).add(Aspect.ORDER, 110).add(Aspect.VOID, 80).add(Aspect.METAL, 140).add(Aspect.ENERGY, 120),
                new ItemStack(ModItems.thaumium_core),
                new Object[] {
                    new ItemStack(ItemsTC.mirroredGlass),
                    new ItemStack(Item.getByNameOrId("industrialupgrade:crafting_elements"), 1, 273),
                    new ItemStack(ItemsTC.mirroredGlass),
                    new ItemStack(Item.getByNameOrId("industrialupgrade:photoniy_ingot")) ,
                    new ItemStack(ItemsTC.plate,1,3),
                    new ItemStack(ModItems.ItemVoidGear),
                    new ItemStack(ItemsTC.plate,1,3)     ,
                    new ItemStack(Item.getByNameOrId("industrialupgrade:photoniy_ingot"))
                }
            ));
            

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:advanced_node_stabilizer"), new InfusionRecipe(
                "BUFFSTABILIZER",
                new ItemStack(ModBlocks.buffnodeStabilizer),
                8,
                new AspectList().add(Aspect.AURA, 32).add(Aspect.MAGIC, 16).add(Aspect.ORDER, 16).add(Aspect.ENERGY, 16),
                new ItemStack(ModBlocks.nodeStabilizer),
                new Object[] {
                    new ItemStack(Blocks.REDSTONE_BLOCK),
                    new ItemStack(ItemsTC.alumentum),
                    new ItemStack(Blocks.REDSTONE_BLOCK),
                    "nitor",
                    new ItemStack(Blocks.REDSTONE_BLOCK),
                    new ItemStack(ItemsTC.alumentum),
                    new ItemStack(Blocks.REDSTONE_BLOCK),
                    "nitor",
                }
            ));

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:node_magnet"), new InfusionRecipe(
                "NODEMAGNET",
                new ItemStack(ModItems.itemNodeMagnet),
                6,
                new AspectList().add(Aspect.AURA, 200).add(Aspect.MECHANISM, 50).add(Aspect.MOTION, 100),
                new ItemStack(ItemsTC.morphicResonator),
                new Object[] {
                    new ItemStack(BlocksTC.crystalAir),
                    new ItemStack(BlocksTC.crystalFire),
                    new ItemStack(BlocksTC.crystalWater),
                    new ItemStack(BlocksTC.crystalEarth),
                    new ItemStack(BlocksTC.crystalOrder),
                    new ItemStack(BlocksTC.crystalEntropy),
                    new ItemStack(BlocksTC.crystalTaint),
                    new ItemStack(BlocksTC.plankGreatwood),
                    new ItemStack(ItemsTC.mind, 1, 1),
                    "blockIron",
                    "plateBrass",
                    new ItemStack(ItemsTC.mechanismSimple)
                }
            ));

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:arcane_workbench_wand_charger"), new InfusionRecipe(
                "WANDCHARGE",
                new ItemStack(ModBlocks.ARCANE_WORKBENCH_WAND_CHARGER),
                4,
                new AspectList().add(Aspect.AURA, 200).add(AspectRegistry.MATTERYA, 50),
                new ItemStack(BlocksTC.arcaneWorkbenchCharger),
                new Object[] {
                    new ItemStack(ModItems.GOLD_PLATE),
                    new ItemStack(ItemsTC.visResonator),
                    new ItemStack(ModItems.GOLD_PLATE),
                    new ItemStack(ModItems.AQUAREIA_GEM),
                    new ItemStack(ModItems.GOLD_PLATE),
                    new ItemStack(ItemsTC.visResonator),
                    new ItemStack(ModItems.GOLD_PLATE),
                    new ItemStack(ModItems.AQUAREIA_GEM),
                }
            ));

            registerAuraTotemRecipes();

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:FLUXSCRUBBER"), new InfusionRecipe(
                "FLUXSCRUBBER",
                new ItemStack(ModBlocks.FLUX_SCRAPER),
                2,
                new AspectList()
                    .add(Aspect.FLUX, 18)
                    .add(Aspect.AURA, 14)
                    .add(Aspect.ORDER, 12)
                    .add(Aspect.WATER, 8),
                new ItemStack(BlocksTC.stoneArcane),
                new Object[] {
                    new ItemStack(ModItems.AQUAREIA_GEM),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(ItemsTC.quicksilver),
                    new ItemStack(ItemsTC.mechanismSimple),
                    new ItemStack(ModItems.AQUAREIA_GEM),
                    new ItemStack(ItemsTC.salisMundus)
                }
            ));

            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:BIGJAR"), new InfusionRecipe(
                "NEWGOLEMANCY",
                new ItemStack(ModBlocks.BIG_JAR),
                5,
                new AspectList()
                    .add(Aspect.MIND, 60)
                    .add(Aspect.UNDEAD, 20)
                    .add(Aspect.ORDER, 25)
                    .add(Aspect.WATER, 40)
                    .add(Aspect.DARKNESS, 18),
                new ItemStack(BlocksTC.jarBrain),
                new Object[] {
                    new ItemStack(Blocks.GLASS),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(Blocks.GLASS),
                    new ItemStack(Items.WATER_BUCKET),
                    new ItemStack(Blocks.GLASS),
                    new ItemStack(ModItems.CLEAN_MIND),
                    new ItemStack(Blocks.GLASS),
                    new ItemStack(Items.WATER_BUCKET),
                    new ItemStack(Blocks.GLASS),
                    new ItemStack(ModItems.CLEAN_MIND),
                    new ItemStack(Blocks.GLASS),
                    new ItemStack(Items.WATER_BUCKET),
                    new ItemStack(Blocks.GLASS),
                    new ItemStack(ItemsTC.salisMundus),
                    new ItemStack(Blocks.GLASS),
                    new ItemStack(Items.BOOK)

                }
            ));
    }

    private static void registerAuraTotemRecipes() {
        ResourceLocation poleInner = new ResourceLocation("thaumicforever:PoleInner");
        ThaumcraftApi.addArcaneCraftingRecipe(poleInner, new ShapedArcaneRecipe(
            poleInner,
            "AURATOTEMS",
            50,
            new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 2).add(Aspect.ORDER, 1),
            auraTotem(3),
            "WGW",
            "WNW",
            "WGW",
            'N', ThaumcraftApiHelper.makeCrystal(Aspect.EARTH),
            'W', new ItemStack(BlocksTC.plankSilverwood),
            'G', new ItemStack(BlocksTC.plankGreatwood)
        ));

        ResourceLocation poleOuter = new ResourceLocation("thaumicforever:PoleOuter");
        ThaumcraftApi.addArcaneCraftingRecipe(poleOuter, new ShapedArcaneRecipe(
            poleOuter,
            "AURATOTEMS",
            50,
            new AspectList().add(Aspect.AIR, 2).add(Aspect.EARTH, 1).add(Aspect.ORDER, 2),
            auraTotem(2),
            "WGW",
            "WNW",
            "WGW",
            'N', ThaumcraftApiHelper.makeCrystal(Aspect.AIR),
            'W', new ItemStack(BlocksTC.plankSilverwood),
            'G', new ItemStack(BlocksTC.plankGreatwood)
        ));

        ResourceLocation poleStable = new ResourceLocation("thaumicforever:PoleStable");
        ThaumcraftApi.addArcaneCraftingRecipe(poleStable, new ShapedArcaneRecipe(
            poleStable,
            "AURATOTEMS",
            75,
            new AspectList().add(Aspect.AIR, 2).add(Aspect.EARTH, 2).add(Aspect.ORDER, 3),
            auraTotem(4),
            "WGW",
            "WNW",
            "WGW",
            'N', new ItemStack(ItemsTC.visResonator),
            'W', new ItemStack(BlocksTC.plankSilverwood),
            'G', new ItemStack(BlocksTC.plankGreatwood)
        ));

        ResourceLocation crystallizer = new ResourceLocation("thaumicforever:Crystallizer");
        ThaumcraftApi.addArcaneCraftingRecipe(crystallizer, new ShapedArcaneRecipe(
            crystallizer,
            "ESSENTIACRYSTALCONVERTER",
            75,
            new AspectList().add(Aspect.ORDER, 2).add(Aspect.EARTH, 2).add(Aspect.CRYSTAL, 3),
            new ItemStack(ModBlocks.CRYSTALLIZER),
            "SQS",
            "GCG",
            "SRS",
            'S', new ItemStack(BlocksTC.stoneArcane),
            'Q', new ItemStack(Items.QUARTZ),
            'G', new ItemStack(Blocks.GLASS),
            'C', new ItemStack(BlocksTC.crystalOrder),
            'R', new ItemStack(ItemsTC.visResonator)
        ));

        ResourceLocation voidChest = new ResourceLocation("thaumicforever:void_chest");
        ThaumcraftApi.addArcaneCraftingRecipe(voidChest, new ShapedArcaneRecipe(
            voidChest,
            "VOIDCHEST",
            100,
            new AspectList().add(Aspect.ENTROPY, 3).add(Aspect.ORDER, 2).add(Aspect.AIR, 1),
            new ItemStack(ModBlocks.VOID_CHEST),
            "VEV",
            "NCN",
            "VRV",
            'V', "ingotVoid",
            'E', new ItemStack(Items.ENDER_PEARL),
            'N', new ItemStack(Items.NAME_TAG),
            'C', new ItemStack(Blocks.ENDER_CHEST),
            'R', new ItemStack(ItemsTC.visResonator)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:TotemPush"), new InfusionRecipe(
            "AURATOTEMS",
            auraTotem(0),
            5,
            new AspectList()
                .add(Aspect.ENERGY, 25)
                .add(Aspect.AURA, 25)
                .add(Aspect.MOTION, 25)
                .add(Aspect.AVERSION, 25),
            new ItemStack(BlocksTC.plankSilverwood),
            new Object[] {
                new ItemStack(BlocksTC.plankGreatwood),
                new ItemStack(Blocks.CHEST),
                new ItemStack(ItemsTC.visResonator),
                new ItemStack(ItemsTC.salisMundus),
                new ItemStack(ItemsTC.quicksilver),
                new ItemStack(Blocks.PISTON),
                new ItemStack(ItemsTC.visResonator)
            }
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumicforever:TotemPull"), new InfusionRecipe(
            "AURATOTEMS",
            auraTotem(1),
            5,
            new AspectList()
                .add(Aspect.ENERGY, 25)
                .add(Aspect.AURA, 25)
                .add(Aspect.MOTION, 25)
                .add(Aspect.DESIRE, 25),
            new ItemStack(BlocksTC.plankSilverwood),
            new Object[] {
                new ItemStack(BlocksTC.plankGreatwood),
                new ItemStack(Blocks.CHEST),
                new ItemStack(ItemsTC.visResonator),
                new ItemStack(ItemsTC.salisMundus),
                new ItemStack(ItemsTC.amber),
                new ItemStack(Items.COMPASS),
                new ItemStack(ItemsTC.visResonator)
            }
        ));
    }

    private static ItemStack auraTotem(int meta) {
        if (meta == 2) return new ItemStack(ModBlocks.AURA_TOTEM_POLE, 1, 0);
        if (meta == 3) return new ItemStack(ModBlocks.AURA_TOTEM_POLE, 1, 1);
        if (meta == 4) return new ItemStack(ModBlocks.AURA_TOTEM_POLE, 1, 2);
        return new ItemStack(ModBlocks.AURA_TOTEM, 1, meta);
    }
}
