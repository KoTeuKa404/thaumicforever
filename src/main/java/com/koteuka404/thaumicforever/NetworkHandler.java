package com.koteuka404.thaumicforever;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerPackets() {
        INSTANCE.registerMessage(
            PacketOpenMysticTab.Handler.class,
            PacketOpenMysticTab.class,
            4,
            Side.SERVER
        );
        INSTANCE.registerMessage(
            PacketOpenNormalInventory.Handler.class,
            PacketOpenNormalInventory.class,
            5,
            Side.SERVER
        );
    }
}
