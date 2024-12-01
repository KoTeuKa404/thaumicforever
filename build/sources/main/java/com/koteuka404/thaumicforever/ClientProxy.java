package com.koteuka404.thaumicforever;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.model.ModelLoader;
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

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event); 
    }

    private void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityGuardianMannequin.class, manager -> new RenderGuardianMannequin(manager));
        RenderingRegistry.registerEntityRenderingHandler(AuraNodeEntity.class, AuraNodeRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(EntityTimeFreezeProjectile.class, manager -> new RenderInvisibleSnowball(manager, Items.SNOWBALL, Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(ReviveSkeletonEntity.class, ReviveSkeletonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonAngry.class, RenderSkeletonAngry::new);

    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemColorRegister(ColorHandlerEvent.Item event) {
        ItemColors itemColors = event.getItemColors();
        
        itemColors.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex == 0 && stack.getItem() instanceof ItemFocus4) {
                    return ((ItemFocus4) stack.getItem()).getFocusColor(stack);
                }
                return -1;
            }
        }, ModItems.FOCUS_4);

        itemColors.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex == 0 && stack.getItem() instanceof ItemFocusComplex) {
                    return ((ItemFocusComplex) stack.getItem()).getFocusColor(stack);
                }
                return -1;
            }
        }, ModItems.FOCUS_COMPLEX);
    }
}
