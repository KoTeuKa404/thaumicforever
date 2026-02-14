package com.koteuka404.thaumicforever;

import java.util.Map;
import java.util.WeakHashMap;

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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ItemCompassMaze extends Item {

    private static final Map<Entity, Wobble> WOBBLES = new WeakHashMap<>();

    private long lastSearchTick = -1L;

    public ItemCompassMaze() {
        setMaxStackSize(1);

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
                @Override
                public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                    if ((entity == null && !stack.isOnItemFrame())) return 0F;

                    final Entity ref = (entity != null) ? entity : stack.getItemFrame();
                    if (ref == null) return 0F;
                    if (world == null) world = ref.world;

                    NBTTagCompound root = stack.getTagCompound();
                    if (root == null) return 0F;
                    NBTTagCompound tag = root.getCompoundTag("MazeCompass");
                    if (!tag.hasKey("TargetX")) return 0F;

                    double tx = tag.getInteger("TargetX") + 0.5D;
                    double tz = tag.getInteger("TargetZ") + 0.5D;

                    double dx = tx - ref.posX;
                    double dz = tz - ref.posZ;
                    double theta = Math.atan2(dz, dx);
                    double yawRad = Math.toRadians(ref.rotationYaw);

                    double raw = (theta - yawRad - Math.PI / 2.0D) / (2.0D * Math.PI);
                    raw = MathHelper.positiveModulo(raw, 1.0D);

                    Wobble w = WOBBLES.computeIfAbsent(ref, r -> new Wobble());
                    return (float) w.wobble(world.getTotalWorldTime(), raw);
                }
            });
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (world.isRemote || !(entity instanceof EntityPlayer)) return;

        long t = world.getTotalWorldTime();
        if (t - lastSearchTick < 20) return;
        lastSearchTick = t;

        NBTTagCompound root = stack.getTagCompound();
        if (root == null) {
            root = new NBTTagCompound();
            stack.setTagCompound(root);
        }
        NBTTagCompound maze = root.getCompoundTag("MazeCompass");

        EntityPlayer player = (EntityPlayer) entity;
        MazeDungeonWrapper md = MazeDungeonWrapper.findClosest(world, player.getPosition());
        if (md != null) {
            BlockPos center = md.getCenter();
            maze.setInteger("TargetX", center.getX());
            maze.setInteger("TargetY", center.getY());
            maze.setInteger("TargetZ", center.getZ());
            maze.setString("TargetName", md.getName());
            root.setTag("MazeCompass", maze);
            stack.setTagCompound(root);
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
                    TextFormatting.YELLOW + "You need to find a Taiga biome first."
                ));
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private static final class Wobble {
        private double rotation = 0.0; // [0,1)
        private double rota = 0.0;
        private long lastTick = Long.MIN_VALUE;

        double wobble(long tick, double raw) {
            if (tick != lastTick) {
                lastTick = tick;
                double delta = MathHelper.positiveModulo(raw - rotation + 0.5D, 1.0D) - 0.5D;
                rota += delta * 0.1D;
                rota *= 0.8D;
                rotation = MathHelper.positiveModulo(rotation + rota, 1.0D);
            }
            return rotation;
        }
    }
}
