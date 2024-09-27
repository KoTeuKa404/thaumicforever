package com.tutorialmod.turtywurty;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event); // Виклик основної ініціалізації

        // Реєструємо рендер для моба GuardianMannequin
        RenderingRegistry.registerEntityRenderingHandler(EntityGuardianMannequin.class, manager -> new RenderGuardianMannequin(manager));
        
        // Реєструємо рендеринг Baubles (амулетів, кілець і т.д.)
        // MinecraftForge.EVENT_BUS.register(new BaublesRenderHandler());
    }
}
