package com.wonginnovations.oldresearch.core.mixin;

import net.minecraft.launchwrapper.Launch;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;
import java.util.Set;

public class OldResearchMixinPlugin implements IMixinConfigPlugin {
    private boolean isCleanroom;

    @Override
    public void onLoad(String mixinPackage) {
        isCleanroom = hasResource("top/outlands/foundation/boot/Foundation.class")
                || hasResource("com/cleanroommc/cleanroom/Cleanroom.class");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!isCleanroom) {
            return true;
        }

        return !mixinClassName.endsWith("ItemThaumometerMixin")
                && !mixinClassName.endsWith("RenderEventHandlerMixin");
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

    private static boolean hasResource(String path) {
        return Launch.classLoader.getResource(path) != null;
    }
}
