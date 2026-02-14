package com.koteuka404.thaumicforever;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;

public class FocusEffectExplosion extends FocusEffect {
    public static final int EXPLOSION_PARTICLE_GRID = 64;
    public static final int EXPLOSION_PARTICLE_COL = 7;
    public static final int EXPLOSION_PARTICLE_ROW = 9;
    public static final int EXPLOSION_PARTICLE_FRAMES = 9;
    public static final int EXPLOSION_TRAIL_COUNT = 10;
    public static final int EXPLOSION_BURST_COUNT = 30;

    @Override
    public String getResearch() {
        return "EXPLOSIONCAST";
    }

    @Override
    public String getKey() {
        return "thaumicforever.EXPLOSIONCAST";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.FIRE;
    }

    @Override
    public int getComplexity() {
        int power = getSettingValue("power"); // 1..5
        return 4 + power * 2;
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
            new NodeSetting("power", "focus.common.power",
                new NodeSetting.NodeSettingIntRange(1, 5))
        };
    }

    private float getBaseStrengthForPower() {
        int power = getSettingValue("power");
        switch (power) {
            case 1:
                return 2.0F;
            case 2:
                return 2.5F;
            case 3:
                return 3.0F;
            case 4:
                return 4.0F;
            case 5:
            default:
                return 6.0F;
        }
    }

    private float getExplosionStrength(float finalPower) {
        return getBaseStrengthForPower() * finalPower;
    }


    @Override
    public float getDamageForDisplay(float finalPower) {
        return getExplosionStrength(finalPower) * 2.0F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticleFX(World world, double x, double y, double z,
                                double dx, double dy, double dz) {
        if (world == null || !world.isRemote) return;

        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if (mc == null || mc.gameSettings == null) return;

        int setting = mc.gameSettings.particleSetting;
        if (setting >= 2) return;

        int count = EXPLOSION_TRAIL_COUNT;
        if (setting == 1) count = Math.max(1, count / 2);

        double spread = 0.06;
        int startIndex = EXPLOSION_PARTICLE_ROW * EXPLOSION_PARTICLE_GRID + EXPLOSION_PARTICLE_COL;

        for (int i = 0; i < count; i++) {
            double mx = (world.rand.nextDouble() - 0.5) * spread;
            double my = (world.rand.nextDouble() - 0.2) * spread;
            double mz = (world.rand.nextDouble() - 0.5) * spread;
            FXExplosionWisp fx = new FXExplosionWisp(
                world, x, y, z, mx, my, mz,
                EXPLOSION_PARTICLE_GRID, startIndex, EXPLOSION_PARTICLE_FRAMES
            );
            mc.effectRenderer.addEffect(fx);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void spawnExplosionBurst(World world, double x, double y, double z) {
        if (world == null || !world.isRemote) return;

        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if (mc == null || mc.gameSettings == null) return;

        int setting = mc.gameSettings.particleSetting;
        if (setting >= 2) return;

        int count = EXPLOSION_BURST_COUNT;
        if (setting == 1) count = Math.max(1, count / 2);

        double spread = 0.18;
        int startIndex = EXPLOSION_PARTICLE_ROW * EXPLOSION_PARTICLE_GRID + EXPLOSION_PARTICLE_COL;

        for (int i = 0; i < count; i++) {
            double mx = (world.rand.nextDouble() - 0.5) * spread;
            double my = (world.rand.nextDouble() - 0.1) * spread;
            double mz = (world.rand.nextDouble() - 0.5) * spread;
            FXExplosionWisp fx = new FXExplosionWisp(
                world, x, y, z, mx, my, mz,
                EXPLOSION_PARTICLE_GRID, startIndex, EXPLOSION_PARTICLE_FRAMES
            );
            mc.effectRenderer.addEffect(fx);
        }
    }

    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory,
                        float finalPower, int num) {
        if (target == null) return false;

        World world = getPackage().getCaster().world;
        if (world == null) return false;

        double x, y, z;

        if (target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit != null) {
            Entity e = target.entityHit;
            x = e.posX;
            y = e.posY + e.height * 0.5;
            z = e.posZ;
        } else {
            x = target.hitVec.x;
            y = target.hitVec.y;
            z = target.hitVec.z;
        }

        int power = getSettingValue("power");
        float strength = getExplosionStrength(finalPower);

        if (world.isRemote) {
            return true;
        }

        if (power <= 2) {
            Explosion explosion = new Explosion(world, null, x, y, z, strength, false, false);
            explosion.doExplosionA();
            explosion.doExplosionB(false);
        } else if (power == 3) {
            softBlocksExplosion(world, x, y, z, strength);
        } else if (power == 4) {
            Explosion explosion = new Explosion(world, null, x, y, z, strength, false, true);
            explosion.doExplosionA();
            explosion.doExplosionB(false);
        } else {
            Explosion explosion = new Explosion(world, null, x, y, z, strength, false, true);
            explosion.doExplosionA();
            explosion.doExplosionB(false);
        }

        ThaumicForever.network.sendToAllAround(
            new PacketExplosionFX(x, y, z),
            new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, 48)
        );

        return true;
    }


    private void softBlocksExplosion(World world, double x, double y, double z, float strength) {
        Explosion explosion = new Explosion(world, null, x, y, z, strength, false, true);
        explosion.doExplosionA();
        explosion.doExplosionB(false);
        for (BlockPos pos : explosion.getAffectedBlockPositions()) {
            if (!world.isBlockLoaded(pos)) continue;

            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (state.getMaterial().isReplaceable()) {
                continue;
            }

            float hardness = state.getBlockHardness(world, pos);
            if (hardness < 0) {
                continue;
            }

            if (hardness <= 2.0F) {
                world.destroyBlock(pos, true);
            }
        }
    }

    @Override
    public void onCast(Entity caster) {
    }
}
