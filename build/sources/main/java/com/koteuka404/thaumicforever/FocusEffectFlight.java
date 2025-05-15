package com.koteuka404.thaumicforever;

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
        return Aspect.AIR;
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
            applyFlightEffect(targetEntity, powerLevel);

            playVisualEffect(targetEntity);
        }
        return true;
    }

    private void applyFlightEffect(EntityLivingBase entity, int powerLevel) {
        switch (powerLevel) {
            case 1:
                entity.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.LEVITATION, 200, -1, true, true));
                break;
            case 2:
                entity.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.LEVITATION, 200, 0, true, true));
                break;

            case 3:
                entity.addPotionEffect(new PotionEffect(PotionFlight.INSTANCE, 400, 0, true, true));
                break;

            case 4:
                entity.addPotionEffect(new PotionEffect(PotionFlight.INSTANCE, 800, 1, true, true));
                break;
        }
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
