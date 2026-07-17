package com.koteuka404.thaumicforever.network;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class PacketRequestFutureDrops implements IMessage {

    private static final int SAMPLE_ROLLS = 1024;
    private static final int MAX_SHOWN_DROPS = 12;
    private static final int NO_NEW_DROP_STOP = 512;

    private int entityId;

    public PacketRequestFutureDrops() {}

    public PacketRequestFutureDrops(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
    }

    public static class Handler implements IMessageHandler<PacketRequestFutureDrops, IMessage> {

        private static final Method GET_LOOT_TABLE = ReflectionHelper.findMethod(
                EntityLiving.class,
                "getLootTable",
                "func_184647_J"
        );

        @Override
        public IMessage onMessage(PacketRequestFutureDrops message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            player.getServerWorld().addScheduledTask(() -> {
                handle(message, player);
            });

            return null;
        }

        private void handle(PacketRequestFutureDrops message, EntityPlayerMP player) {
            WorldServer world = player.getServerWorld();
            Entity entity = world.getEntityByID(message.entityId);

            if (!(entity instanceof EntityLiving)) {
                NetworkHandler.INSTANCE.sendTo(new PacketFutureDrops(message.entityId, new ArrayList<>()), player);
                return;
            }

            if (entity.getDistance(player) > 28.0F) {
                return;
            }

            EntityLiving mob = (EntityLiving) entity;
            ResourceLocation lootTableId = getLootTable(mob);

            if (lootTableId == null) {
                NetworkHandler.INSTANCE.sendTo(new PacketFutureDrops(message.entityId, new ArrayList<>()), player);
                return;
            }

            LootTable lootTable = world.getLootTableManager().getLootTableFromLocation(lootTableId);

            LootContext context = new LootContext.Builder(world)
                    .withLootedEntity((EntityLivingBase) mob)
                    .withPlayer(player)
                    .withDamageSource(DamageSource.causePlayerDamage(player))
                    .withLuck(player.getLuck())
                    .build();

                    List<ItemStack> result = new ArrayList<>();
                    Set<String> seen = new HashSet<>();
                    
                    int noNewDropRolls = 0;
                    
                    for (int i = 0; i < SAMPLE_ROLLS && result.size() < MAX_SHOWN_DROPS; i++) {
                        List<ItemStack> generated;
                    
                        try {
                            generated = lootTable.generateLootForPools(world.rand, context);
                        } catch (RuntimeException ignored) {
                            break;
                        }
                    
                        boolean addedSomething = false;
                    
                        for (ItemStack stack : generated) {
                            if (stack.isEmpty()) {
                                continue;
                            }
                    
                            String key = keyOf(stack);
                    
                            if (!seen.add(key)) {
                                continue;
                            }
                    
                            result.add(stack.copy());
                            addedSomething = true;
                    
                            if (result.size() >= MAX_SHOWN_DROPS) {
                                break;
                            }
                        }
                    
                        if (addedSomething) {
                            noNewDropRolls = 0;
                        } else {
                            noNewDropRolls++;
                        }
                    
                        if (noNewDropRolls >= NO_NEW_DROP_STOP) {
                            break;
                        }
                    }

            NetworkHandler.INSTANCE.sendTo(new PacketFutureDrops(message.entityId, result), player);
        }

        private static ResourceLocation getLootTable(EntityLiving entity) {
            try {
                return (ResourceLocation) GET_LOOT_TABLE.invoke(entity);
            } catch (Exception ignored) {
                return null;
            }
        }

        private static String keyOf(ItemStack stack) {
            ResourceLocation registryName = stack.getItem().getRegistryName();
            NBTTagCompound tag = stack.getTagCompound();

            return (registryName == null ? "" : registryName.toString())
                    + ":"
                    + stack.getMetadata()
                    + ":"
                    + (tag == null ? "" : tag.toString());
        }
    }
}