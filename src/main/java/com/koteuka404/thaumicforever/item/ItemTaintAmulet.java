package com.koteuka404.thaumicforever.item;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.koteuka404.thaumicforever.registry.ModItems;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;

@Mod.EventBusSubscriber
public class ItemTaintAmulet extends Item implements IBauble {

    private static final ConcurrentHashMap<UUID, List<EntityLiving>> ignoredMobs = new ConcurrentHashMap<>();
    public static boolean taintAggroProtectionEnabled = true;
    public static boolean taintPoisonImmunityEnabled = true;

    public ItemTaintAmulet() {
        this.setMaxStackSize(1);
        this.setRegistryName("taint_amulet");
        this.setUnlocalizedName("taint_amulet");
        MinecraftForge.EVENT_BUS.register(this); 
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        if (taintPoisonImmunityEnabled) {
            removeTaintPoison(player);
        }

        if (taintAggroProtectionEnabled) {
            ignorePlayer(player);
        }
    }


    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!(entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) entity;

        if (taintPoisonImmunityEnabled && BaublesApi.isBaubleEquipped(player, ModItems.ItemTaintAmulet) >= 0) {
            removeTaintPoison(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        PotionEffect effect = event.getPotionEffect();
        if (effect == null || !hasTaintAmuletImmunity(entity)) return;

        if (isTaintPoison(effect.getPotion())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingAttack(LivingAttackEvent event) {
        if (shouldBlockTaintDamage(event.getEntityLiving(), event.getSource())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingHurt(LivingHurtEvent event) {
        if (shouldBlockTaintDamage(event.getEntityLiving(), event.getSource())) {
            event.setCanceled(true);
        }
    }

    private static boolean shouldBlockTaintDamage(EntityLivingBase entity, net.minecraft.util.DamageSource source) {
        if (!hasTaintAmuletImmunity(entity) || source == null) return false;
        return source == DamageSourceThaumcraft.taint || "taint".equals(source.damageType);
    }

    private static boolean hasTaintAmuletImmunity(EntityLivingBase entity) {
        if (!taintPoisonImmunityEnabled || !(entity instanceof EntityPlayer)) return false;
        return BaublesApi.isBaubleEquipped((EntityPlayer) entity, ModItems.ItemTaintAmulet) >= 0;
    }

    private static void removeTaintPoison(EntityPlayer player) {
        for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
            Potion potion = effect.getPotion();
            if (isTaintPoison(potion)) {
                player.removePotionEffect(potion);
            }
        }
    }

    private static boolean isTaintPoison(Potion potion) {
        if (potion == null) return false;
        if (PotionFluxTaint.instance != null && potion == PotionFluxTaint.instance) return true;

        ResourceLocation id = Potion.REGISTRY.getNameForObject(potion);
        if (id != null) {
            String key = id.toString().toLowerCase(java.util.Locale.ROOT);
            if (key.contains("fluxtaint") || key.contains("taint_poison") || key.contains("taintpoison")) {
                return true;
            }
        }

        String name = potion.getName();
        if (name != null) {
            String key = name.toLowerCase(java.util.Locale.ROOT);
            return key.contains("flux_taint") || key.contains("fluxtaint")
                    || key.contains("taint_poison") || key.contains("taintpoison");
        }

        return false;
    }

    private static void ignorePlayer(EntityPlayer player) {
        if (!taintAggroProtectionEnabled) return;
        UUID playerId = player.getUniqueID();

        List<EntityLiving> nearbyMobs = player.world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(
                player.posX - 20, player.posY - 10, player.posZ - 20,
                player.posX + 20, player.posY + 10, player.posZ + 20
        ));

        ignoredMobs.put(playerId, nearbyMobs);

        for (EntityLiving mob : nearbyMobs) {
            if (isTaintedOrChampion(mob) && mob.getAttackTarget() == player) {
                mob.setAttackTarget(null);
                mob.setRevengeTarget(null);
                removeAggressiveAITasks(mob);
                clearHostileMemory(mob);

                if (mob instanceof EntityCreature) {
                    ((EntityCreature) mob).setAttackTarget(null);
                }
            }
        }
    }

    private static void restoreAggro(EntityPlayer player) {
        UUID playerId = player.getUniqueID();
        if (!ignoredMobs.containsKey(playerId)) return;

        List<EntityLiving> mobs = ignoredMobs.get(playerId);
        for (EntityLiving mob : mobs) {
            if (mob != null && !mob.isDead) {
                mob.setAttackTarget(player);
                mob.setRevengeTarget(player);

                if (mob instanceof EntityCreature) {
                    EntityCreature creature = (EntityCreature) mob;
                    creature.setAttackTarget(player);

                    if (creature.targetTasks.taskEntries.isEmpty()) {
                        creature.targetTasks.addTask(1, new net.minecraft.entity.ai.EntityAINearestAttackableTarget<>(creature, EntityPlayer.class, true));
                    }
                }
            }
        }
        ignoredMobs.remove(playerId);
    }

    private static void removeAggressiveAITasks(EntityLiving mob) {
        if (mob != null && mob.targetTasks != null) {
            mob.targetTasks.taskEntries.clear();
        }
    }

    private static void clearHostileMemory(EntityLiving mob) {
        if (mob != null) {
            mob.getEntityData().removeTag("PersistentAnger");
            mob.getEntityData().removeTag("AngerTime");
            mob.getEntityData().removeTag("AngryAt");
        }
    }

    private static boolean isTaintedOrChampion(EntityLivingBase mob) {
        return mob instanceof ITaintedMob ||
               (mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD) != null &&
                mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() > -1.0);
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase entity) {}

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            restoreAggro((EntityPlayer) entity);
        }
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase entity) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase entity) {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("Wearing this amulet protects you from " + TextFormatting.DARK_PURPLE + "Taint poisoning." + TextFormatting.GRAY);
        tooltip.add(TextFormatting.DARK_PURPLE + "Taint" + TextFormatting.GRAY + " mobs will ignore you.");
        tooltip.add(TextFormatting.GRAY + "Aggro Protection: " + (taintAggroProtectionEnabled ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled"));
        tooltip.add(TextFormatting.GRAY + "Poison Immunity: " + (taintPoisonImmunityEnabled ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled"));
    }
}
