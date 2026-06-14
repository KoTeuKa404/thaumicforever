package com.koteuka404.thaumicforever.event;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.koteuka404.thaumicforever.entity.EntityDecoyMannequin;
import com.mojang.authlib.GameProfile;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DecoyMannequinHandler {
    private static final String FAKE_PLAYER_PREFIX = "TFD";
    private static final double MOB_BAIT_RANGE = 24.0D;

    private final Map<Integer, List<FakePlayer>> activeFakePlayers = new HashMap<>();

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!(event.world instanceof WorldServer)) {
            return;
        }
        WorldServer world = (WorldServer) event.world;
        if (event.phase == TickEvent.Phase.START) {
            removeFakePlayers(world);
            addDecoyPlayers(world);
        } else {
            baitHostileMobs(world);
            removeFakePlayers(world);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() instanceof WorldServer) {
            removeFakePlayers((WorldServer) event.getWorld());
        }
    }

    private void addDecoyPlayers(WorldServer world) {
        List<EntityDecoyMannequin> decoys = world.getEntities(EntityDecoyMannequin.class, decoy -> decoy != null && !decoy.isDead);
        if (decoys.isEmpty()) {
            return;
        }

        List<FakePlayer> added = new ArrayList<>();
        for (EntityDecoyMannequin decoy : decoys) {
            FakePlayer fake = FakePlayerFactory.get(world, createProfile(world, decoy));
            prepareFakePlayer(world, decoy, fake);
            if (!world.playerEntities.contains(fake)) {
                world.playerEntities.add(fake);
                added.add(fake);
            }
        }
        if (!added.isEmpty()) {
            activeFakePlayers.put(world.provider.getDimension(), added);
        }
    }

    private void prepareFakePlayer(WorldServer world, EntityDecoyMannequin decoy, FakePlayer fake) {
        fake.dimension = world.provider.getDimension();
        fake.setWorld(world);
        fake.setPositionAndRotation(decoy.posX, decoy.posY, decoy.posZ, decoy.rotationYaw, 0.0F);
        fake.capabilities.disableDamage = true;
        fake.capabilities.isCreativeMode = false;
        fake.noClip = true;
        fake.setInvisible(true);
        if (fake.connection == null || !(fake.connection.getNetworkManager() instanceof SilentNetworkManager)) {
            fake.connection = new SilentNetHandler(world, fake);
        }
    }

    private void baitHostileMobs(WorldServer world) {
        List<EntityDecoyMannequin> decoys = world.getEntities(EntityDecoyMannequin.class, decoy -> decoy != null && !decoy.isDead);
        for (EntityDecoyMannequin decoy : decoys) {
            AxisAlignedBB area = decoy.getEntityBoundingBox().grow(MOB_BAIT_RANGE);
            List<EntityCreature> mobs = world.getEntitiesWithinAABB(EntityCreature.class, area, entity -> entity instanceof IMob && entity.isEntityAlive());
            for (EntityCreature mob : mobs) {
                EntityLivingBase currentTarget = mob.getAttackTarget();
                if (currentTarget == null || currentTarget.isDead || mob.getDistanceSq(decoy) < mob.getDistanceSq(currentTarget)) {
                    mob.setAttackTarget(decoy);
                    mob.getNavigator().tryMoveToEntityLiving(decoy, 1.0D);
                }
            }
        }
    }

    private void removeFakePlayers(WorldServer world) {
        List<FakePlayer> players = activeFakePlayers.remove(world.provider.getDimension());
        if (players != null) {
            world.playerEntities.removeAll(players);
        }
        for (Iterator<EntityPlayer> it = world.playerEntities.iterator(); it.hasNext();) {
            EntityPlayer player = it.next();
            if (player instanceof FakePlayer && player.getName().startsWith(FAKE_PLAYER_PREFIX)) {
                it.remove();
            }
        }
    }

    private GameProfile createProfile(WorldServer world, EntityDecoyMannequin decoy) {
        String name = FAKE_PLAYER_PREFIX + Integer.toHexString(decoy.getEntityId());
        String key = world.provider.getDimension() + ":" + decoy.getEntityId() + ":" + decoy.getUniqueID();
        UUID uuid = UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
        return new GameProfile(uuid, name);
    }

    private static class SilentNetHandler extends NetHandlerPlayServer {
        SilentNetHandler(WorldServer world, FakePlayer fake) {
            super(world.getMinecraftServer(), new SilentNetworkManager(), fake);
        }

        @Override
        public void sendPacket(Packet<?> packetIn) {
        }
    }

    private static class SilentNetworkManager extends NetworkManager {
        SilentNetworkManager() {
            super(EnumPacketDirection.CLIENTBOUND);
        }

        @Override
        public void sendPacket(Packet<?> packetIn) {
        }

        @Override
        @SafeVarargs
        public final void sendPacket(Packet<?> packetIn, GenericFutureListener<? extends Future<? super Void>> listener, GenericFutureListener<? extends Future<? super Void>>... listeners) {
        }

        @Override
        public boolean isChannelOpen() {
            return true;
        }

        @Override
        public boolean hasNoChannel() {
            return false;
        }
    }
}
