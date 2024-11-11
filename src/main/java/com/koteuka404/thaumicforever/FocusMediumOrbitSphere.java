package com.koteuka404.thaumicforever;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;

public class FocusMediumOrbitSphere extends FocusMedium {

    @Override
    public String getResearch() {
        return "FOCUSORBSPHERE"; 
    }

    @Override
    public String getKey() {
        return "thaumicforever.ORBSPHERE"; 
    }

    @Override
    public Aspect getAspect() {
        return Aspect.AIR;
    }

    @Override
    public int getComplexity() {
        return 5 + getSettingValue("radius") * 2;
    }

    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TRAJECTORY  }; 
    }

    @Override
    public boolean execute(Trajectory trajectory) {
        Entity caster = getPackage().getCaster();
        if (caster instanceof EntityLivingBase) {
            EntityLivingBase player = (EntityLivingBase) caster;
            
            createOrbitSphere(player, getSettingValue("radius"));
            
            return true;
        }
        return false;
    }

    @Override
    public boolean hasIntermediary() {
        return true;
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
            new NodeSetting("radius", "focus.common.radius", new NodeSetting.NodeSettingIntRange(3, 5)) // Радіус обертання
        };
    }

    @Override
    public float getPowerMultiplier() {
        return 1.0f;
    }

    private void createOrbitSphere(EntityLivingBase player, int radius) {
        World world = player.world;
        Vec3d playerPos = player.getPositionVector();
        double angleStep = Math.PI / 10; // Кутовий крок для обертання сфери

        for (double theta = 0; theta < 2 * Math.PI; theta += angleStep) {
            for (double phi = 0; phi < Math.PI; phi += angleStep) {
                double x = playerPos.x + radius * Math.sin(phi) * Math.cos(theta);
                double y = playerPos.y + radius * Math.cos(phi);
                double z = playerPos.z + radius * Math.sin(phi) * Math.sin(theta);
                
                world.spawnParticle(EnumParticleTypes.SPELL_WITCH, x, y, z, 0, 0, 0);
                
                executeNextFocus(new Vec3d(x, y, z), player);
            }
        }
    }

    private void executeNextFocus(Vec3d position, EntityLivingBase player) {
        Trajectory trajectory = new Trajectory(position, new Vec3d(0, 0, 0));

        if (getRemainingPackage() != null) {
            FocusEngine.runFocusPackage(getRemainingPackage(), new Trajectory[] { trajectory }, new RayTraceResult[0]);
        }
    }

}
