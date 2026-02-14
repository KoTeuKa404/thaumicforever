package com.koteuka404.thaumicforever;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class ModDimensions {
    public static final int VOID_DIMENSION_ID = 2505;
    public static DimensionType VOID_DIMENSION;

    public static void init(FMLPreInitializationEvent event) {
        VOID_DIMENSION = DimensionType.register("void_dimension", "_void", VOID_DIMENSION_ID, VoidWorldProvider.class, false);
        DimensionManager.registerDimension(VOID_DIMENSION_ID, VOID_DIMENSION);
    }
}
