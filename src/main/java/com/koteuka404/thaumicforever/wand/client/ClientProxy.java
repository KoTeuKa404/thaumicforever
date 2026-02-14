package com.koteuka404.thaumicforever.wand.client;

import com.koteuka404.thaumicforever.wand.client.render.entity.EntityVisOrbRenderer;
import com.koteuka404.thaumicforever.wand.client.render.entity.ItemWandRenderer;
import com.koteuka404.thaumicforever.wand.client.render.tile.TileArcaneWorktableRenderer;
import com.koteuka404.thaumicforever.wand.entity.EntityVisOrb;
import com.koteuka404.thaumicforever.wand.item.TW_Items;
import com.koteuka404.thaumicforever.wand.main.CommonProxy;
import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.wand.tile.TileArcaneWorkbenchNew;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = ThaumicForever.MODID, value = Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        TW_Items.itemWand.setTileEntityItemStackRenderer(new ItemWandRenderer());
        TW_Items.itemStaff.setTileEntityItemStackRenderer(new ItemWandRenderer());
        TW_Items.itemScepter.setTileEntityItemStackRenderer(new ItemWandRenderer());
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        ClientRegistry.bindTileEntitySpecialRenderer(TileArcaneWorkbenchNew.class, new TileArcaneWorktableRenderer());

        RenderingRegistry.registerEntityRenderingHandler(EntityVisOrb.class, new EntityVisOrbRenderer(Minecraft.getMinecraft().getRenderManager()));
    }

}
