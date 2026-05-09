package com.example.coremod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"com.example.coremod"})
public class CoreModPlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        List<String> transformers = new ArrayList<String>();
        transformers.add("com.example.coremod.IUEventHandlerTransformer");
        transformers.add("com.example.coremod.ThaumcraftLogisticsScrollTransformer");
        transformers.add("com.example.coremod.ThaumcraftLogisticsSearchTransformer");
        transformers.add("com.example.coremod.ThaumcraftLogisticsQueueTransformer");
        transformers.add("com.example.coremod.ThaumcraftLogisticsRequestLifetimeTransformer");
        transformers.add("com.example.coremod.ThaumcraftSealProvideTransformer");
        transformers.add("com.example.coremod.GolemRangedKitingTransformer");
        // transformers.add("com.example.coremod.ThaumicPeripheryTransformer");
        // transformers.add("com.example.coremod.BaubleAttributeModifierHandlerPatcher");
        // transformers.add("com.example.coremod.BaublesLogSuppressTransformer");

        boolean isBaublesEx = isClassResourcePresent("baubles/api/BaubleTypeEx.class");
        boolean isBubbles = isClassResourcePresent("baubles/api/BaubleTypeImpl.class");
        if (isBaublesEx) {
            System.out.println("[CoreModPlugin] BaublesEX detected - skipping Baubles transformers.");
        } else if (isBubbles) {
            System.out.println("[CoreModPlugin] Bubbles detected - skipping Baubles transformers.");
        } else {
            transformers.add("com.example.coremod.BaublesClassSwapTransformer");
            transformers.add("com.example.coremod.TransformerBaubles");
        }

        return transformers.toArray(new String[0]);
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        System.out.println("[CoreModPlugin] data injected");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    private static boolean isClassResourcePresent(String classResourcePath) {
        ClassLoader cl = CoreModPlugin.class.getClassLoader();
        return cl != null && cl.getResource(classResourcePath) != null;
    }
}
