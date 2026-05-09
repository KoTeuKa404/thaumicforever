package com.koteuka404.thaumicforever.compat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import com.koteuka404.thaumicforever.ThaumicForever;

public final class BaublesCompat {
    private static volatile Constructor<?> packetSyncCtor;
    private static volatile int packetSyncCtorType = -1;
    private static volatile Object packetHandlerInstance;
    private static volatile Method packetSendToMethod;
    private static volatile boolean packetSyncMissingLogged;
    private static volatile boolean packetSyncCreateFailedLogged;
    private static volatile boolean packetHandlerMissingLogged;
    private static volatile boolean packetHandlerSendFailedLogged;

    private BaublesCompat() {}

    public static void sendSlotSync(EntityPlayerMP owner, int slot, ItemStack stack, EntityPlayerMP receiver) {
        IMessage msg = createPacketSync(owner, slot, stack);
        if (msg == null) {
            return;
        }
        if (!ensurePacketHandler()) {
            if (!packetHandlerMissingLogged) {
                packetHandlerMissingLogged = true;
                ThaumicForever.LOGGER.warn("Baubles PacketHandler not found. Slot sync send is disabled for this Baubles variant.");
            }
            return;
        }
        try {
            packetSendToMethod.invoke(packetHandlerInstance, msg, receiver);
        } catch (Throwable ignored) {
            if (!packetHandlerSendFailedLogged) {
                packetHandlerSendFailedLogged = true;
                ThaumicForever.LOGGER.warn("Failed to send Baubles slot sync packet. Sync is disabled for this Baubles variant.");
            }
        }
    }

    public static IMessage createPacketSync(EntityPlayerMP owner, int slot, ItemStack stack) {
        try {
            Constructor<?> ctor = packetSyncCtor;
            int type = packetSyncCtorType;
            if (ctor == null) {
                discoverPacketSyncCtor();
                ctor = packetSyncCtor;
                type = packetSyncCtorType;
            }
            if (ctor == null) {
                if (!packetSyncMissingLogged) {
                    packetSyncMissingLogged = true;
                    ThaumicForever.LOGGER.warn("Baubles PacketSync constructor not found. Slot sync is disabled for this Baubles variant.");
                }
                return null;
            }

            Object obj;
            switch (type) {
                case 0:
                    obj = ctor.newInstance(owner, slot, stack);
                    break;
                case 1:
                    obj = ctor.newInstance(owner.getEntityId(), slot, stack);
                    break;
                case 2:
                    obj = ctor.newInstance(slot, stack);
                    break;
                default:
                    return null;
            }
            return (obj instanceof IMessage) ? (IMessage) obj : null;
        } catch (Throwable ignored) {
            if (!packetSyncCreateFailedLogged) {
                packetSyncCreateFailedLogged = true;
                ThaumicForever.LOGGER.warn("Failed to construct Baubles PacketSync message. Slot sync is disabled for this Baubles variant.");
            }
            return null;
        }
    }

    private static synchronized void discoverPacketSyncCtor() {
        if (packetSyncCtor != null) {
            return;
        }
        try {
            Class<?> packetSyncCls = Class.forName("baubles.common.network.PacketSync");

            for (Constructor<?> c : packetSyncCls.getConstructors()) {
                Class<?>[] p = c.getParameterTypes();
                if (p.length == 3
                        && net.minecraft.entity.player.EntityPlayer.class.isAssignableFrom(p[0])
                        && p[1] == int.class
                        && ItemStack.class.isAssignableFrom(p[2])) {
                    c.setAccessible(true);
                    packetSyncCtor = c;
                    packetSyncCtorType = 0;
                    return;
                }
            }

            for (Constructor<?> c : packetSyncCls.getConstructors()) {
                Class<?>[] p = c.getParameterTypes();
                if (p.length == 3
                        && p[0] == int.class
                        && p[1] == int.class
                        && ItemStack.class.isAssignableFrom(p[2])) {
                    c.setAccessible(true);
                    packetSyncCtor = c;
                    packetSyncCtorType = 1;
                    return;
                }
            }

            for (Constructor<?> c : packetSyncCls.getConstructors()) {
                Class<?>[] p = c.getParameterTypes();
                if (p.length == 2
                        && p[0] == int.class
                        && ItemStack.class.isAssignableFrom(p[1])) {
                    c.setAccessible(true);
                    packetSyncCtor = c;
                    packetSyncCtorType = 2;
                    return;
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static synchronized boolean ensurePacketHandler() {
        if (packetHandlerInstance != null && packetSendToMethod != null) {
            return true;
        }
        try {
            Class<?> cls = Class.forName("baubles.common.network.PacketHandler");
            Field instanceField = cls.getField("INSTANCE");
            Object instance = instanceField.get(null);
            if (instance == null) {
                return false;
            }
            Method sendTo = cls.getMethod("sendTo", IMessage.class, EntityPlayerMP.class);
            packetHandlerInstance = instance;
            packetSendToMethod = sendTo;
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
