package com.koteuka404.thaumicforever;

import net.minecraft.util.ResourceLocation;
import thaumcraft.common.golems.seals.SealProvide;

public class SealProviderAdvanced extends SealProvide {

    @Override
    public String getKey() {
        return "thaumicforever:provider_advanced";
    }

    @Override
    public ResourceLocation getSealIcon() {
        return new ResourceLocation("thaumicforever", "items/seals/seal_provider_advanced");
    }
}
