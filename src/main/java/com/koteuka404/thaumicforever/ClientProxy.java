package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));

    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event); 
        registerRenderers();
        if (event.getSide() == Side.CLIENT) { 
            OBJLoader.INSTANCE.addDomain("thaumicforever");
        }       
        MinecraftForge.EVENT_BUS.register(this); 
        if (ModItems.hand != null) {
            ModItems.hand.setTileEntityItemStackRenderer(new GorillaHandTileEntityItemStackRenderer());
        }
        // MinecraftForge.EVENT_BUS.register(AirCurrentManager.class);

    }

    

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event); 
        KeyBindings.register();
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler()); 
        if (ModItems.hand != null) {
            ModItems.hand.setTileEntityItemStackRenderer(new GorillaHandTileEntityItemStackRenderer());
        }         
        MinecraftForge.EVENT_BUS.register(new GuiTabHandler());

      
    
    }

    private void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityGuardianMannequin.class, manager -> new RenderGuardianMannequin(manager));
        RenderingRegistry.registerEntityRenderingHandler(AuraNodeEntity.class, AuraNodeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBottleClean.class, manager -> new RenderSnowball<>(manager, ModItems.ItemBottleClean, Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(EntityBottleVis.class, manager -> new RenderSnowball<>(manager, ModItems.ItemBottleVis, Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(EntityTimeFreezeProjectile.class, manager -> new RenderInvisibleSnowball(manager, Items.SNOWBALL, Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(ReviveSkeletonEntity.class, ReviveSkeletonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonAngry.class, RenderSkeletonAngry::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBVilager.class, RenderBVilager::new);
        RenderingRegistry.registerEntityRenderingHandler(CoinVillager.class, RenderCoinVillager::new);
        RenderingRegistry.registerEntityRenderingHandler(WizardVillager.class, RenderWizardVillager::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityGorilla.class, RenderGorilla::new);
        RenderingRegistry.registerEntityRenderingHandler(WatcherEntity.class, WatcherRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityWindCharge.class, RenderWindCharge::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityGorillaHand.class, RenderGorillaHand::new);
        // RenderingRegistry.registerEntityRenderingHandler(EntityAirCurrent.class, RenderAirCurrent::new);

    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemColorRegister(ColorHandlerEvent.Item event) {
        ItemColors itemColors = event.getItemColors();
        
        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 0 && stack.getItem() instanceof ItemFocus4) {
                return ((ItemFocus4) stack.getItem()).getFocusColor(stack);
            }
            return -1;
        }, ModItems.FOCUS_4);

        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 0 && stack.getItem() instanceof ItemFocusComplex) {
                return ((ItemFocusComplex) stack.getItem()).getFocusColor(stack);
            }
            return -1;
        }, ModItems.FOCUS_COMPLEX);
    }

   

    // @SubscribeEvent
    // @SideOnly(Side.CLIENT)
    // public void onModelBakeEvent(ModelBakeEvent event) {
    //     try {
    //         IModel model = OBJLoader.INSTANCE.loadModel(new ResourceLocation("thaumicforever:models/item/monkey_paw.obj"));
    //         event.getModelRegistry().putObject(
    //             new ModelResourceLocation("thaumicforever:monkey_paw", "inventory"),
    //             model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()))
    //         );
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }


    
    

}