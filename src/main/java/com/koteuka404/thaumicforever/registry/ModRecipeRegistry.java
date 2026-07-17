package com.koteuka404.thaumicforever.registry;

import com.koteuka404.thaumicforever.ThaumicForever;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraft.util.ResourceLocation;
import com.koteuka404.thaumicforever.registry.ModBlocks;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.koteuka404.thaumicforever.recipe.CustomSalisMundusRecipe;
import com.koteuka404.thaumicforever.recipe.RecipeRepairScribeToolLargeN;
import com.koteuka404.thaumicforever.recipe.LootBagRecipe;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class ModRecipeRegistry {
    @SubscribeEvent
    public static void onRegisterRecipes(final RegistryEvent.Register<IRecipe> event) {
        if (Loader.isModLoaded(ThaumicForever.MODID)) {
            event.getRegistry().register(new CustomSalisMundusRecipe());
        }

        for (int n = 1; n <= 8; n++) {
            event.getRegistry().register(new RecipeRepairScribeToolLargeN(n));
        }

        event.getRegistry().register(new LootBagRecipe());
        event.getRegistry().register(new ShapedOreRecipe(
            new ResourceLocation("thaumicforever", "nether_star_block"),
            new ItemStack(ModBlocks.NETHER_STAR_BLOCK),
            "NNN", "NNN", "NNN", 'N', new ItemStack(Items.NETHER_STAR)
        ).setRegistryName(new ResourceLocation("thaumicforever", "nether_star_block")));
    }
}
