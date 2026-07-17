package com.koteuka404.thaumicforever.focus;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.FXDispatcher;

public class FocusEffectPrimalElement extends FocusEffect {
    private static final String MOD_TA = "thaumicaugmentation";
    private static final String TA_WATER = "focus.thaumicaugmentation.water";
    private static final String[] BASE_EFFECTS = {
            "thaumcraft.AIR",
            "thaumcraft.EARTH",
            "thaumcraft.FIRE",
            "thaumcraft.FROST"
    };

    @Override
    public String getResearch() {
        return "PRIMAL_ELEMENT";
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
        return 16 + getSettingValue("power") * 6;
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
                new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5))
        };
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        int power = getSettingValue("power");
        int count = BASE_EFFECTS.length + (hasWaterEffect() ? 1 : 0);
        return count * (1.5F + power) * finalPower;
    }

    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        if (target == null || getPackage() == null || getPackage().getCaster() == null) return false;

        boolean executed = false;
        for (String key : getEffectKeys()) {
            IFocusElement element = FocusEngine.getElement(key);
            if (!(element instanceof FocusEffect)) continue;

            FocusEffect effect = (FocusEffect) element;
            effect.initialize();
            copySetting(effect, "power", getSettingValue("power"));
            copySetting(effect, "duration", 1);
            effect.setPackage(getPackage());
            effect.onCast(getPackage().getCaster());
            executed |= effect.execute(target, trajectory, finalPower, num);
        }
        return executed;
    }

    private List<String> getEffectKeys() {
        List<String> keys = new ArrayList<>();
        if (hasWaterEffect()) keys.add(TA_WATER);
        for (String key : BASE_EFFECTS) keys.add(key);
        return keys;
    }

    private boolean hasWaterEffect() {
        return Loader.isModLoaded(MOD_TA) && FocusEngine.elements.containsKey(TA_WATER);
    }

    private static void copySetting(FocusEffect effect, String key, int value) {
        NodeSetting setting = effect.getSetting(key);
        if (setting != null) setting.setValue(value);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
        if (world == null) return;
        int[] colors = {0x70D6FF, 0x9B7653, 0xFF5A36, 0xBDEBFF, 0x406CFF};
        int color = colors[world.rand.nextInt(hasWaterEffect() ? colors.length : colors.length - 1)];
        FXDispatcher.GenPart particle = new FXDispatcher.GenPart();
        particle.age = 12 + world.rand.nextInt(8);
        particle.alpha = new float[] {0.8F, 0.0F};
        particle.grid = 64;
        particle.partStart = 264;
        particle.partInc = 1;
        particle.partNum = 4;
        particle.scale = new float[] {0.7F, 1.0F};
        particle.redStart = particle.redEnd = ((color >> 16) & 255) / 255.0F;
        particle.greenStart = particle.greenEnd = ((color >> 8) & 255) / 255.0F;
        particle.blueStart = particle.blueEnd = (color & 255) / 255.0F;
        FXDispatcher.INSTANCE.drawGenericParticles(x, y, z, vx, vy, vz, particle);
    }

    @Override
    public void onCast(Entity caster) {
    }
}
