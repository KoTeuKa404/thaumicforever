package com.koteuka404.thaumicforever.proxy;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import com.koteuka404.thaumicforever.registry.ModItems;

import com.koteuka404.thaumicforever.ThaumicForever;

import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFlowerPot;
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
import thaumcraft.client.lib.events.TFHudHandler;
import thaumcraft.common.items.casters.ItemFocus;
import com.koteuka404.thaumicforever.client.gui.AddendumToastHandler;
import com.koteuka404.thaumicforever.client.gui.GuiTabHandler;
import com.koteuka404.thaumicforever.client.input.KeyBindings;
import com.koteuka404.thaumicforever.client.input.KeyInputHandler;
import com.koteuka404.thaumicforever.client.render.AuraNodeRenderer;
import com.koteuka404.thaumicforever.client.render.RenderAuraNode;
import com.koteuka404.thaumicforever.client.render.RenderBVilager;
import com.koteuka404.thaumicforever.client.render.RenderCoinVillager;
import com.koteuka404.thaumicforever.client.render.RenderDecoyMannequin;
import com.koteuka404.thaumicforever.client.render.RenderGorilla;
import com.koteuka404.thaumicforever.client.render.RenderGorillaHand;
import com.koteuka404.thaumicforever.client.render.RenderGuardianMannequin;
import com.koteuka404.thaumicforever.client.render.RenderInvisibleSnowball;
import com.koteuka404.thaumicforever.client.render.RenderNodeMagnet;
import com.koteuka404.thaumicforever.client.render.RenderResonanceBolt;
import com.koteuka404.thaumicforever.client.render.RenderSkeletonAngry;
import com.koteuka404.thaumicforever.client.render.RenderTileBigJar;
import com.koteuka404.thaumicforever.client.render.RenderTileCustomFlowerPot;
import com.koteuka404.thaumicforever.client.render.RenderTileJarredNode;
import com.koteuka404.thaumicforever.client.render.RenderVampireBat;
import com.koteuka404.thaumicforever.client.render.RenderVanillaFlowerPotExtras;
import com.koteuka404.thaumicforever.client.render.RenderTileVoidChest;
import com.koteuka404.thaumicforever.client.render.RenderWindCharge;
import com.koteuka404.thaumicforever.client.render.RenderWizardVillager;
import com.koteuka404.thaumicforever.client.render.ReviveSkeletonRenderer;
import com.koteuka404.thaumicforever.client.render.SkyBeamClientRenderer;
import com.koteuka404.thaumicforever.client.render.TileBuffNodeStabilizerRenderer;
import com.koteuka404.thaumicforever.client.render.TileNodeStabilizerRenderer;
import com.koteuka404.thaumicforever.client.render.TileNodeTransducerRenderer;
import com.koteuka404.thaumicforever.client.render.TilePortRenderer;
import com.koteuka404.thaumicforever.client.render.TileRechargePedestalRenderer;
import com.koteuka404.thaumicforever.client.render.WatcherRenderer;
import com.koteuka404.thaumicforever.client.render.item.BigJarItemRenderer;
import com.koteuka404.thaumicforever.client.render.item.GorillaHandTileEntityItemStackRenderer;
import com.koteuka404.thaumicforever.client.render.item.JarredNodeItemRenderer;
import com.koteuka404.thaumicforever.entity.AuraNodeEntity;
import com.koteuka404.thaumicforever.entity.CoinVillager;
import com.koteuka404.thaumicforever.entity.EntityAuraNode;
import com.koteuka404.thaumicforever.entity.EntityBVilager;
import com.koteuka404.thaumicforever.entity.EntityBottleClean;
import com.koteuka404.thaumicforever.entity.EntityBottleVis;
import com.koteuka404.thaumicforever.entity.EntityDecoyMannequin;
import com.koteuka404.thaumicforever.entity.EntityGorilla;
import com.koteuka404.thaumicforever.entity.EntityGorillaHand;
import com.koteuka404.thaumicforever.entity.EntityGuardianMannequin;
import com.koteuka404.thaumicforever.entity.EntityNodeMagnet;
import com.koteuka404.thaumicforever.entity.EntityResonanceBolt;
import com.koteuka404.thaumicforever.entity.EntitySkeletonAngry;
import com.koteuka404.thaumicforever.entity.EntityTimeFreezeProjectile;
import com.koteuka404.thaumicforever.entity.EntityVampireBat;
import com.koteuka404.thaumicforever.entity.EntityWindCharge;
import com.koteuka404.thaumicforever.entity.ReviveSkeletonEntity;
import com.koteuka404.thaumicforever.entity.WatcherEntity;
import com.koteuka404.thaumicforever.entity.WizardVillager;
import com.koteuka404.thaumicforever.item.ItemBottleClean;
import com.koteuka404.thaumicforever.item.ItemBottleVis;
import com.koteuka404.thaumicforever.item.ItemCustomCaster;
import com.koteuka404.thaumicforever.item.ItemFocus4;
import com.koteuka404.thaumicforever.item.ItemFocusComplex;
import com.koteuka404.thaumicforever.tile.TileBigJar;
import com.koteuka404.thaumicforever.tile.TileBuffNodeStabilizer;
import com.koteuka404.thaumicforever.tile.TileCustomFlowerPot;
import com.koteuka404.thaumicforever.tile.TileEntityJarredNode;
import com.koteuka404.thaumicforever.tile.TileNodeStabilizer;
import com.koteuka404.thaumicforever.tile.TileNodeTransducer;
import com.koteuka404.thaumicforever.tile.TilePort;
import com.koteuka404.thaumicforever.tile.TileRechargePedestal;
import com.koteuka404.thaumicforever.tile.TileVoidChest;

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
        MinecraftForge.EVENT_BUS.register(new AddendumToastHandler());
        MinecraftForge.EVENT_BUS.register(new SkyBeamClientRenderer());
        // MinecraftForge.EVENT_BUS.register(new CustomHelmetRenderHandler());
        ClientRegistry.bindTileEntitySpecialRenderer(TileNodeStabilizer.class, new TileNodeStabilizerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileBuffNodeStabilizer.class, new TileBuffNodeStabilizerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileNodeTransducer.class, new TileNodeTransducerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityJarredNode.class, new RenderTileJarredNode());
        ClientRegistry.bindTileEntitySpecialRenderer(TilePort.class, new TilePortRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileBigJar.class, new RenderTileBigJar());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCustomFlowerPot.class, new RenderTileCustomFlowerPot());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlowerPot.class, new RenderVanillaFlowerPotExtras());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRechargePedestal.class, new TileRechargePedestalRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileVoidChest.class, new RenderTileVoidChest());


		RenderingRegistry.registerEntityRenderingHandler(EntityAuraNode.class, new RenderAuraNode(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityNodeMagnet.class, new RenderNodeMagnet(Minecraft.getMinecraft().getRenderManager()));
        ModBlocks.ITEMBLOCK_JARRED_NODE.setTileEntityItemStackRenderer(new JarredNodeItemRenderer());
    }

    private void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityGuardianMannequin.class, manager -> new RenderGuardianMannequin(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntityDecoyMannequin.class, manager -> new RenderDecoyMannequin(manager));
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
        RenderingRegistry.registerEntityRenderingHandler(EntityVampireBat.class, RenderVampireBat::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityWindCharge.class, RenderWindCharge::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityGorillaHand.class, RenderGorillaHand::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityResonanceBolt.class, RenderResonanceBolt::new);

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
        if (ModBlocks.BIG_JAR_ITEM != null) {
            ModBlocks.BIG_JAR_ITEM.setTileEntityItemStackRenderer(new BigJarItemRenderer());
        }
    }


}
