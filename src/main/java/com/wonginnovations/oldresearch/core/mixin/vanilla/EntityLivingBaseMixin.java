package com.wonginnovations.oldresearch.core.mixin.vanilla;

import com.wonginnovations.oldresearch.common.OldResearchUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin {

    @Inject(method = "playEquipSound", at = @At("HEAD"), cancellable = true, remap = false)
    protected void playEquipSoundInjection(ItemStack stack, CallbackInfo ci) {
        if (OldResearchUtils.isThaumometer(stack)) {
            ci.cancel();
        }
    }

}
