// src/main/java/com/koteuka404/thaumicforever/ItemCompassMaze.java
package com.koteuka404.thaumicforever;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCompassMaze extends Item {
    private double rotation, rota;
    private long lastUpdateTick;
    private long lastSearchTick = -1;

    public ItemCompassMaze() {
        setMaxStackSize(1);

        addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
            @Override @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                if ((entity == null && !stack.isOnItemFrame())
                  || stack.getSubCompound("MazeCompass") == null) {
                    return 0F;
                }
                Entity ref = entity != null ? entity : stack.getItemFrame();
                if (ref == null) return 0F;
                if (world == null) world = ref.world;

                NBTTagCompound tag = stack.getSubCompound("MazeCompass");
                if (!tag.hasKey("TargetX")) return 0F;

                double tx = tag.getInteger("TargetX") + 0.5D;
                double tz = tag.getInteger("TargetZ") + 0.5D;
                double px = ref.posX;
                double pz = ref.posZ;

                double dx = tx - px;
                double dz = tz - pz;
                double theta = Math.atan2(dz, dx);
                double yawRad = Math.toRadians(ref.rotationYaw);

                double raw = (theta - yawRad - Math.PI / 2.0D) / (2.0D * Math.PI);
                raw = MathHelper.positiveModulo(raw, 1.0D);

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

                return (float) raw;
            }
        });
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (world.isRemote || !(entity instanceof EntityPlayer)) return;
        long t = world.getTotalWorldTime();
        if (t - lastSearchTick < 20) return;
        lastSearchTick = t;

        EntityPlayer player = (EntityPlayer) entity;
        MazeDungeonWrapper md = MazeDungeonWrapper.findClosest(world, player.getPosition());
        if (md != null) {
            BlockPos center = md.getCenter(); 
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) tag = new NBTTagCompound();
            NBTTagCompound maze = tag.getCompoundTag("MazeCompass");
            maze.setInteger("TargetX", center.getX());
            maze.setInteger("TargetY", center.getY());
            maze.setInteger("TargetZ", center.getZ());
            maze.setString("TargetName", md.getName());
            tag.setTag("MazeCompass", maze);
            stack.setTagCompound(tag);
            player.inventory.markDirty();
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            Biome current = world.getBiome(player.getPosition());

            if (!(current instanceof BiomeTaiga)) {
                player.sendMessage(new TextComponentString(
                    TextFormatting.YELLOW + "Yoe need find a Taiga biome first."
                ));
            } else {
                NBTTagCompound tag = stack.getSubCompound("MazeCompass");
                if (tag != null && tag.hasKey("TargetX")) {
                    int x = tag.getInteger("TargetX");
                    int y = tag.getInteger("TargetY");
                    int z = tag.getInteger("TargetZ");
                    String name = tag.getString("TargetName");
                    // player.sendMessage(new TextComponentString(
                    //     TextFormatting.GOLD + "[MazeCompass] " +
                    //     TextFormatting.WHITE + name +
                    //     TextFormatting.GRAY + " @ " +
                    //     TextFormatting.AQUA + x + ", " + y + ", " + z
                    // ));
                } else {
                    // player.sendMessage(new TextComponentString(
                    //     TextFormatting.RED + "[MazeCompass] No maze found nearby yet."
                    // ));
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
