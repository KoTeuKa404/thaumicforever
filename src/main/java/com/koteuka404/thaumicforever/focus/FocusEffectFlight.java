package com.koteuka404.thaumicforever.focus;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;
import com.koteuka404.thaumicforever.potion.PotionFlight;

public class FocusEffectFlight extends FocusEffect {

    private static final String SETTING_POWER = "power_level";

    public FocusEffectFlight() {
        this.initialize();
    }

    @Override
    public String getResearch() {
        return "FLIGHT";
    }

    @Override
    public String getKey() {
        return "thaumicforever.FLIGHT";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.FLIGHT;
    }

    @Override
    public int getComplexity() {
        return getSettingValue(SETTING_POWER) * 3;
    }

    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        if (target.entityHit instanceof EntityLivingBase) {
            EntityLivingBase targetEntity = (EntityLivingBase) target.entityHit;
            int powerLevel = this.getSettingValue(SETTING_POWER); 
            applyFlightEffect(targetEntity, powerLevel, finalPower);

            playVisualEffect(targetEntity);
        }
        return true;
    }

    private void applyFlightEffect(EntityLivingBase entity, int powerLevel, float finalPower) {
        int duration;
        switch (powerLevel) {
            case 1: duration = 200; break;
            case 2: duration = 300; break;
            case 3: duration = 400; break;
            case 4:
            default:
                duration = 800; break;
        }

        // finalPower includes focus modifiers (e.g. Double Power).
        // Apply diminishing returns so stacked x2 modifiers do not scale speed too hard.
        // Cap scaling to max effect of two x2 modifiers (finalPower up to 4.0).
        float fp = Math.min(4.0f, Math.max(1.0f, finalPower));
        float extra = fp - 1.0f;
        float diminished = extra / (1.0f + 0.75f * extra); // saturates with higher stacks

        int baseAmp = powerLevel >= 4 ? 1 : 0;
        int bonusAmp = Math.max(0, Math.round(diminished * 2.0f));
        int amplifier = Math.max(0, Math.min(3, baseAmp + bonusAmp));

        entity.addPotionEffect(new PotionEffect(PotionFlight.INSTANCE, duration, amplifier, true, true));
    }

    private void playVisualEffect(EntityLivingBase entity) {
        World world = entity.getEntityWorld();

        PacketHandler.INSTANCE.sendToAllAround(
                new PacketFXFocusPartImpact(entity.posX, entity.posY, entity.posZ, new String[]{getKey()}),
                new net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint(
                        world.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 64.0)
        );

        world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_ENDERDRAGON_FLAP, SoundCategory.PLAYERS, 0.3f, 1.0f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticleFX(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        FXDispatcher.GenPart pp = new FXDispatcher.GenPart();
        pp.grav = -0.05f;
        pp.age = 20 + world.rand.nextInt(10);
        pp.alpha = new float[]{0.8f, 0.0f};
        pp.grid = 32;
        pp.partStart = 337;
        pp.partInc = 1;
        pp.partNum = 5;
        pp.slowDown = 0.75;
        pp.rot = (float) world.rand.nextGaussian() / 2.0f;
        float s = (float) (2.0 + world.rand.nextGaussian() * 0.5);
        pp.scale = new float[]{s, s * 1.5f};
        FXDispatcher.INSTANCE.drawGenericParticles(posX, posY, posZ, motionX, motionY, motionZ, pp);
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[]{
                new NodeSetting(SETTING_POWER, "Power Level", new NodeSetting.NodeSettingIntRange(1, 4))
        };
    }
}
