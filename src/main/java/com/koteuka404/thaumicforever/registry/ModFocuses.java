package com.koteuka404.thaumicforever.registry;

import com.koteuka404.thaumicforever.ThaumicForever;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.casters.FocusEngine;
import com.koteuka404.thaumicforever.focus.FocusEffectBlink;
import com.koteuka404.thaumicforever.focus.FocusEffectCalm;
import com.koteuka404.thaumicforever.focus.FocusEffectChain;
import com.koteuka404.thaumicforever.focus.FocusEffectChainLightning;
import com.koteuka404.thaumicforever.focus.FocusEffectCleanse;
import com.koteuka404.thaumicforever.focus.FocusEffectExplosion;
import com.koteuka404.thaumicforever.focus.FocusEffectFlight;
import com.koteuka404.thaumicforever.focus.FocusEffectMindControl;
import com.koteuka404.thaumicforever.focus.FocusEffectPrimal;
import com.koteuka404.thaumicforever.focus.FocusEffectPrimalElement;
import com.koteuka404.thaumicforever.focus.FocusMediumOrbitSphere;
import com.koteuka404.thaumicforever.focus.FocusMediumSkyBeam;
import com.koteuka404.thaumicforever.focus.FocusModBoostPower;
import com.koteuka404.thaumicforever.focus.FocusModTimeCooldown;

public class ModFocuses {

    public static void registerFocuses() {
        FocusEngine.registerElement(FocusEffectCalm.class, new ResourceLocation("thaumicforever", "textures/foci/calm_focus.png"), 0xFF6347);
        FocusEngine.registerElement(FocusEffectCleanse.class, new ResourceLocation("thaumicforever", "textures/foci/cleanse_focus.png"), 0x00FFD9);
        FocusEngine.registerElement(FocusModBoostPower.class, new ResourceLocation("thaumicforever", "textures/foci/boost_power_focus.png"), 10);
        FocusEngine.registerElement(FocusEffectChain.class, new ResourceLocation("thaumicforever", "textures/foci/chain_focus.png"), 0x444444);
        FocusEngine.registerElement(FocusEffectMindControl.class, new ResourceLocation("thaumicforever", "textures/foci/mindcontrol.png"),0x66071f);
        FocusEngine.registerElement(FocusMediumSkyBeam.class, new ResourceLocation("thaumicforever", "textures/foci/beam.png"), 0xAEE6FF);
        // FocusEngine.registerElement(FocusMediumOrbitSphere.class, new ResourceLocation("thaumicforever", "textures/foci/seeker.png"), 0xFFFFFF);
        FocusEngine.registerElement(FocusEffectFlight.class, new ResourceLocation("thaumcraft", "textures/foci/quicken.png"),0xC0C0C0);
        FocusEngine.registerElement(FocusEffectChainLightning.class, new ResourceLocation("thaumicforever", "textures/foci/bolt2.png"), 0x00E5FF);
        FocusEngine.registerElement(FocusEffectExplosion.class, new ResourceLocation("thaumcraft", "textures/foci/burst.png"), 0xFF8A00);
        FocusEngine.registerElement(FocusEffectBlink.class, new ResourceLocation("thaumicforever", "textures/foci/blink.png"), 0x7C4DFF);
        FocusEngine.registerElement(FocusEffectPrimal.class, new ResourceLocation("thaumicforever", "textures/foci/primal.png"), 0x9C27B0);
        // FocusEngine.registerElement(FocusEffectPrimalElement.class, new ResourceLocation("thaumicforever", "textures/foci/pr_el.png"), 0x4FC3F7);
        FocusEngine.registerElement(FocusModTimeCooldown.class, new ResourceLocation("thaumicforever", "textures/foci/time_coldown.png"), 0x66CCFF);

    }
}
