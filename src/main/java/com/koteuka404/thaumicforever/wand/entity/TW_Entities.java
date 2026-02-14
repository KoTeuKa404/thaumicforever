package com.koteuka404.thaumicforever.wand.entity;

import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.wand.main.ThaumicWands;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class TW_Entities {

    public static void registerEntities(Register<EntityEntry> r) {
        EntityRegistry.registerModEntity(new ResourceLocation(ThaumicWands.modID, "VisOrb"), EntityVisOrb.class, "visOrb", ThaumicForever.nextEntityId(), ThaumicForever.instance, 120, 20, true);
    }

}
