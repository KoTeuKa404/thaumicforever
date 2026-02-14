package com.koteuka404.thaumicforever;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.casters.FocusEngine;

public class ModFocuses {

    public static void registerFocuses() {
        FocusEngine.registerElement(FocusEffectCalm.class, new ResourceLocation("thaumicforever", "textures/foci/calm_focus.png"), 0xFF6347);
        FocusEngine.registerElement(FocusEffectCleanse.class, new ResourceLocation("thaumicforever", "textures/foci/cleanse_focus.png"), 0x00FFD9);
        FocusEngine.registerElement(FocusModBoostPower.class, new ResourceLocation("thaumicforever", "textures/foci/boost_power_focus.png"), 10);
        FocusEngine.registerElement(FocusEffectChain.class, new ResourceLocation("thaumicforever", "textures/foci/chain_focus.png"), 0x444444);
        FocusEngine.registerElement(FocusEffectMindControl.class, new ResourceLocation("thaumicforever", "textures/foci/mindcontrol.png"),0x66071f);
        // FocusEngine.registerElement(FocusMediumSkyBeam.class, new ResourceLocation("thaumicforever", "textures/foci/beam_focus.png"), 0xFFDD44);
        // FocusEngine.registerElement(FocusMediumOrbitSphere.class, new ResourceLocation("thaumicforever", "textures/foci/seeker.png"), 0xFFFFFF);
        FocusEngine.registerElement(FocusEffectFlight.class, new ResourceLocation("thaumcraft", "textures/foci/quicken.png"),0xC0C0C0);
        FocusEngine.registerElement(FocusEffectChainLightning.class, new ResourceLocation("thaumicforever", "textures/foci/bolt2.png"), 0x00E5FF);
        FocusEngine.registerElement(FocusEffectExplosion.class, new ResourceLocation("thaumcraft", "textures/foci/burst.png"), 0xFF8A00);

    }
}
