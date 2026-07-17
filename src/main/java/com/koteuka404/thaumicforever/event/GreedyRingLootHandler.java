package com.koteuka404.thaumicforever.event;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.koteuka404.thaumicforever.item.ItemGreedyRing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class GreedyRingLootHandler {

    private static final Method GET_LOOT_TABLE = ReflectionHelper.findMethod(
            EntityLiving.class,
            "getLootTable",
            "func_184647_J"
    );

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDrops(LivingDropsEvent event) {
        Entity trueSource = event.getSource().getTrueSource();

        if (!(trueSource instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) trueSource;

        if (!ItemGreedyRing.isEquipped(player)) {
            return;
        }

        EntityLivingBase mob = event.getEntityLiving();

        if (mob.world.isRemote || !(mob.world instanceof WorldServer)) {
            return;
        }

        if (!(mob instanceof EntityLiving)) {
            return;
        }

        ResourceLocation lootTableId = getLootTable((EntityLiving) mob);

        if (lootTableId == null) {
            return;
        }

        WorldServer world = (WorldServer) mob.world;
        LootTable lootTable = world.getLootTableManager().getLootTableFromLocation(lootTableId);

        LootContext context = new LootContext.Builder(world)
                .withLootedEntity(mob)
                .withPlayer(player)
                .withDamageSource(event.getSource())
                .withLuck(player.getLuck())
                .build();

        Set<DropKey> alreadyDropped = new HashSet<>();

        for (EntityItem drop : event.getDrops()) {
            ItemStack stack = drop.getItem();

            if (!stack.isEmpty()) {
                alreadyDropped.add(DropKey.of(stack));
            }
        }

        int extraRolls = ItemGreedyRing.getExtraLootRolls(player);

        for (int i = 0; i < extraRolls; i++) {
            List<ItemStack> extraLoot;

            try {
                extraLoot = lootTable.generateLootForPools(world.rand, context);
            } catch (RuntimeException ignored) {
                return;
            }

            for (ItemStack stack : extraLoot) {
                if (stack.isEmpty()) {
                    continue;
                }

                DropKey key = DropKey.of(stack);

                if (!alreadyDropped.add(key)) {
                    continue;
                }

                EntityItem item = new EntityItem(
                        world,
                        mob.posX,
                        mob.posY,
                        mob.posZ,
                        stack.copy()
                );

                event.getDrops().add(item);
            }
        }
    }

    private static ResourceLocation getLootTable(EntityLiving entity) {
        try {
            return (ResourceLocation) GET_LOOT_TABLE.invoke(entity);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static final class DropKey {
        private final String itemId;
        private final int metadata;
        private final String nbt;

        private DropKey(String itemId, int metadata, String nbt) {
            this.itemId = itemId;
            this.metadata = metadata;
            this.nbt = nbt;
        }

        static DropKey of(ItemStack stack) {
            ResourceLocation registryName = stack.getItem().getRegistryName();
            NBTTagCompound tag = stack.getTagCompound();

            return new DropKey(
                    registryName == null ? "" : registryName.toString(),
                    stack.getMetadata(),
                    tag == null ? "" : tag.toString()
            );
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DropKey)) {
                return false;
            }

            DropKey other = (DropKey) obj;

            return itemId.equals(other.itemId)
                    && metadata == other.metadata
                    && nbt.equals(other.nbt);
        }

        @Override
        public int hashCode() {
            int result = itemId.hashCode();
            result = 31 * result + metadata;
            result = 31 * result + nbt.hashCode();
            return result;
        }
    }
}