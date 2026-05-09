package com.koteuka404.thaumicforever.focus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.FXDispatcher;

public class FocusEffectPrimalElement extends FocusEffect {
    private static final String MOD_TA = "thaumicaugmentation";

    @Override
    public String getResearch() {
        return "FOCUSPRIMAL_ELEMENT";
    }

    @Override
    public String getKey() {
        return "thaumicforever.PRIMAL_ELEMENT";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.MAGIC;
    }

    @Override
    public int getComplexity() {
        int fire = getEffectComplexity(resolveThaumcraftEffect("fire", "ignis"), 4);
        int frost = getEffectComplexity(resolveThaumcraftEffect("frost", "ice", "cold", "gelum"), 4);
        int earth = getEffectComplexity(resolveThaumcraftEffect("earth", "terra"), 4);
        int sum = fire + frost + earth;
        return Math.max(1, Math.round(sum * 0.7f));
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        return 3.0f * finalPower;
    }

    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        if (target == null || target.entityHit == null) return false;

        String tcFire = resolveThaumcraftEffect("fire", "ignis");
        String tcFrost = resolveThaumcraftEffect("frost", "ice", "cold", "gelum");
        String tcEarth = resolveThaumcraftEffect("earth", "terra");
        String taWater = null;
        if (Loader.isModLoaded(MOD_TA)) {
            taWater = resolveTAWaterEffect();
            if (taWater == null) {
                taWater = resolveThaumcraftEffect("water", "aqua", "hydro");
            }
        }

        boolean didAny = false;
        if (taWater != null) {
            didAny |= executeChildEffect(taWater, target, trajectory, finalPower, num, true);
        }
        didAny |= executeChildEffect(tcFire, target, trajectory, finalPower, num, false);
        didAny |= executeChildEffect(tcFrost, target, trajectory, finalPower, num, false);
        didAny |= executeChildEffect(tcEarth, target, trajectory, finalPower, num, false);
        return didAny;
    }

    private boolean executeChildEffect(String key, RayTraceResult target, Trajectory trajectory, float finalPower, int num, boolean forcePowerOne) {
        if (key == null || key.equalsIgnoreCase(getKey())) return false;
        IFocusElement el = FocusEngine.getElement(key);
        if (!(el instanceof FocusEffect)) return false;

        FocusEffect effect = (FocusEffect) el;
        effect.setPackage(getPackage());

        if (forcePowerOne && effect instanceof FocusNode) {
            NodeSetting power = ((FocusNode) effect).getSetting("power");
            if (power != null) {
                power.setValue(1);
            }
        }

        EntityLivingBase caster = getPackage() != null ? getPackage().getCaster() : null;
        if (caster != null) {
            effect.onCast(caster);
        }

        return effect.execute(target, trajectory, finalPower, num);
    }

    private int getEffectComplexity(String key, int fallback) {
        if (key == null) return fallback;
        IFocusElement el = FocusEngine.getElement(key);
        if (el instanceof FocusNode) {
            return Math.max(1, ((FocusNode) el).getComplexity());
        }
        return fallback;
    }

    private String resolveTAWaterEffect() {
        return resolveEffectByTokens(MOD_TA, new String[] {"water", "aqua", "hydro"}, new String[] {"cloud", "ward", "heal"});
    }

    private String resolveThaumcraftEffect(String... tokens) {
        return resolveEffectByTokens("thaumcraft", tokens, new String[] {"cloud", "ward", "heal"});
    }

    private String resolveEffectByTokens(String namespace, String[] includeTokens, String[] excludeTokens) {
        List<String> keys = new ArrayList<>(FocusEngine.elements.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            if (key == null) continue;
            String lk = key.toLowerCase(Locale.ROOT);
            if (!lk.startsWith(namespace.toLowerCase(Locale.ROOT) + ".")) continue;

            boolean hasInclude = false;
            for (String t : includeTokens) {
                if (lk.contains(t.toLowerCase(Locale.ROOT))) {
                    hasInclude = true;
                    break;
                }
            }
            if (!hasInclude) continue;

            boolean hasExclude = false;
            if (excludeTokens != null) {
                for (String t : excludeTokens) {
                    if (lk.contains(t.toLowerCase(Locale.ROOT))) {
                        hasExclude = true;
                        break;
                    }
                }
            }
            if (hasExclude) continue;

            IFocusElement el = FocusEngine.getElement(key);
            if (el instanceof FocusEffect && !key.equalsIgnoreCase(getKey())) {
                return key;
            }
        }
        return null;
    }

    @Override
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
        if (world == null) return;
        FXDispatcher.INSTANCE.drawWispyMotes(x, y, z, vx, vy, vz, 16, 0.55f, 0.85f, 1.0f, 0.7f);
    }
}
