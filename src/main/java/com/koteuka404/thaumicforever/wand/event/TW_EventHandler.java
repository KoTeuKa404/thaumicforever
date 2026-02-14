package com.koteuka404.thaumicforever.wand.event;

import com.koteuka404.thaumicforever.wand.api.item.wand.IStaff;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;
import com.koteuka404.thaumicforever.wand.entity.EntityVisOrb;
import com.koteuka404.thaumicforever.ThaumicForever;
import com.koteuka404.thaumicforever.ModBlocks;
import com.koteuka404.thaumicforever.ModGuiHandler;
import com.koteuka404.thaumicforever.wand.tile.TileArcaneWorkbenchNew;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import com.koteuka404.thaumicforever.wand.wand.TW_Wands;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.List;

@EventBusSubscriber(modid = ThaumicForever.MODID)
public class TW_EventHandler {

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent e) {
        if (!e.getEntityLiving().world.isRemote) {
            AspectList aspects = AspectHelper.getEntityAspects(e.getEntityLiving());
            if (aspects != null && aspects.visSize() > 0 && e.getEntityLiving().getEntityWorld().rand.nextBoolean()) {
                EntityVisOrb orb = new EntityVisOrb(e.getEntityLiving().getEntityWorld(), e.getEntityLiving().posX, e.getEntityLiving().posY, e.getEntityLiving().posZ, 1 + e.getEntityLiving().world.rand.nextInt(1 + Math.floorDiv(aspects.visSize(), 20)));
                e.getEntityLiving().getEntityWorld().spawnEntity(orb);
            }

        }
    }

    @SubscribeEvent
    public static void createArcaneWorkbench(PlayerInteractEvent.RightClickBlock e) {
        EntityPlayer player = e.getEntityPlayer();
        for (EnumHand hand : EnumHand.values()) {
            ItemStack wand = player.getHeldItem(hand);
            if (wand.getItem() instanceof IWand) {
                if (e.getFace() == null)
                    return;

                if (!player.canPlayerEdit(e.getPos(), e.getFace(), player.getHeldItem(hand))) {
                    return;
                }

                if (player.isSneaking()) {
                    return;
                }

                player.swingArm(hand);
                for (IDustTrigger trigger : IDustTrigger.triggers) {
                    IDustTrigger.Placement place = trigger.getValidFace(player.world, player, e.getPos(), e.getFace());
                    if (place != null) {
                        trigger.execute(player.world, player, e.getPos(), place, e.getFace());
                        if (player.world.isRemote) {
                            doSparkles(player, player.world, e.getPos(), e.getPos().getX(), e.getPos().getY(), e.getPos().getZ(), hand, trigger, place);
                            break;
                        }
                    }
                }

                if (player.world.getBlockState(e.getPos()).getBlock() == BlocksTC.tableWood) {
                    player.world.setBlockState(e.getPos(), ModBlocks.WAND_WORKBENCH.getDefaultState());
                    player.world.setTileEntity(e.getPos(), new TileArcaneWorkbenchNew());
                    TileArcaneWorkbenchNew taw = (TileArcaneWorkbenchNew) player.world.getTileEntity(e.getPos());
                    if ((taw != null) && !(wand.getItem() instanceof IStaff)) {
                        taw.inventoryCraft.setInventorySlotContents(15, wand.copy());
                        wand.shrink(1);
                        taw.markDirty();
                    }
                    player.inventory.markDirty();
                    player.world.notifyBlockUpdate(e.getPos(), player.world.getBlockState(e.getPos()), player.world.getBlockState(e.getPos()), 3);
                    FMLCommonHandler.instance().firePlayerCraftingEvent(player, new ItemStack(BlocksTC.arcaneWorkbench), new InventoryFake(1));
                }
            }
        }
    }

    @SubscribeEvent
    public static void openWandWorkbenchFromCharger(PlayerInteractEvent.RightClickBlock e) {
        if (e.getWorld().isRemote) return;
        if (e.getHand() != EnumHand.MAIN_HAND) return;
        if (e.getWorld().getBlockState(e.getPos()).getBlock() != BlocksTC.arcaneWorkbenchCharger) return;

        BlockPos benchPos = e.getPos().down();
        if (e.getWorld().getBlockState(benchPos).getBlock() != ModBlocks.WAND_WORKBENCH) return;
        if (!(e.getWorld().getTileEntity(benchPos) instanceof TileArcaneWorkbenchNew)) return;

        e.setCanceled(true);
        e.setCancellationResult(EnumActionResult.SUCCESS);
        e.getEntityPlayer().openGui(ThaumicForever.instance, ModGuiHandler.GUI_WAND_WORKBENCH, e.getWorld(),
                benchPos.getX(), benchPos.getY(), benchPos.getZ());
    }

    public static void doSparkles(EntityPlayer player, World world, BlockPos pos, float hitX, float hitY, float hitZ, EnumHand hand, IDustTrigger trigger, IDustTrigger.Placement place) {
        Vec3d v1 = EntityUtils.posToHand(player, hand);
        Vec3d v2 = new Vec3d(pos);
        v2 = v2.addVector(0.5, 0.5, 0.5);
        v2 = v2.subtract(v1);
        for (int cnt = 50, a = 0; a < cnt; ++a) {
            boolean floaty = a < cnt / 3;
            float r = MathHelper.getInt(world.rand, 255, 255) / 255.0f;
            float g = MathHelper.getInt(world.rand, 189, 255) / 255.0f;
            float b = MathHelper.getInt(world.rand, 64, 255) / 255.0f;
            FXDispatcher.INSTANCE.drawSimpleSparkle(world.rand, v1.x, v1.y, v1.z, v2.x / 6.0 + world.rand.nextGaussian() * 0.05, v2.y / 6.0 + world.rand.nextGaussian() * 0.05 + (floaty ? 0.05 : 0.15), v2.z / 6.0 + world.rand.nextGaussian() * 0.05, 0.5f, r, g, b, world.rand.nextInt(5), floaty ? (0.3f + world.rand.nextFloat() * 0.5f) : 0.85f, floaty ? 0.2f : 0.5f, 16);
        }
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.dust, SoundCategory.PLAYERS, 0.33f, 1.0f + (float) world.rand.nextGaussian() * 0.05f, false);
        List<BlockPos> sparkles = trigger.sparkle(world, player, pos, place);
        if (sparkles != null) {
            Vec3d v3 = new Vec3d(pos).addVector(hitX, hitY, hitZ);
            for (BlockPos p : sparkles) {
                FXDispatcher.INSTANCE.drawBlockSparkles(p, v3);
            }
        }
    }

    private static boolean getDrainSpeed(EntityPlayer player, ItemStack stack) {
        if (((IWand) stack.getItem()).getCap(stack) == TW_Wands.capBrass)
            return player.getEntityWorld().getTotalWorldTime() % 10 == 0;
        else
            return player.getEntityWorld().getTotalWorldTime() % 20 == 0;
    }

    @SubscribeEvent
    public static void rechargeWands(TickEvent.PlayerTickEvent e) {
        return;
    }
}
