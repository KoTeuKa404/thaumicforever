package com.tutorialmod.turtywurty.mixins;

 
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import thaumcraft.common.world.aura.AuraThread;

@Mixin(AuraThread.class)
public class MixinRestoreAuraGeneration {
    
    // Відновлюємо стандартну генерацію аури, використовуючи оригінальний метод Math.min
    @Redirect(method = "processAuraChunk", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(FF)F", ordinal = 2), remap = false)
    private float restoreAuraRegen(float v, float v1) {
        // Відновлюємо оригінальну поведінку
        return Math.min(v, v1);
    }
}
