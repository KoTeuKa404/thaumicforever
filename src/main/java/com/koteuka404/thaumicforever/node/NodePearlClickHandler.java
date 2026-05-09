package com.koteuka404.thaumicforever.node;

import com.koteuka404.thaumicforever.registry.ModItems;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import com.koteuka404.thaumicforever.entity.AuraNodeEntity;
import com.koteuka404.thaumicforever.entity.EntityAuraNode;

public final class NodePearlClickHandler {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new NodePearlClickHandler());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickItem(PlayerInteractEvent.RightClickItem e) {
        if (tryUsePhialOnAuraNode(e.getWorld(), e.getEntityPlayer(), e.getHand(), e.getItemStack())) {
            e.setCancellationResult(EnumActionResult.SUCCESS);
            e.setCanceled(true);
            return;
        }
        if (tryUsePearlOnNode(e.getWorld(), e.getEntityPlayer(), e.getHand(), e.getItemStack())) {
            e.setCancellationResult(EnumActionResult.SUCCESS);
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
        if (tryUsePhialOnAuraNode(e.getWorld(), e.getEntityPlayer(), e.getHand(), e.getItemStack())) {
            e.setCancellationResult(EnumActionResult.SUCCESS);
            e.setCanceled(true);
            return;
        }
        if (tryUsePearlOnNode(e.getWorld(), e.getEntityPlayer(), e.getHand(), e.getItemStack())) {
            e.setCancellationResult(EnumActionResult.SUCCESS);
            e.setCanceled(true);
        }
    }

    private boolean tryUsePhialOnAuraNode(World world, EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (world.isRemote) return false;
        if (stack.isEmpty()) return false;
        if (stack.getItem() != ItemsTC.phial && stack.getItem() != ModItems.AuraPhial) return false;

        AuraNodeEntity node = pickNearestAuraNode(world, player, 2.0D);
        if (node == null) return false;

        return node.tryUsePhial(player, hand, stack);
    }

    private boolean tryUsePearlOnNode(World world, EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (world.isRemote) return false;
        if (stack.isEmpty() || stack.getItem() != ItemsTC.primordialPearl) return false;
        if (stack.isItemDamaged()) return false;

        double reach = player.isCreative() ? 5.0D : 4.5D;

        Vec3d eye  = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F);
        Vec3d end  = eye.addVector(look.x * reach, look.y * reach, look.z * reach);
        RayTraceResult blockHit = world.rayTraceBlocks(eye, end, false, true, false);
        double maxSq = (blockHit == null || blockHit.typeOfHit == RayTraceResult.Type.MISS)
                ? reach * reach
                : eye.squareDistanceTo(blockHit.hitVec);
        EntityAuraNode node = pickNodeAlongLook(world, player, reach, maxSq);
        if (node == null) return false;

        boolean researched = false;
        IPlayerKnowledge kn = ThaumcraftCapabilities.getKnowledge(player);
        if (kn != null) {
            researched = kn.isResearchKnown("PRIMALNODE") || kn.isResearchKnown("primalnode");
        }

        node.applyPrimordialManipulation(player, researched);

        if (!player.capabilities.isCreativeMode) stack.shrink(1);

        return true;
    }

    private EntityAuraNode pickNodeAlongLook(World world, EntityPlayer player, double reach, double maxDistSq) {
        Vec3d eye  = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F);
        Vec3d end  = eye.addVector(look.x * reach, look.y * reach, look.z * reach);

        AxisAlignedBB sweep = player.getEntityBoundingBox()
                .expand(look.x * reach, look.y * reach, look.z * reach)
                .grow(1.0D);

        List<EntityAuraNode> candidates = world.getEntitiesWithinAABB(EntityAuraNode.class, sweep,
                e -> e != null && !e.isDead);

        EntityAuraNode pick = null;
        double best = maxDistSq;

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

    private AuraNodeEntity pickNearestAuraNode(World world, EntityPlayer player, double radius) {
        AxisAlignedBB box = player.getEntityBoundingBox().grow(radius);
        List<AuraNodeEntity> nodes = world.getEntitiesWithinAABB(AuraNodeEntity.class, box, n -> n != null && !n.isDead);
        AuraNodeEntity pick = null;
        double best = radius * radius;
        for (AuraNodeEntity n : nodes) {
            double d = player.getDistanceSq(n);
            if (d <= best) {
                best = d;
                pick = n;
            }
        }
        return pick;
    }
}
