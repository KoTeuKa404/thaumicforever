package com.koteuka404.thaumicforever.seal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigToggles.SealToggle;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.seals.SealUse;

public class SealUseAdvanced extends SealUse {
    private static final ResourceLocation ICON = new ResourceLocation("thaumicforever", "items/seals/seal_use_advanced");
    private static final int FILTER_SLOTS = 9;
    private static final int TOGGLE_REPEAT = 9;
    private static final int TOGGLE_REQUIRE_REDSTONE = 10;
    private static final int TOGGLE_ONLY_EMPTY_TARGET = 11;

    private static final Map<String, Boolean> ONE_SHOT_COMPLETED = new ConcurrentHashMap<>();

    public SealUseAdvanced() {
        props = new SealToggle[]{
                new SealToggle(true, "pmeta", "golem.prop.meta"),
                new SealToggle(true, "pnbt", "golem.prop.nbt"),
                new SealToggle(false, "pore", "golem.prop.ore"),
                new SealToggle(false, "pmod", "golem.prop.mod"),
                new SealToggle(false, "pleft", "golem.prop.left"),
                new SealToggle(false, "pempty", "golem.prop.empty"),
                new SealToggle(false, "pemptyhand", "golem.prop.emptyhand"),
                new SealToggle(false, "psneak", "golem.prop.sneak"),
                new SealToggle(false, "ppro", "golem.prop.provision.wl"),
                new SealToggle(true, "tf_repeat_use", "thaumicforever.golem.prop.repeat_use"),
                new SealToggle(false, "tf_require_redstone", "thaumicforever.golem.prop.require_redstone"),
                new SealToggle(false, "tf_only_empty_target", "thaumicforever.golem.prop.only_empty_target")
        };
    }

    @Override
    public String getKey() {
        return "thaumicforever:use_advanced";
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ICON;
    }

    @Override
    public int getFilterSize() {
        return FILTER_SLOTS;
    }

    @Override
    public void tickSeal(World world, ISealEntity seal) {
        if (world.isRemote || seal == null || seal.getSealPos() == null) {
            return;
        }

        String key = makeKey(world, seal.getSealPos().pos, seal.getSealPos().face);
        enforceDerivedToggles();
        if (props[TOGGLE_REQUIRE_REDSTONE].getValue() && !isSealPowered(world, seal.getSealPos().pos, seal.getSealPos().face)) {
            return;
        }
        if (props[TOGGLE_ONLY_EMPTY_TARGET].getValue() && !isTargetReplaceable(world, seal.getSealPos().pos, seal.getSealPos().face)) {
            return;
        }

        if (props[TOGGLE_REPEAT].getValue()) {
            ONE_SHOT_COMPLETED.remove(key);
        } else if (ONE_SHOT_COMPLETED.containsKey(key)) {
            return;
        }

        super.tickSeal(world, seal);
    }

    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        enforceDerivedToggles();
        if (!world.isRemote
                && task != null
                && task.getSealPos() != null
                && props[TOGGLE_ONLY_EMPTY_TARGET].getValue()
                && !isTargetReplaceable(world, task.getSealPos().pos, task.getSealPos().face)) {
            return false;
        }

        boolean done = super.onTaskCompletion(world, golem, task);
        if (world.isRemote || task == null || task.getSealPos() == null || !done) {
            return done;
        }

        String key = makeKey(world, task.getSealPos().pos, task.getSealPos().face);
        if (!props[TOGGLE_REPEAT].getValue()) {
            ONE_SHOT_COMPLETED.put(key, Boolean.TRUE);
        }
        return done;
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        enforceDerivedToggles();
        if (golem != null
                && task != null
                && task.getSealPos() != null
                && props[TOGGLE_ONLY_EMPTY_TARGET].getValue()
                && !isTargetReplaceable(golem.getGolemWorld(), task.getSealPos().pos, task.getSealPos().face)) {
            return false;
        }
        return super.canGolemPerformTask(golem, task);
    }

    @Override
    public void onRemoval(World world, BlockPos pos, EnumFacing side) {
        if (!world.isRemote) {
            String key = makeKey(world, pos, side);
            ONE_SHOT_COMPLETED.remove(key);
        }
        super.onRemoval(world, pos, side);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        enforceDerivedToggles();
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.DEFT, EnumGolemTrait.SMART};
    }

    private void enforceDerivedToggles() {
        if (props != null && props.length > 4) {
            props[4].setValue(false);
        }
    }

    private static String makeKey(World world, BlockPos pos, EnumFacing side) {
        return world.provider.getDimension() + ":" + pos.toLong() + ":" + side.ordinal();
    }

    private boolean isSealPowered(World world, BlockPos pos, EnumFacing side) {
        return world.isBlockPowered(pos) || world.isBlockPowered(pos.offset(side));
    }

    private boolean isTargetReplaceable(World world, BlockPos pos, EnumFacing side) {
        BlockPos target = pos.offset(side);
        return world.isAirBlock(target) || world.getBlockState(target).getBlock().isReplaceable(world, target);
    }
}
