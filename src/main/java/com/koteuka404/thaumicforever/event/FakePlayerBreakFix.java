package com.koteuka404.thaumicforever.event;

import com.koteuka404.thaumicforever.ThaumicForever;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
        if (!isThaumcraftGolemFakePlayer(harv)) return;
        if (!(evt.getWorld() instanceof WorldServer)) return;

        BlockPos pos = evt.getPos();
        IBlockState state = evt.getState();
        if (state == null || state.getBlock() == Blocks.AIR) return;
        WorldServer ws = (WorldServer)evt.getWorld();

        ws.setBlockToAir(pos);
        ws.playEvent(2001, pos, Block.getStateId(state));
        ws.notifyBlockUpdate(pos, state, Blocks.AIR.getDefaultState(), 3);
        ws.getChunkFromBlockCoords(pos).markDirty();
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed evt) {
        EntityPlayer player = evt.getEntityPlayer();
        if (!isThaumcraftGolemFakePlayer(player)) return;
        if (evt.getState() == null || evt.getState().getBlock() == Blocks.AIR) return;
        if (evt.getNewSpeed() > 0.0F) return;

        // After dimension/chunk reload some fake players can get zero break speed.
        // Clamp to a small positive speed so golem tasks do not deadlock.
        float original = evt.getOriginalSpeed();
        evt.setNewSpeed(original > 0.0F ? original : 0.2F);
    }

    private static boolean isThaumcraftGolemFakePlayer(EntityPlayer player) {
        if (!(player instanceof FakePlayer)) return false;
        String name = player.getName() == null ? "" : player.getName().toLowerCase(Locale.ROOT);
        String cls = player.getClass().getName().toLowerCase(Locale.ROOT);
        return name.contains("thaumcraftgolem")
                || (name.contains("thaumcraft") && name.contains("golem"))
                || (cls.contains("thaumcraft") && cls.contains("golem"))
                || (cls.contains("fake") && cls.contains("golem"));
    }
}
