package com.koteuka404.thaumicforever.wand.util;

import com.koteuka404.thaumicforever.EntityAuraNode;
import com.koteuka404.thaumicforever.wand.api.ThaumicWandsAPI;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;
import com.koteuka404.thaumicforever.wand.item.ItemScepter;
import com.koteuka404.thaumicforever.wand.item.ItemStaff;
import com.koteuka404.thaumicforever.wand.item.ItemWand;
import com.koteuka404.thaumicforever.wand.item.TW_Items;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.casters.CasterManager;

public class WandHelper {

    public static final String NBT_PRIMAL_CHARGE = "tf.primal_charge";

    public static int getActualVisCost(int vis, ItemStack wand, EntityPlayer player) {
        return (int) (vis * getTotalDiscount(wand, player));
    }

    public static float getTotalDiscount(ItemStack wand, EntityPlayer player) {
        float discount = getWandDiscount(wand);
        return discount - CasterManager.getTotalVisDiscount(player);
    }

    public static float getWandDiscount(ItemStack wand) {
        if (wand.getItem() instanceof ItemScepter)
            return ((ItemWand) TW_Items.itemWand).getCap(wand).getDiscount() - 0.10F;
        return ((ItemWand) TW_Items.itemWand).getCap(wand).getDiscount() + (wand.getItem() instanceof ItemStaff ? -0.05F : 0);
    }

    public static AspectList getActualCrystals(AspectList list, ItemStack wand) {
        if (list == null)
            return null;
        if (!(wand.getItem() instanceof IWand))
            return null;

        AspectList l = new AspectList();
        AspectList subtract = ((IWand) wand.getItem()).getCap(wand).getAspectDiscount();
        for (Aspect a : list.getAspects()) {
            if (list.getAmount(a) - subtract.getAmount(a) > 0)
                l.add(a, list.getAmount(a) - subtract.getAmount(a));
        }

        return l;
    }

    public static ItemStack getWandWithParts(String rod, String cap) {
        NBTTagCompound nbt = new NBTTagCompound();
        if (ThaumicWandsAPI.getWandRod(rod) == null)
            rod = "wood";
        if (ThaumicWandsAPI.getWandCap(cap) == null)
            cap = "iron";

        nbt.setString("rod", rod);
        nbt.setString("cap", cap);

        ItemStack is = new ItemStack(TW_Items.itemWand);
        is.setTagCompound(nbt);
        return is;
    }

    public static ItemStack getScepterWithParts(String rod, String cap) {
        NBTTagCompound nbt = new NBTTagCompound();
        if (ThaumicWandsAPI.getWandRod(rod) == null)
            rod = "silverwood";
        if (ThaumicWandsAPI.getWandCap(cap) == null)
            cap = "thaumium";

        nbt.setString("rod", rod);
        nbt.setString("cap", cap);

        ItemStack is = new ItemStack(TW_Items.itemScepter);
        is.setTagCompound(nbt);
        return is;
    }

    public static ItemStack getStaffWithParts(String core, String cap) {
        NBTTagCompound nbt = new NBTTagCompound();
        if (ThaumicWandsAPI.getStaffCore(core) == null)
            core = "greatwood";
        if (ThaumicWandsAPI.getWandCap(cap) == null)
            cap = "iron";

        nbt.setString("rod", core);
        nbt.setString("cap", cap);

        ItemStack is = new ItemStack(TW_Items.itemStaff);
        is.setTagCompound(nbt);
        return is;
    }

    public static AspectList getPrimalCharge(ItemStack wand) {
        AspectList out = new AspectList();
        if (wand == null || wand.isEmpty() || !wand.hasTagCompound()) return out;
        NBTTagCompound tag = wand.getTagCompound();
        if (tag.hasKey(NBT_PRIMAL_CHARGE, Constants.NBT.TAG_COMPOUND)) {
            out.readFromNBT(tag.getCompoundTag(NBT_PRIMAL_CHARGE));
        }
        return clampPrimals(out, getMaxPrimalCharge(wand, null));
    }

    public static void setPrimalCharge(ItemStack wand, AspectList charge, @Nullable EntityLivingBase player) {
        if (wand == null || wand.isEmpty()) return;
        AspectList clean = clampPrimals(filterPrimals(charge), getMaxPrimalCharge(wand, player));
        NBTTagCompound tag = getOrCreateTag(wand);
        NBTTagCompound data = new NBTTagCompound();
        clean.writeToNBT(data);
        tag.setTag(NBT_PRIMAL_CHARGE, data);
    }

    public static boolean isWandFullyCharged(ItemStack wand, @Nullable EntityLivingBase player) {
        int cap = getMaxPrimalCharge(wand, player);
        if (cap <= 0) return true;
        AspectList cur = getPrimalCharge(wand);
        for (Aspect p : Aspect.getPrimalAspects()) {
            if (cur.getAmount(p) < cap) return false;
        }
        return true;
    }

    public static float getPrimalChargePercentage(ItemStack wand, @Nullable EntityLivingBase player) {
        int cap = getMaxPrimalCharge(wand, player);
        if (cap <= 0) return 1.0f;
        AspectList cur = getPrimalCharge(wand);
        float min = 1.0f;
        for (Aspect p : Aspect.getPrimalAspects()) {
            float pct = cur.getAmount(p) / (float) cap;
            if (pct < min) min = pct;
        }
        return min;
    }

    public static void fillPrimalCharge(ItemStack wand, @Nullable EntityLivingBase player) {
        int cap = getMaxPrimalCharge(wand, player);
        if (cap <= 0) return;
        AspectList filled = new AspectList();
        for (Aspect p : Aspect.getPrimalAspects()) {
            filled.add(p, cap);
        }
        setPrimalCharge(wand, filled, player);
    }

    public static boolean addPrimalCharge(ItemStack wand, AspectList add, @Nullable EntityLivingBase player) {
        if (wand == null || wand.isEmpty()) return false;
        int cap = getMaxPrimalCharge(wand, player);
        if (cap <= 0) return false;

        AspectList current = getPrimalCharge(wand);
        AspectList primals = filterPrimals(add);
        boolean changed = false;

        for (Aspect p : primals.getAspects()) {
            int want = primals.getAmount(p);
            if (want <= 0) continue;
            int have = current.getAmount(p);
            int addAmt = Math.min(want, cap - have);
            if (addAmt > 0) {
                current.add(p, addAmt);
                changed = true;
            }
        }

        if (changed) {
            setPrimalCharge(wand, current, player);
            if (player instanceof EntityPlayer) {
                ((EntityPlayer) player).inventory.markDirty();
            }
        }
        return changed;
    }

    public static int addPrimalChargeDistributed(ItemStack wand, int amount, @Nullable EntityLivingBase player) {
        if (amount <= 0 || wand == null || wand.isEmpty()) return 0;
        int cap = getMaxPrimalCharge(wand, player);
        if (cap <= 0) return 0;

        AspectList current = getPrimalCharge(wand);
        int added = 0;
        for (int i = 0; i < amount; i++) {
            Aspect next = findLowestPrimalWithRoom(current, cap);
            if (next == null) break;
            current.add(next, 1);
            added++;
        }
        if (added > 0) {
            setPrimalCharge(wand, current, player);
            if (player instanceof EntityPlayer) {
                ((EntityPlayer) player).inventory.markDirty();
            }
        }
        return added;
    }

    public static boolean consumePrimalCharge(ItemStack wand, AspectList aspects, @Nullable EntityLivingBase player, boolean simulate) {
        if (wand == null || wand.isEmpty()) return false;
        if (aspects == null || aspects.size() <= 0) return true;

        AspectList cost = decomposeToPrimals(aspects);
        if (cost.size() <= 0) return true;

        AspectList current = getPrimalCharge(wand);
        for (Aspect p : cost.getAspects()) {
            int need = cost.getAmount(p);
            if (need > 0 && current.getAmount(p) < need) return false;
        }
        if (simulate) return true;

        for (Aspect p : cost.getAspects()) {
            int need = cost.getAmount(p);
            if (need > 0) current.reduce(p, need);
        }
        setPrimalCharge(wand, current, player);
        if (player instanceof EntityPlayer) {
            ((EntityPlayer) player).inventory.markDirty();
        }
        return true;
    }

    public static boolean consumePrimalSet(ItemStack wand, int amount, @Nullable EntityLivingBase player, boolean simulate) {
        if (amount <= 0) return true;
        AspectList cost = new AspectList();
        for (Aspect p : Aspect.getPrimalAspects()) {
            cost.add(p, amount);
        }
        return consumePrimalCharge(wand, cost, player, simulate);
    }

    public static AspectList decomposeToPrimals(AspectList aspects) {
        AspectList out = new AspectList();
        if (aspects == null) return out;
        for (Aspect a : aspects.getAspects()) {
            int amt = aspects.getAmount(a);
            if (amt > 0) addAspectToPrimals(a, amt, out, 0);
        }
        return out;
    }

    public static double getNodeReach(EntityPlayer player) {
        return player != null && player.isCreative() ? 5.0D : 4.5D;
    }

    @Nullable
    public static EntityAuraNode findNodeAlongLook(EntityPlayer player, double reach) {
        if (player == null) return null;
        World world = player.world;
        Vec3d eye = player.getPositionEyes(1.0F);
        RayTraceResult blockHit = player.rayTrace(reach, 1.0F);
        double maxSq = (blockHit == null || blockHit.typeOfHit == RayTraceResult.Type.MISS)
                ? reach * reach
                : eye.squareDistanceTo(blockHit.hitVec);

        Vec3d look = player.getLook(1.0F);
        Vec3d end = eye.addVector(look.x * reach, look.y * reach, look.z * reach);

        AxisAlignedBB sweep = player.getEntityBoundingBox()
                .expand(look.x * reach, look.y * reach, look.z * reach)
                .grow(1.0D);

        List<EntityAuraNode> candidates = world.getEntitiesWithinAABB(EntityAuraNode.class, sweep,
                e -> e != null && !e.isDead);
        EntityAuraNode pick = null;
        double best = maxSq;

        for (EntityAuraNode n : candidates) {
            AxisAlignedBB bb = n.getEntityBoundingBox().grow(0.2D);
            RayTraceResult hit = bb.calculateIntercept(eye, end);
            if (hit != null) {
                double dist = eye.squareDistanceTo(hit.hitVec);
                if (dist < best) {
                    best = dist;
                    pick = n;
                }
            }
        }
        return pick;
    }

    public static boolean chargeWandFromNode(ItemStack wand, EntityAuraNode node, @Nullable EntityLivingBase player) {
        if (wand == null || wand.isEmpty() || node == null || node.isDead) return false;
        int cap = getMaxPrimalCharge(wand, player);
        if (cap <= 0) return false;

        AspectList current = getPrimalCharge(wand);
        AspectList nodeAspects = node.getNodeAspects();
        if (nodeAspects == null || nodeAspects.size() <= 0) return false;

        List<Aspect> order = node.getFixedAspectOrder();
        if (order == null || order.isEmpty()) {
            Aspect[] arr = nodeAspects.getAspects();
            order = new ArrayList<>();
            if (arr != null) {
                for (Aspect a : arr) order.add(a);
            }
        }

        for (Aspect a : order) {
            if (a == null) continue;
            if (nodeAspects.getAmount(a) <= 1) continue;

            AspectList primals = new AspectList();
            primals.add(a, 1);
            primals = decomposeToPrimals(primals);
            if (primals.size() <= 0) continue;
            if (!canFitPrimals(current, primals, cap)) continue;

            nodeAspects.reduce(a, 1);
            for (Aspect p : primals.getAspects()) {
                current.add(p, primals.getAmount(p));
            }
            setPrimalCharge(wand, current, player);
            if (player instanceof EntityPlayer) {
                ((EntityPlayer) player).inventory.markDirty();
            }
            node.updateSyncAspects();
            return true;
        }
        return false;
    }

    public static ItemStack isWandInHotbarWithRoom(EntityPlayer player, int amount) {
        for (int i = 0; i != InventoryPlayer.getHotbarSize(); i++) {
            ItemStack wand = player.inventory.mainInventory.get(i);
            if (wand.getItem() instanceof IWand) {
                if (!isWandFullyCharged(wand, player)) {
                    return wand;
                }
            }
        }

        ItemStack wand = player.inventory.offHandInventory.get(0);
        if (wand.getItem() instanceof IWand && !isWandFullyCharged(wand, player)) return wand;

        return ItemStack.EMPTY;

    }

    public static ItemStack isWandInBackpack(EntityPlayer player, int amount) {
        for (int i = 0; i != player.inventory.mainInventory.size(); i++) {
            ItemStack wand = player.inventory.mainInventory.get(i);
            if (wand.getItem() instanceof IWand) {
                if (!isWandFullyCharged(wand, player)) {
                    return wand;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack getHeldWand(EntityPlayer player) {
        if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof IWand)
            return player.getHeldItemMainhand();
        if (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() instanceof IWand)
            return player.getHeldItemOffhand();
        return ItemStack.EMPTY;
    }

    private static int getMaxPrimalCharge(ItemStack wand, @Nullable EntityLivingBase player) {
        if (wand == null || wand.isEmpty() || !(wand.getItem() instanceof IWand)) return 0;
        return ((IWand) wand.getItem()).getMaxCharge(wand, player);
    }

    private static AspectList filterPrimals(AspectList list) {
        AspectList out = new AspectList();
        if (list == null) return out;
        for (Aspect p : Aspect.getPrimalAspects()) {
            int amt = list.getAmount(p);
            if (amt > 0) out.add(p, amt);
        }
        return out;
    }

    private static AspectList clampPrimals(AspectList list, int cap) {
        if (cap <= 0) return list == null ? new AspectList() : list;
        AspectList out = new AspectList();
        if (list == null) return out;
        for (Aspect p : Aspect.getPrimalAspects()) {
            int amt = list.getAmount(p);
            if (amt > 0) out.add(p, Math.min(cap, amt));
        }
        return out;
    }

    private static boolean canFitPrimals(AspectList current, AspectList add, int cap) {
        if (cap <= 0) return false;
        for (Aspect p : add.getAspects()) {
            int want = add.getAmount(p);
            if (want <= 0) continue;
            if (current.getAmount(p) + want > cap) return false;
        }
        return true;
    }

    private static Aspect findLowestPrimalWithRoom(AspectList current, int cap) {
        Aspect pick = null;
        int best = Integer.MAX_VALUE;
        for (Aspect p : Aspect.getPrimalAspects()) {
            int amt = current.getAmount(p);
            if (amt >= cap) continue;
            if (amt < best) {
                best = amt;
                pick = p;
            }
        }
        return pick;
    }

    private static void addAspectToPrimals(Aspect aspect, int amount, AspectList out, int depth) {
        if (aspect == null || amount <= 0 || out == null || depth > 10) return;
        if (aspect.isPrimal()) {
            out.add(aspect, amount);
            return;
        }
        Aspect[] comps = aspect.getComponents();
        if (comps == null || comps.length == 0) return;
        for (Aspect c : comps) {
            addAspectToPrimals(c, amount, out, depth + 1);
        }
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (stack.hasTagCompound()) return stack.getTagCompound();
        NBTTagCompound tag = new NBTTagCompound();
        stack.setTagCompound(tag);
        return tag;
    }

}
