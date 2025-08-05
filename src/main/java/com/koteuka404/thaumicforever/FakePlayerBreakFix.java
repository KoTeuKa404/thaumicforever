package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class FakePlayerBreakFix {

    @Mod.EventHandler
    public static void init(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(FakePlayerBreakFix.class);
    }

    @SubscribeEvent
    public static void onHarvestDrops(HarvestDropsEvent evt) {
        if (evt.getWorld().isRemote) return;
        EntityPlayer harv = evt.getHarvester();
        if (!(harv instanceof FakePlayer)) return;
        if (!"FakeThaumcraftGolem".equals(harv.getName())) return;

        BlockPos pos = evt.getPos();
        IBlockState state = evt.getState();
        WorldServer ws = (WorldServer)evt.getWorld();

        ws.setBlockToAir(pos);
        ws.playEvent(2001, pos, Block.getStateId(state));
        ws.getChunkFromBlockCoords(pos).markDirty();
    }
}
