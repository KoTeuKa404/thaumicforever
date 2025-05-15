package com.koteuka404.thaumicforever;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCompassMaze extends Item {
    private double rotation, rota;
    private long lastUpdateTick;

    public ItemCompassMaze() {
        setMaxStackSize(1);

        addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {

                if (entity == null && !stack.isOnItemFrame()) return 0.0F;
                Entity ref = entity != null ? entity : stack.getItemFrame();
                if (ref == null) return 0.0F;
                if (world == null) world = ref.world;

                NBTTagCompound tag = stack.getSubCompound("MazeCompass");
                if (tag == null || !tag.hasKey("TargetX")) return 0.0F;
                BlockPos target = new BlockPos(
                    tag.getInteger("TargetX"),
                    tag.getInteger("TargetY"),
                    tag.getInteger("TargetZ")
                );

                double raw;
                if (ref instanceof EntityPlayer) {
                    double dx = target.getX() + 0.5D - ref.posX;
                    double dz = target.getZ() + 0.5D - ref.posZ;
                    double theta = Math.atan2(dz, dx);
                    raw = MathHelper.positiveModulo(
                        (180.0D - ref.rotationYaw + Math.toDegrees(theta)) / 360.0D,
                        1.0D
                    );
                } else {
                    raw = (((EntityItemFrame)ref)
                            .getHorizontalFacing().getHorizontalIndex() % 4) / 4.0D;
                }

                if (entity != null) {
                    if (world.getTotalWorldTime() != lastUpdateTick) {
                        lastUpdateTick = world.getTotalWorldTime();
                        double delta = raw - rotation;
                        delta = MathHelper.positiveModulo(delta + 0.5D, 1.0D) - 0.5D;
                        rota = (rota + delta * 0.1D) * 0.8D;
                        rotation = MathHelper.positiveModulo(rotation + rota, 1.0D);
                    }
                    raw = rotation;
                }

                float angle = (float) raw;

                System.out.println("[MazeCompass] angle = " + angle);

                EntityPlayerSP client = Minecraft.getMinecraft().player;
                if (client != null) {
                    client.sendMessage(
                        new TextComponentString("[MazeCompass] angle = " + angle)
                    );
                }

                return angle;
            }
        });
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (world.isRemote) {
        } else {
        }

        if (!(entity instanceof EntityPlayer)) return;
        BlockPos pos = entity.getPosition();
        MazeDungeonWrapper md = MazeDungeonWrapper.findClosest(world, pos);
        if (md != null) {
            BlockPos c = md.getCenter();
            NBTTagCompound tag = stack.getOrCreateSubCompound("MazeCompass");
            tag.setInteger("TargetX", c.getX());
            tag.setInteger("TargetY", c.getY());
            tag.setInteger("TargetZ", c.getZ());
            tag.setString("TargetName", md.getName());
            System.out.println("[MazeCompass] set target to " + c);
        }
    }
}
