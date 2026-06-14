package com.koteuka404.thaumicforever.golemcore;

import com.koteuka404.thaumicforever.api.golemcore.GolemCoreHelper;
import com.koteuka404.thaumicforever.api.golemcore.GolemCoreRegistry;
import com.koteuka404.thaumicforever.api.golemcore.IGolemCore;
import com.koteuka404.thaumicforever.api.golemcore.IGolemCoreItem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.golems.IGolemAPI;

public class GolemCoreEventHandler {
    @SubscribeEvent
    public void onGolemTick(LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof IGolemAPI)) return;

        IGolemAPI golem = (IGolemAPI) event.getEntityLiving();
        IGolemCore core = GolemCoreHelper.getActiveCore(golem);
        if (core != null) {
            core.onGolemTick(golem);
            if (!GoliathGolemCore.ID.equals(core.getId())) {
                GoliathGolemCore.clearGoliathEffects(event.getEntityLiving());
            }
            if (!SwiftGolemCore.ID.equals(core.getId())) {
                SwiftGolemCore.clearSwiftEffects(event.getEntityLiving());
            }
        } else if (GoliathGolemCore.hasGoliathHealthModifier(event.getEntityLiving())) {
            GoliathGolemCore.applyGoliathSize(event.getEntityLiving());
            SwiftGolemCore.clearSwiftEffects(event.getEntityLiving());
        } else {
            GoliathGolemCore.restoreGoliathSize(event.getEntityLiving());
            SwiftGolemCore.clearSwiftEffects(event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public void onGolemHurt(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof IGolemAPI) {
            IGolemAPI golem = (IGolemAPI) event.getEntityLiving();
            IGolemCore core = GolemCoreHelper.getActiveCore(golem);
            if (core != null) {
                core.onGolemHurt(golem, event);
            }
        }

        Entity source = event.getSource().getTrueSource();
        if (source instanceof IGolemAPI && event.getEntityLiving() != source) {
            IGolemAPI golem = (IGolemAPI) source;
            IGolemCore core = GolemCoreHelper.getActiveCore(golem);
            if (core != null) {
                core.onGolemAttack(golem, event.getEntityLiving(), event.getSource(), event.getAmount());
            }
        }
    }

    @SubscribeEvent
    public void onGolemDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof IGolemAPI)) return;

        IGolemAPI golem = (IGolemAPI) event.getEntityLiving();
        IGolemCore core = GolemCoreHelper.getActiveCore(golem);
        if (core != null) {
            core.onGolemDeath(golem, event.getSource());
        }
    }

    @SubscribeEvent
    public void onInstallCore(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof IGolemAPI)) return;

        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = player.getHeldItem(event.getHand());
        if (stack.isEmpty() || !(stack.getItem() instanceof IGolemCoreItem)) return;

        IGolemAPI golem = (IGolemAPI) event.getTarget();
        IGolemCoreItem coreItem = (IGolemCoreItem) stack.getItem();
        ResourceLocation coreId = coreItem.getGolemCoreId(stack);
        if (!GolemCoreRegistry.contains(coreId)) {
            if (!player.world.isRemote) {
                player.sendMessage(new TextComponentTranslation("thaumicforever.golem_core.invalid"));
            }
            event.setCancellationResult(EnumActionResult.FAIL);
            event.setCanceled(true);
            return;
        }

        if (GolemCoreHelper.hasActiveCore(golem) && !player.isSneaking()) {
            if (!player.world.isRemote) {
                player.sendMessage(new TextComponentTranslation("thaumicforever.golem_core.already_installed"));
            }
            event.setCancellationResult(EnumActionResult.FAIL);
            event.setCanceled(true);
            return;
        }

        if (!coreItem.canInstallCore(player, golem, stack)) {
            event.setCancellationResult(EnumActionResult.FAIL);
            event.setCanceled(true);
            return;
        }

        if (!player.world.isRemote && GolemCoreHelper.setActiveCore(golem, coreId)) {
            coreItem.onCoreInstalled(player, golem, stack);
            player.sendMessage(new TextComponentTranslation("thaumicforever.golem_core.installed"));
        }

        event.setCancellationResult(EnumActionResult.SUCCESS);
        event.setCanceled(true);
    }
}
