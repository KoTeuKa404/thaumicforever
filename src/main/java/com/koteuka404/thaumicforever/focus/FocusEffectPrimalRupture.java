package com.koteuka404.thaumicforever.focus;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.FXDispatcher;

public class FocusEffectPrimalRupture extends FocusEffect {
    @Override
    public String getResearch() {
        return "PRIMAL_RUPTURE";
    }

    @Override
    public String getKey() {
        return "thaumicforever.PRIMAL_RUPTURE";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.MAGIC;
    }

    @Override
    public int getComplexity() {
        return 20 + getSettingValue("power") * 8;
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
                new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5))
        };
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        return (2.0F + getSettingValue("power") * 2.0F) * finalPower;
    }

    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        if (target == null || !(target.entityHit instanceof EntityLivingBase) || getPackage() == null) return false;
        EntityLivingBase victim = (EntityLivingBase) target.entityHit;
        EntityLivingBase caster = getPackage().getCaster();
        if (caster == null || victim.world.isRemote || victim == caster) return victim.world.isRemote;

        float damage = Math.max(0.0F, getDamageForDisplay(finalPower));
        if (damage <= 0.0F) return false;

        float healthBefore = victim.getHealth();
        float requiredHealth = Math.max(0.0F, healthBefore - damage);
        EntityDamageSource source = new EntityDamageSource("thaumicforever.primal_rupture", caster);
        source.setDamageBypassesArmor().setDamageIsAbsolute().setMagicDamage();

        victim.hurtResistantTime = 0;
        victim.attackEntityFrom(source, damage);

        // Energy shields and heavily modified armor often cancel LivingAttackEvent entirely.
        // Enforce the same health loss without depending on any particular armor mod API.
        if (victim.getHealth() > requiredHealth) {
            victim.setAbsorptionAmount(0.0F);
            victim.setHealth(requiredHealth);
            if (requiredHealth <= 0.0F && !victim.isDead) victim.onDeath(source);
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
        if (world == null) return;
        FXDispatcher.GenPart particle = new FXDispatcher.GenPart();
        particle.age = 10 + world.rand.nextInt(6);
        particle.alpha = new float[] {0.9F, 0.0F};
        particle.grid = 64;
        particle.partStart = 264;
        particle.partInc = 1;
        particle.partNum = 4;
        particle.scale = new float[] {0.55F, 1.1F};
        particle.redStart = particle.redEnd = 0.95F;
        particle.greenStart = particle.greenEnd = 0.85F;
        particle.blueStart = particle.blueEnd = 1.0F;
        FXDispatcher.INSTANCE.drawGenericParticles(x, y, z, vx, vy, vz, particle);
    }

    @Override
    public void onCast(Entity caster) {
    }
}
