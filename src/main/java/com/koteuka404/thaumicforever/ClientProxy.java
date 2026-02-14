package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.client.lib.events.TFHudHandler;

import java.lang.reflect.Method;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));

    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        OBJLoader.INSTANCE.addDomain(ThaumicForever.MODID);

        registerRenderers();

        MinecraftForge.EVENT_BUS.register(this);
        if (ModItems.hand != null) {
            ModItems.hand.setTileEntityItemStackRenderer(new GorillaHandTileEntityItemStackRenderer());
        }
        ModelLoader.setCustomModelResourceLocation(
        Item.getItemFromBlock(ModBlocks.Port),
        0,
        new ModelResourceLocation("thaumicforever:port", "inventory")
    );
    }

    
    

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        TFHudHandler.install();
        KeyBindings.register();
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        if (ModItems.hand != null) {
            ModItems.hand.setTileEntityItemStackRenderer(new GorillaHandTileEntityItemStackRenderer());
        }
        MinecraftForge.EVENT_BUS.register(new GuiTabHandler());
        // MinecraftForge.EVENT_BUS.register(new CustomHelmetRenderHandler());
		ClientRegistry.bindTileEntitySpecialRenderer(TileNodeStabilizer.class, new TileNodeStabilizerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileBuffNodeStabilizer.class, new TileBuffNodeStabilizerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileNodeTransducer.class, new TileNodeTransducerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityJarredNode.class, new RenderTileJarredNode());
        ClientRegistry.bindTileEntitySpecialRenderer(TilePort.class, new TilePortRenderer());


		RenderingRegistry.registerEntityRenderingHandler(EntityAuraNode.class, new RenderAuraNode(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityNodeMagnet.class, new RenderNodeMagnet(Minecraft.getMinecraft().getRenderManager()));
        ModBlocks.ITEMBLOCK_JARRED_NODE.setTileEntityItemStackRenderer(new JarredNodeItemRenderer());
        MinecraftForge.EVENT_BUS.register(new AquareiaGogglesRenderLayerHandler());


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

        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex != 1) return -1;
            if (!(stack.getItem() instanceof ItemCustomCaster)) return -1;

            ItemCustomCaster caster = (ItemCustomCaster) stack.getItem();
            ItemStack focusStack = caster.getFocusStack(stack);
            if (focusStack.isEmpty() || !(focusStack.getItem() instanceof ItemFocus)) return -1;

            ItemFocus focus = (ItemFocus) focusStack.getItem();
            try {
                Method m = ItemFocus.class.getMethod("getFocusColor", ItemStack.class);
                return (int) m.invoke(focus, focusStack);
            } catch (Exception e) {
                return -1;
            }
        }, ModItems.CUSTOM_CASTER);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        System.out.println("Setting renderer for jarred node: " + ModBlocks.ITEMBLOCK_JARRED_NODE);
        if (ModBlocks.ITEMBLOCK_JARRED_NODE != null) {
            ModBlocks.ITEMBLOCK_JARRED_NODE.setTileEntityItemStackRenderer(new JarredNodeItemRenderer());
        }
    }


}
