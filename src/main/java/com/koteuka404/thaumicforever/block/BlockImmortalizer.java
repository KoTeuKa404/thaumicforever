package com.koteuka404.thaumicforever.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import thaumcraft.api.items.ItemsTC;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import com.koteuka404.thaumicforever.tile.TileEntityImmortalizer;

public class BlockImmortalizer extends Block implements ITileEntityProvider {

    public static final net.minecraft.block.properties.PropertyInteger STATE = net.minecraft.block.properties.PropertyInteger.create("state", 0, 3);

    public BlockImmortalizer() {
        super(Material.IRON);
        this.setUnlocalizedName("immortalizer");
        this.setRegistryName("immortalizer");
        this.setHardness(15.0F);
        this.setResistance(40.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(STATE, 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STATE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STATE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(STATE, meta);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
    
            if (tile instanceof TileEntityImmortalizer) {
                TileEntityImmortalizer immortalizer = (TileEntityImmortalizer) tile;
                boolean automatedActivator = isAutomatedActivator(player);
                EnumHand usedHand = hand;
                ItemStack heldItem = player.getHeldItem(usedHand);

                if (heldItem.isEmpty() || heldItem.getItem() != ItemsTC.primordialPearl) {
                    EnumHand other = (hand == EnumHand.MAIN_HAND) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                    ItemStack otherStack = player.getHeldItem(other);
                    if (!otherStack.isEmpty() && otherStack.getItem() == ItemsTC.primordialPearl) {
                        usedHand = other;
                        heldItem = otherStack;
                    }
                }

                if (heldItem.isEmpty()) {
                    if (automatedActivator) {
                        return false;
                    }
                    return true;
                }
    
                if (heldItem.getItem() == ItemsTC.primordialPearl) {
    
                    if (TileEntityImmortalizer.REQUIRED_ASPECT == null) {
                        if (!automatedActivator) {
                            player.sendStatusMessage(
                                new net.minecraft.util.text.TextComponentString("Аспект CAELES не доступний."), true
                            );
                        }
                        if (automatedActivator) {
                            return false;
                        }
                        return true;
                    }
    
                    UUID targetUuid = resolveImmortalityTarget(world, pos, player);
                    if (immortalizer.activateImmortality(targetUuid)) {
                        heldItem.shrink(1);
                        player.setHeldItem(usedHand, heldItem);
                        immortalizer.markDirty();

                        world.playSound(null, pos, net.minecraft.init.SoundEvents.BLOCK_END_PORTAL_SPAWN,
                            net.minecraft.util.SoundCategory.BLOCKS, 1.0F, 1.0F);
                    } else {
                        boolean alreadyCharged = immortalizer.hasImmortalityFor(targetUuid);
                        if (!automatedActivator) {
                            if (alreadyCharged) {
                                player.sendStatusMessage(
                                    new net.minecraft.util.text.TextComponentString("Ціль уже заряджена безсмертям."),
                                    true
                                );
                            } else {
                                String aspectTag = TileEntityImmortalizer.REQUIRED_ASPECT != null
                                    ? TileEntityImmortalizer.REQUIRED_ASPECT.getTag()
                                    : "null";
                                player.sendStatusMessage(
                                    new net.minecraft.util.text.TextComponentString(
                                        "Недостатньо есенції: " + immortalizer.getStoredEssentia() + "/" + TileEntityImmortalizer.ESSENTIA_COST + " (" + aspectTag + ")"
                                    ),
                                    true
                                );
                            }
                        }

                        // Important for golem Use seal:
                        // if pearl insertion failed due missing essentia, report interaction as failed
                        // so golem keeps retrying later instead of treating task as completed.
                        if (automatedActivator && !alreadyCharged) {
                            return false;
                        }
                    }
    
                    return true;
                }
            }
        }
        return true;
    }

    private UUID resolveImmortalityTarget(World world, BlockPos pos, EntityPlayer activator) {
        if (!isAutomatedActivator(activator)) {
            return activator.getUniqueID();
        }

        UUID owner = resolveOwnerFromNearbyThaumcraftGolem(world, pos);
        return owner != null ? owner : activator.getUniqueID();
    }

    private UUID resolveOwnerFromNearbyThaumcraftGolem(World world, BlockPos pos) {
        AxisAlignedBB box = new AxisAlignedBB(pos).grow(8.0D);
        List<Entity> candidates = world.getEntitiesWithinAABB(Entity.class, box);
        Entity nearest = null;
        double bestDistanceSq = Double.MAX_VALUE;

        for (Entity entity : candidates) {
            String name = entity.getClass().getName().toLowerCase();
            if (!name.contains("thaumcraft") || !name.contains("golem")) {
                continue;
            }

            UUID owner = extractOwnerUuid(entity);
            if (owner == null) {
                continue;
            }

            double d = entity.getDistanceSq(pos);
            if (d < bestDistanceSq) {
                bestDistanceSq = d;
                nearest = entity;
            }
        }

        return nearest != null ? extractOwnerUuid(nearest) : null;
    }

    private UUID extractOwnerUuid(Entity entity) {
        String[] methodCandidates = new String[] {
                "getOwnerUUID", "getOwnerId", "getOwner", "getOwnerEntity", "func_184753_b"
        };
        for (String methodName : methodCandidates) {
            try {
                Method m = entity.getClass().getMethod(methodName);
                Object result = m.invoke(entity);
                UUID uuid = toUuid(result);
                if (uuid != null) return uuid;
            } catch (Exception ignored) {
            }
        }

        String[] fieldCandidates = new String[] {
                "ownerUUID", "ownerId", "owner", "field_184754_bv"
        };
        for (String fieldName : fieldCandidates) {
            try {
                Field f = entity.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                Object result = f.get(entity);
                UUID uuid = toUuid(result);
                if (uuid != null) return uuid;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private boolean isAutomatedActivator(EntityPlayer player) {
        if (player instanceof FakePlayer) {
            return true;
        }
        String name = player.getClass().getName().toLowerCase();
        return name.contains("fake") || name.contains("golem");
    }

    private UUID toUuid(Object value) {
        if (value == null) return null;
        if (value instanceof UUID) return (UUID) value;
        if (value instanceof EntityPlayer) return ((EntityPlayer) value).getUniqueID();
        if (value instanceof String) {
            try {
                return UUID.fromString((String) value);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
    



    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityImmortalizer();
    }
}
